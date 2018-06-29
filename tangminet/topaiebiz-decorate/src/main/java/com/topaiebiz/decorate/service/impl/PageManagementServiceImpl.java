package com.topaiebiz.decorate.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.nebulapaas.common.redis.aop.JedisOperation;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.decorate.ComponentBuilder;
import com.topaiebiz.decorate.component.ItemListVO;
import com.topaiebiz.decorate.component.ItemVO;
import com.topaiebiz.decorate.constant.Constant;
import com.topaiebiz.decorate.dao.ContentDao;
import com.topaiebiz.decorate.dao.PageComponentDao;
import com.topaiebiz.decorate.dao.PageDetailDao;
import com.topaiebiz.decorate.dto.*;
import com.topaiebiz.decorate.entity.ComponentContentEntity;
import com.topaiebiz.decorate.entity.PageComponentEntity;
import com.topaiebiz.decorate.entity.PageDetailEntity;
import com.topaiebiz.decorate.exception.DecorateExcepionEnum;
import com.topaiebiz.decorate.service.PageManagementService;
import com.topaiebiz.file.api.FileUploadApi;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.dto.sku.GoodsDecorateDTO;
import com.topaiebiz.system.util.SecurityContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

@Service
@Slf4j
public class PageManagementServiceImpl extends ServiceImpl<PageDetailDao, PageDetailEntity> implements PageManagementService {

    @Autowired
    private PageDetailDao pageDetailDao;

    @Autowired
    private PageComponentDao pageComponentDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private FileUploadApi fileUploadApi;

    @Autowired
    private ComponentBuilder componentBuilder;

    @Override
    @Transactional
    public Long newPage(PageDetailDto pageDetailDto) {
        //页面名称不可空
        if (StringUtils.isEmpty(pageDetailDto.getPageName())) {
            throw new GlobalException(DecorateExcepionEnum.PAGENAME_NOT_NULL);
        }
        //页面URL后缀不可空，需要用来生成二维码
        String suffixUrl = pageDetailDto.getSuffixUrl();
        if (StringUtils.isEmpty(suffixUrl)) {
            throw new GlobalException(DecorateExcepionEnum.SUFFIXURL_NOT_NULL);
        }
        PageDetailEntity entity = new PageDetailEntity();
        entity.setPageName(pageDetailDto.getPageName());
        entity.setSuffixUrl(suffixUrl);
        entity.setPageUrl(Constant.PAGE_URL_PREFIX + suffixUrl);
        //设置页面URL后缀CRC32值
        CRC32 crc32 = new CRC32();
        crc32.update(suffixUrl.getBytes());
        entity.setCRC32(crc32.getValue());
        entity.setMemo(pageDetailDto.getMemo());
        entity.setCreatedTime(new Date());
        entity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        //页面默认不在线
        entity.setStatus(Constant.NOT_ONLINE);
        //手动新增的页面都是可以删除的
        entity.setType(Constant.CAN_BE_DELETED);
        //根据页面地址生成二维码
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.MARGIN, 0);
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(pageDetailDto.getSuffixUrl(), BarcodeFormat.QR_CODE,
                    Constant.QRCODE_WIDTH, Constant.QRCODE_HEIGHT, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) == true ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }
            //将图片转成byte[]并上传OSS服务器
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, Constant.PNG_FORMAT, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();//图片数据
            String fileName = UUID.randomUUID().toString().replace("-", "") + "." +
                    Constant.PNG_FORMAT;//图片名称
            String relativePath = fileUploadApi.uploadFile(bytes, fileName);// 二维码图片上传OSS服务器返回的相对路径
            entity.setQrCode(relativePath);
        } catch (WriterException e) {
            log.info("图片写进byteArrayOutputStream失败!!!!!!");
            e.printStackTrace();
        } catch (IOException e) {
            log.info("图片转byte[]失败!!!!!!");
            e.printStackTrace();
        }
        pageDetailDao.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public void modifyPublishTime(PageDetailDto pageDetailDto) {
        if (null == pageDetailDto.getId()) {
            throw new GlobalException(DecorateExcepionEnum.ID_NOT_NULL);
        }
        Date startTime = pageDetailDto.getStartTime();
        Date endTime = pageDetailDto.getEndTime();
        //开始时间或结束时间不可有一个为null
        if (null == startTime || null == endTime) {
            throw new GlobalException(DecorateExcepionEnum.PUBLISHTIME_NOT_NULL);
        }
        //如果上线时间大于或等于下线时间
        if (startTime.compareTo(endTime) > 0) {
            throw new GlobalException(DecorateExcepionEnum.TIME_COMPARE_ERROR);
        }
        PageDetailEntity entity = new PageDetailEntity();
        entity.cleanInit();
        entity.setId(pageDetailDto.getId());
        entity.setStartTime(startTime);
        entity.setEndTime(endTime);
        entity.setLastModifiedTime(new Date());
        entity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        pageDetailDao.updateById(entity);
    }

    @Override
    public PageInfo<PageDetailEntity> pagingQuery(PageDetailDto pageDetailDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(pageDetailDto.getPageNo());
        pagePO.setPageSize(pageDetailDto.getPageSize());
        Page<PageDetailEntity> page = PageDataUtil.buildPageParam(pagePO);
        EntityWrapper<PageDetailEntity> condition = new EntityWrapper<>();
        condition.eq("type", pageDetailDto.getType());
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        if (StringUtils.isNotEmpty(pageDetailDto.getPageName())) {
            condition.like("pageName", pageDetailDto.getPageName());
        }
        if (StringUtils.isNotEmpty(pageDetailDto.getSuffixUrl())) {
            condition.like("pageUrl", pageDetailDto.getSuffixUrl());
        }
        if (null != pageDetailDto.getStatus()) {
            condition.eq("status", pageDetailDto.getStatus());
        }
        List<PageDetailEntity> entities = pageDetailDao.selectPage(page, condition);
        page.setRecords(entities);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    @Transactional
    public void remove(Long id) {
        if (null == id) {
            throw new GlobalException(DecorateExcepionEnum.ID_NOT_NULL);
        }
        //删除页面表中的该条数据
        PageDetailEntity pageDetailEntity = new PageDetailEntity();
        pageDetailEntity.cleanInit();
        pageDetailEntity.setId(id);
        pageDetailEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        pageDetailEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        pageDetailEntity.setLastModifiedTime(new Date());
        pageDetailDao.updateById(pageDetailEntity);
        EntityWrapper<PageComponentEntity> pageComponentCondition = new EntityWrapper<>();
        pageComponentCondition.eq("pageId", id);
        pageComponentCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        //还未删除前先获取该页面下的所有页面组件关联id
        List<PageComponentEntity> pageComponentEntities = pageComponentDao.selectList(pageComponentCondition);
        if (CollectionUtils.isNotEmpty(pageComponentEntities)) {
            //删除页面组件表中的这些数据
            PageComponentEntity pageComponentEntity = new PageComponentEntity();
            pageComponentEntity.cleanInit();
            pageComponentEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
            pageComponentEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            pageComponentEntity.setLastModifiedTime(new Date());
            pageComponentDao.update(pageComponentEntity, pageComponentCondition);
            //删除组件内容表中的这些数据
            List<Long> pageComponentIds = pageComponentEntities.stream().map(PageComponentEntity::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(pageComponentIds)) {
                EntityWrapper<ComponentContentEntity> contentCondition = new EntityWrapper<>();
                contentCondition.in("componentId", pageComponentIds);
                contentCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
                ComponentContentEntity componentContentEntity = new ComponentContentEntity();
                componentContentEntity.cleanInit();
                componentContentEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                componentContentEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
                componentContentEntity.setLastModifiedTime(new Date());
                contentDao.update(componentContentEntity, contentCondition);
            }
        }

    }


    @Override
    @Transactional
    public void copy(Long id) {
        if (null == id) {
            throw new GlobalException(DecorateExcepionEnum.ID_NOT_NULL);
        }
        //复制页面表中的数据
        PageDetailEntity beCopiedPageEntity = pageDetailDao.selectById(id);//被复制的页面entity
        PageDetailEntity pageDetailEntity = new PageDetailEntity();
        BeanUtils.copyProperties(beCopiedPageEntity, pageDetailEntity);
        pageDetailEntity.setId(null);
        pageDetailEntity.setLastModifierId(null);
        pageDetailEntity.setLastModifiedTime(null);
        pageDetailEntity.setStatus(Constant.NOT_ONLINE);
        pageDetailEntity.setType(Constant.CAN_BE_DELETED);
        pageDetailEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        pageDetailEntity.setCreatedTime(new Date());
        pageDetailDao.insert(pageDetailEntity);
        Long pageId = pageDetailEntity.getId();//新页面实例的页面主键id
        //复制页面组件表中的数据
        EntityWrapper<PageComponentEntity> beCopiedComponentCondition = new EntityWrapper<>();
        beCopiedComponentCondition.eq("pageId", id);
        beCopiedComponentCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<PageComponentEntity> beCopiedComponentEntities = pageComponentDao.selectList(beCopiedComponentCondition);
        if (CollectionUtils.isNotEmpty(beCopiedComponentEntities)) {
            //获取所有的要被复制的页面组件主键id集合
            List<Long> beCopiedComponentIds = beCopiedComponentEntities.stream().map(PageComponentEntity::getId).collect(Collectors.toList());
            List<PageComponentEntity> componentEntities = new ArrayList<>();
            for (PageComponentEntity beCopiedComponentEntity : beCopiedComponentEntities) {
                PageComponentEntity componentEntity = new PageComponentEntity();
                BeanUtils.copyProperties(beCopiedComponentEntity, componentEntity);
                componentEntity.setId(null);
                componentEntity.setLastModifierId(null);
                componentEntity.setLastModifiedTime(null);
                componentEntity.setPageId(pageId);
                componentEntity.setCreatedTime(new Date());
                componentEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
                componentEntities.add(componentEntity);
            }
            //批量插入页面组件
            pageComponentDao.insertBatch(componentEntities);
            //获取复制好的页面组件id集合
            List<Long> componentIds = componentEntities.stream().map(PageComponentEntity::getId).collect(Collectors.toList());
            //复制组件内容,无法保证顺序，需要用户自己调整顺序,类型需保持一致
            //先获取要被复制的组件内容
            EntityWrapper<ComponentContentEntity> contentCondition = new EntityWrapper<>();
            contentCondition.in("componentId", beCopiedComponentIds);
            contentCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            List<ComponentContentEntity> beCopiedContentEntities = contentDao.selectList(contentCondition);
            List<ComponentContentEntity> contentEntities = new ArrayList<>();
            //因为页面组件id的集合长度与被复制的组件内容集合长度相同
            for (int i = 0; i < beCopiedContentEntities.size(); i++) {
                ComponentContentEntity contentEntity = new ComponentContentEntity();
                BeanUtils.copyProperties(beCopiedContentEntities.get(i), contentEntity);
                contentEntity.setId(null);
                contentEntity.setLastModifiedTime(null);
                contentEntity.setLastModifierId(null);
                contentEntity.setComponentId(componentIds.get(i));
                contentEntity.setCreatedTime(new Date());
                contentEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
                contentEntities.add(contentEntity);
            }
            //批量插入组件内容
            contentDao.insertBatch(contentEntities);
            //调整页面组件表组件类型，以组件内容表为准
            //将组件内容的组件id与类型放在Map集合中
            Map<Long, String> componentTypeMap = new HashMap<>();
            for (ComponentContentEntity contentEntity : contentEntities) {
                componentTypeMap.put(contentEntity.getComponentId(), contentEntity.getType());
            }
            for (PageComponentEntity componentEntity : componentEntities) {
                PageComponentEntity entity = new PageComponentEntity();
                entity.cleanInit();
                entity.setType(componentTypeMap.get(componentEntity.getId()));
                pageComponentDao.updateById(entity);
            }//后续修改为批量修改
        }

    }

    @Override
    public PageComponentDto preview(Long id) {
        if (null == id) {
            throw new GlobalException(DecorateExcepionEnum.ID_NOT_NULL);
        }
        List<PageComponentEntity> entities = getPageEntities(id);
        PageComponentDto pageComponentDto = new PageComponentDto();
        pageComponentDto.setPageId(id);
        if (CollectionUtils.isNotEmpty(entities)) {
            List<ComponentDto> componentDtos = new ArrayList<>();
            for (PageComponentEntity entity : entities) {
                ComponentDto componentDto = new ComponentDto();
                BeanUtils.copyProperties(entity, componentDto);
                componentDtos.add(componentDto);
            }
            pageComponentDto.setComponentDtos(componentDtos);
        }
        return pageComponentDto;
    }

    @Override
    @Transactional
    @JedisOperation
    public void publish(Long id) {
        if (null == id) {
            throw new GlobalException(DecorateExcepionEnum.ID_NOT_NULL);
        }

        PageDetailEntity pageEntity = pageDetailDao.selectById(id);
        //重置发布时间
        resetPublishTime(pageEntity);

        //先判断该上线页面下是否有页面组件与页面内容,如果没有不允许发布
        List<PageComponentEntity> componentEntities = getPageEntities(id);
        if (CollectionUtils.isEmpty(componentEntities)) {
            throw new GlobalException(DecorateExcepionEnum.COMPONENT_NOT_NULL);
        }

        List<ComponentContentEntity> contentEntities = getContentEntities(componentEntities);
        if (CollectionUtils.isEmpty(contentEntities)) {
            throw new GlobalException(DecorateExcepionEnum.CONTENT_NOT_NULL);
        }

        //将该种页面其他页面设置为下线
        PageDetailEntity entity = pageDetailDao.selectById(id);
        List<PageDetailEntity> pageDetailEntities = getPageDetailEntities(entity);

        //在线页面id集合
        Integer update = 0;
        List<Long> pageIds = pageDetailEntities.stream().map(PageDetailEntity::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(pageDetailEntities) && CollectionUtils.isNotEmpty(pageIds)) {
            update = modifyPageStatus(pageIds);
        }

        //修改页面表中该条数据的状态为上线
        Integer updateById = modifySomePageStatus(id);

        //如果两次更新操作都成功，则更新缓存
        Jedis jedis = JedisContext.getJedis();
        if (null != updateById && updateById > 0 && update > 0) {
            PageComponentDto pageComponentDto = new PageComponentDto();
            pageComponentDto.setPageId(id);
            List<ComponentDto> componentDtos = new ArrayList<>();
            for (PageComponentEntity componentEntity : componentEntities) {
                ComponentDto componentDto = new ComponentDto();
                BeanUtils.copyProperties(componentEntity, componentDto);
                componentDtos.add(componentDto);
            }
            pageComponentDto.setComponentDtos(componentDtos);
            pageComponentDto.setStartTime(pageEntity.getStartTime());
            pageComponentDto.setEndTime(pageEntity.getEndTime());
            //系统页面不设置缓存失效时间
            if (Constant.DO_NOT_DELETE.equals(entity.getType())) {
                jedis.set(Constant.PAGE_KEY_PREFIX + entity.getSuffixUrl(), JSON.toJSONString(pageComponentDto));
            }
            //活动页面设置缓存失效时间
            if (Constant.CAN_BE_DELETED.equals(entity.getType())) {
                int expireTime = Constant.ACTIVITY_PAGE_EXPIRE_TIME + (int) Math.random() * Constant.RANDOM_MAX_EXPIRE_TIME;
                jedis.setex(Constant.PAGE_KEY_PREFIX + entity.getSuffixUrl(), expireTime, JSON.toJSONString(pageComponentDto));
            }
            //设置组件内容缓存数据
            this.saveContentCache(contentEntities, entity.getType());
        }

    }


    @Transactional
    @Override
    public void resetPublishTime(PageDetailEntity pageDetailEntity) {
        Date sysDate = new Date();//当前系统时间
        Date startTime = pageDetailEntity.getStartTime();
        Date endTime = pageDetailEntity.getEndTime();
        PageDetailEntity modifyPageEntity = new PageDetailEntity();
        modifyPageEntity.cleanInit();
        modifyPageEntity.setId(pageDetailEntity.getId());
        //1、在发布时间之前
        if (null != startTime && sysDate.before(startTime)) {
            pageDetailEntity.setStartTime(sysDate);
            modifyPageEntity.setStartTime(sysDate);
        }
        //2、在之间
        if (null != startTime && null != endTime && sysDate.before(startTime) && sysDate.after(endTime)) {
            pageDetailEntity.setStartTime(sysDate);
            pageDetailEntity.setEndTime(null);
            modifyPageEntity.setStartTime(sysDate);
            modifyPageEntity.setEndTime(null);
        }
        //3、在结束时间之后
        if (null != endTime && sysDate.after(endTime)) {
            pageDetailEntity.setStartTime(sysDate);
            pageDetailEntity.setEndTime(null);
            modifyPageEntity.setStartTime(sysDate);
            modifyPageEntity.setEndTime(null);
        }
        pageDetailDao.updateById(modifyPageEntity);
    }

    private Integer modifySomePageStatus(Long id) {
        PageDetailEntity pageDetailEntity = new PageDetailEntity();
        pageDetailEntity.cleanInit();
        pageDetailEntity.setId(id);
        pageDetailEntity.setStatus(Constant.ON_LINE);
        pageDetailEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        pageDetailEntity.setLastModifiedTime(new Date());
        return pageDetailDao.updateById(pageDetailEntity);
    }

    private Integer modifyPageStatus(List<Long> pageIds) {
        Integer update;
        EntityWrapper<PageDetailEntity> modifyCondition = new EntityWrapper<>();
        modifyCondition.in("id", pageIds);
        PageDetailEntity pageEntity = new PageDetailEntity();
        pageEntity.cleanInit();
        pageEntity.setStatus(Constant.NOT_ONLINE);
        pageEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        pageEntity.setLastModifiedTime(new Date());
        update = pageDetailDao.update(pageEntity, modifyCondition);
        return update;
    }

    private List<PageDetailEntity> getPageDetailEntities(PageDetailEntity entity) {
        EntityWrapper<PageDetailEntity> pageCondition = new EntityWrapper<>();
        pageCondition.eq("cRC32", entity.getCRC32());
        pageCondition.eq("suffixUrl", entity.getSuffixUrl());
        pageCondition.eq("status", Constant.ON_LINE);
        pageCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        return pageDetailDao.selectList(pageCondition);
    }

    private List<ComponentContentEntity> getContentEntities(List<PageComponentEntity> componentEntities) {
        List<Long> componentIds = componentEntities.stream().map(PageComponentEntity::getId).collect(Collectors.toList());
        EntityWrapper<ComponentContentEntity> contentCondition = new EntityWrapper<>();
        contentCondition.in("componentId", componentIds);
        contentCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        return contentDao.selectList(contentCondition);
    }

    private List<PageComponentEntity> getPageEntities(Long id) {
        EntityWrapper<PageComponentEntity> componentCondition = new EntityWrapper<>();
        componentCondition.eq("pageId", id);
        componentCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        componentCondition.orderBy("sortNo", true);//用作缓存内容排序
        return pageComponentDao.selectList(componentCondition);
    }

    @Override
    @Transactional
    @JedisOperation
    public void offline(Long id) {
        if (null == id) {
            throw new GlobalException(DecorateExcepionEnum.ID_NOT_NULL);
        }

        //修改该页面的状态为下线
        PageDetailEntity pageDetailEntity = new PageDetailEntity();
        pageDetailEntity.cleanInit();
        pageDetailEntity.setId(id);
        pageDetailEntity.setStatus(Constant.NOT_ONLINE);
        pageDetailEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        pageDetailEntity.setLastModifiedTime(new Date());
        Integer updateById = pageDetailDao.updateById(pageDetailEntity);

        //修改成功，删除缓存
        Jedis jedis = JedisContext.getJedis();
        if (null != updateById && updateById > 0) {
            PageDetailEntity entity = pageDetailDao.selectById(id);
            jedis.del(Constant.PAGE_KEY_PREFIX + entity.getSuffixUrl());
            //删除组件内容缓存
            EntityWrapper<PageComponentEntity> componentCondition = new EntityWrapper<>();
            componentCondition.eq("pageId", id);
            componentCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            List<PageComponentEntity> componentEntities = pageComponentDao.selectList(componentCondition);
            List<Long> componentIds = componentEntities.stream().map(PageComponentEntity::getId).collect(Collectors.toList());
            List<String> keyList = new ArrayList<>();
            for (Long componentId : componentIds) {
                keyList.add(Constant.COMPONENT_KEY_PREFIX + componentId);
            }
            String[] keyArr = keyList.toArray(new String[]{});
            jedis.del(keyArr);
        }
    }

    /**
     * 保存组件内容缓存
     *
     * @param entities
     */
    @Override
    @JedisOperation
    public void saveContentCache(List<ComponentContentEntity> entities, Byte pageType) {
        Jedis jedis = JedisContext.getJedis();
        for (ComponentContentEntity entity : entities) {
            String key = Constant.COMPONENT_KEY_PREFIX + entity.getComponentId();
            ComponentContentDto componentContentDto = componentBuilder.makeUpContent(entity);
            if (Constant.CAN_BE_DELETED.equals(pageType)) {
                int expireTime = Constant.ACTIVITY_PAGE_EXPIRE_TIME + (int) Math.random() * Constant.RANDOM_MAX_EXPIRE_TIME;
                jedis.setex(key, expireTime, JSON.toJSONString(componentContentDto));
            }
            if (Constant.DO_NOT_DELETE.equals(pageType)) {
                jedis.set(key, JSON.toJSONString(componentContentDto));
            }
        }
    }
}
