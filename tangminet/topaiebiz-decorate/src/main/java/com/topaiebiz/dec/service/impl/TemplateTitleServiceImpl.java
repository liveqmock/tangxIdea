package com.topaiebiz.dec.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.contants.Constants;

import com.nebulapaas.common.redis.aop.JedisContext;
import com.nebulapaas.common.redis.aop.JedisOperation;
import com.nebulapaas.web.exception.GlobalException;

import com.topaiebiz.dec.constans.RedisKey;
import com.topaiebiz.dec.dao.TemplateTitleDao;
import com.topaiebiz.dec.dao.TitleGoodsDao;
import com.topaiebiz.dec.dto.SecondTitleDto;
import com.topaiebiz.dec.dto.TemplateTitleDto;
import com.topaiebiz.dec.entity.TemplateTitleEntity;
import com.topaiebiz.dec.entity.TitleGoodsEntity;
import com.topaiebiz.dec.exception.DecExceptionEnum;
import com.topaiebiz.dec.service.MQProducerService;
import com.topaiebiz.dec.service.TemplateTitleService;
import com.topaiebiz.system.util.SecurityContextUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;


import java.util.*;

import java.util.stream.Collectors;

/**
 * <p>
 * 商品标题表 服务实现类
 * </p>
 *
 * @author hzj
 * @since 2018-01-08
 */
@Service
public class TemplateTitleServiceImpl extends ServiceImpl<TemplateTitleDao, TemplateTitleEntity> implements TemplateTitleService {

    public static final int FIRST_LEVEL_TITLE = 1;

    public static final int SECOND_LEVEL_TITLE = 2;

    @Autowired
    private TemplateTitleDao templateTitleDao;

    @Autowired
    private TitleGoodsDao titleGoodsDao;

    @Autowired
    private MQProducerService mqProducerService;

    @Override
    public TemplateTitleDto saveTemplateTitleDto(TemplateTitleDto templateTitleDto) {
        if (StringUtils.isEmpty(templateTitleDto)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        TemplateTitleDto backTitleDto = new TemplateTitleDto();
        List<SecondTitleDto> secondBackTitleDto = new ArrayList<>();
        String firstTitle = templateTitleDto.getTitleName();
        String memo = templateTitleDto.getMemo();
        TemplateTitleEntity templateTitleEntity = new TemplateTitleEntity();
        templateTitleEntity.setModuleId(templateTitleDto.getModuleId());
        templateTitleEntity.setTitleName(firstTitle);
        templateTitleEntity.setSortNo(templateTitleDto.getSortNo());
        templateTitleEntity.setMemo(memo);
        templateTitleEntity.setLevel(FIRST_LEVEL_TITLE);
        templateTitleEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        templateTitleDao.insert(templateTitleEntity);
        backTitleDto.setId(templateTitleEntity.getId());
        Long pId = templateTitleEntity.getId();

        if (!CollectionUtils.isEmpty(templateTitleDto.getSecondTitleDto())) {
            for (SecondTitleDto secondTitleDto : templateTitleDto.getSecondTitleDto()) {
                SecondTitleDto backSecondTitleDto = new SecondTitleDto();
                TemplateTitleEntity titleEntity = new TemplateTitleEntity();
                titleEntity.setModuleId(templateTitleDto.getModuleId());
                titleEntity.setTitleName(secondTitleDto.getTitleName());
                titleEntity.setParentId(pId);
                titleEntity.setSortNo(secondTitleDto.getSortNo());
                titleEntity.setLevel(SECOND_LEVEL_TITLE);
                titleEntity.setMemo(memo);
                titleEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
                templateTitleDao.insert(titleEntity);
                backSecondTitleDto.setId(titleEntity.getId());
                secondBackTitleDto.add(backSecondTitleDto);
            }
        }
        mqProducerService.produceMQByModuleId(templateTitleDto.getModuleId());
        return backTitleDto;
    }


    @Override
    public void updateTemplateTitleDto(Long id, String titleName) {
        if (StringUtils.isEmpty(id)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        TemplateTitleEntity templateTitleEntity = new TemplateTitleEntity();
        templateTitleEntity.cleanInit();
        templateTitleEntity.setTitleName(titleName);
        templateTitleEntity.setLastModifiedTime(new Date());
        templateTitleEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        templateTitleEntity.setId(id);
        templateTitleDao.updateById(templateTitleEntity);

        TemplateTitleEntity entity = templateTitleDao.selectById(id);
        mqProducerService.produceMQByModuleId(entity.getModuleId());
    }

    @Override
    @JedisOperation
    @Transactional
    public void deleteTemplateTitle(Long id) {
        if (StringUtils.isEmpty(id)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        Jedis jedis = JedisContext.getJedis();
        TemplateTitleEntity oldTemplateTitleEntity = templateTitleDao.selectById(id);

        //先删除自己
        TemplateTitleEntity templateTitleEntity = new TemplateTitleEntity();
        templateTitleEntity.cleanInit();
        templateTitleEntity.setId(id);
        templateTitleEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        templateTitleEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        templateTitleEntity.setLastModifiedTime(new Date());
        templateTitleDao.updateById(templateTitleEntity);

        //删除子标题
        EntityWrapper<TemplateTitleEntity> subTitleCondition = new EntityWrapper();
        subTitleCondition.eq("parentId", oldTemplateTitleEntity.getId());
        subTitleCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        //未删除前获取
        List<TemplateTitleEntity> subEntities = templateTitleDao.selectList(subTitleCondition);
        TemplateTitleEntity subTitleEntity = new TemplateTitleEntity();
        subTitleEntity.cleanInit();
        subTitleEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        subTitleEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        subTitleEntity.setLastModifiedTime(new Date());
        templateTitleDao.update(subTitleEntity, subTitleCondition);

        //删除标题下对应的商品
        EntityWrapper<TitleGoodsEntity> secondTitleGoodsCondition = new EntityWrapper();
        secondTitleGoodsCondition.eq("titleId", id);
        EntityWrapper<TitleGoodsEntity> parentTitleGoodsCondition = new EntityWrapper();
        parentTitleGoodsCondition.eq("parentId", id);
        TitleGoodsEntity titleGoodsEntity = new TitleGoodsEntity();
        titleGoodsEntity.cleanInit();
        titleGoodsEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        titleGoodsEntity.setLastModifiedTime(new Date());
        titleGoodsEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        titleGoodsDao.update(titleGoodsEntity, secondTitleGoodsCondition);
        titleGoodsDao.update(titleGoodsEntity, parentTitleGoodsCondition);
        //更新模板REDIS缓存数据
        mqProducerService.produceMQByModuleId(oldTemplateTitleEntity.getModuleId());
        //更新标题商品REDIS缓存数据,删除标题即删除标题商品直接删除相应缓存数据，先删除自己
        jedis.del(RedisKey.DECORATE_APP_HOMEPAGE_PREFIX + id);
        //删除子标题缓存数据
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(subEntities)) {
            List<Long> subTitleIdArr = subEntities.stream().map(TemplateTitleEntity::getId).collect(Collectors.toList());
            Long[] subTitleIds = subTitleIdArr.toArray(new Long[]{});
            List<String> titleIds = new ArrayList<>();
            for (Long titleId : subTitleIds) {
                titleIds.add(RedisKey.DECORATE_TITLE_GOODS_PREFIX + titleId);
            }
            jedis.del(titleIds.toArray(new String[]{}));
        }

    }

    @Override
    public List<TemplateTitleDto> getTemplateTitleDto(Long moduleId) {
        if (StringUtils.isEmpty(moduleId)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        List<TemplateTitleDto> templateTitleDtoList = new ArrayList<>();
        //先获取所有的一级标题
        EntityWrapper<TemplateTitleEntity> condition = new EntityWrapper();
        condition.eq("moduleId", moduleId);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.eq("level", FIRST_LEVEL_TITLE);
        condition.orderBy("sortNo", true);
        List<TemplateTitleEntity> firstTitleEntityList = templateTitleDao.selectList(condition);

        if (CollectionUtils.isEmpty(firstTitleEntityList)) {
            return Collections.emptyList();
        }
        List<Long> parentIdList = firstTitleEntityList.stream().map(item -> item.getId()).collect(Collectors.toList());

        EntityWrapper<TemplateTitleEntity> queryCondition = new EntityWrapper();
        queryCondition.eq("moduleId", moduleId);
        queryCondition.in("parentId", parentIdList);
        queryCondition.eq("level", SECOND_LEVEL_TITLE);
        queryCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        queryCondition.orderBy("sortNo", true);
        List<TemplateTitleEntity> secondTitleEntityList = templateTitleDao.selectList(queryCondition);

        Map<Long, List<TemplateTitleEntity>> titleMap = new HashMap<>();
        for (TemplateTitleEntity firstTitle : firstTitleEntityList) {
            List<TemplateTitleEntity> linkTitleList = new ArrayList<>();
            for (TemplateTitleEntity secondTitle : secondTitleEntityList) {
                if (secondTitle.getParentId().equals(firstTitle.getId())) {
                    linkTitleList.add(secondTitle);
                }
            }
            linkTitleList.add(firstTitle);
            titleMap.put(firstTitle.getId(), linkTitleList);
        }

        for (Long id : parentIdList) {
            List<TemplateTitleEntity> templateTitleEntityList = titleMap.get(id);
            List<TemplateTitleEntity> secondTemplateTitleEntityList = new ArrayList<>();
            List<TemplateTitleEntity> firstTemplateTitleEntityList = new ArrayList<>();
            for (TemplateTitleEntity templateTitleEntity : templateTitleEntityList) {
                if (!StringUtils.isEmpty(templateTitleEntity.getParentId()) && templateTitleEntity.getParentId().equals(id)) {
                    secondTemplateTitleEntityList.add(templateTitleEntity);
                } else if (templateTitleEntity.getId().equals(id)) {
                    firstTemplateTitleEntityList.add(templateTitleEntity);
                }
            }
            if (CollectionUtils.isEmpty(secondTemplateTitleEntityList)) {
                TemplateTitleDto templateTitleDto = new TemplateTitleDto();
                templateTitleDto.setId(id);
                templateTitleDto.setModuleId(firstTemplateTitleEntityList.get(0).getModuleId());
                templateTitleDto.setTitleName(firstTemplateTitleEntityList.get(0).getTitleName());
                templateTitleDto.setSortNo(firstTemplateTitleEntityList.get(0).getSortNo());
                templateTitleDtoList.add(templateTitleDto);
            } else {
                TemplateTitleDto templateTitleDto = new TemplateTitleDto();
                List<SecondTitleDto> secondTitleDtoList = new ArrayList<>();
                templateTitleDto.setId(id);
                templateTitleDto.setModuleId(firstTemplateTitleEntityList.get(0).getModuleId());
                templateTitleDto.setTitleName(firstTemplateTitleEntityList.get(0).getTitleName());
                templateTitleDto.setSortNo(firstTemplateTitleEntityList.get(0).getSortNo());
                for (TemplateTitleEntity secondTemplateTitleEntity : secondTemplateTitleEntityList) {
                    SecondTitleDto secondTitleDto = new SecondTitleDto();
                    secondTitleDto.setId(secondTemplateTitleEntity.getId());
                    secondTitleDto.setTitleName(secondTemplateTitleEntity.getTitleName());
                    secondTitleDto.setSortNo(secondTemplateTitleEntity.getSortNo());
                    secondTitleDtoList.add(secondTitleDto);
                }
                templateTitleDto.setSecondTitleDto(secondTitleDtoList);
                templateTitleDtoList.add(templateTitleDto);

            }
        }
        return templateTitleDtoList;
    }

    @Override
    public void moveTemplateTitle(Long id, Long targetId) {

        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(targetId)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }

        TemplateTitleEntity templateTitleEntity = templateTitleDao.selectById(id);
        TemplateTitleEntity targetTitleEntity = templateTitleDao.selectById(targetId);
        if (!StringUtils.isEmpty(templateTitleEntity) && !StringUtils.isEmpty(targetTitleEntity)) {
            TemplateTitleEntity modifyEntity = new TemplateTitleEntity();
            modifyEntity.cleanInit();
            modifyEntity.setSortNo(targetTitleEntity.getSortNo());
            modifyEntity.setLastModifiedTime(new Date());
            modifyEntity.setId(templateTitleEntity.getId());
            modifyEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            templateTitleDao.updateById(modifyEntity);

            TemplateTitleEntity targetModifyEntity = new TemplateTitleEntity();
            targetModifyEntity.cleanInit();
            targetModifyEntity.setSortNo(templateTitleEntity.getSortNo());
            targetModifyEntity.setLastModifiedTime(new Date());
            targetModifyEntity.setId(targetTitleEntity.getId());
            targetModifyEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            templateTitleDao.updateById(targetModifyEntity);
        }

        //更新缓存
        mqProducerService.produceMQByModuleId(templateTitleEntity.getModuleId());
    }

    @Override
    public Long saveSecondTitle(SecondTitleDto secondTitleDto) {
        if (null == secondTitleDto) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        TemplateTitleEntity entity = new TemplateTitleEntity();
        BeanUtils.copyProperties(secondTitleDto, entity);
        entity.setLevel(SECOND_LEVEL_TITLE);
        entity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        entity.setCreatedTime(new Date());
        templateTitleDao.insert(entity);
        mqProducerService.produceMQByModuleId(secondTitleDto.getModuleId());
        return entity.getId();
    }
}
