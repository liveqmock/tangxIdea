package com.topaiebiz.decorate.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.nebulapaas.common.redis.aop.JedisOperation;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.decorate.ComponentBuilder;
import com.topaiebiz.decorate.constant.Constant;
import com.topaiebiz.decorate.dao.PageComponentDao;
import com.topaiebiz.decorate.dao.PageDetailDao;
import com.topaiebiz.decorate.dto.ComponentContentDto;
import com.topaiebiz.decorate.dto.ComponentDto;
import com.topaiebiz.decorate.dto.PageComponentDto;
import com.topaiebiz.decorate.entity.PageComponentEntity;
import com.topaiebiz.decorate.entity.PageDetailEntity;
import com.topaiebiz.decorate.exception.DecorateExcepionEnum;
import com.topaiebiz.decorate.service.CTerminalService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.CRC32;

@Service
@Slf4j
public class CTerminalServiceImpl implements CTerminalService {

    @Autowired
    private PageComponentDao pageComponentDao;

    @Autowired
    private PageDetailDao pageDetailDao;

    @Autowired
    private DistLockSservice distLockSservice;

    @Autowired
    private ComponentBuilder componentBuilder;

    @Override
    @JedisOperation
    public PageComponentDto getComponent(String suffixUrl) {
        Jedis jedis = JedisContext.getJedis();
        PageComponentDto pageComponentDto = new PageComponentDto();
        String key = Constant.PAGE_KEY_PREFIX + suffixUrl;
        if (jedis.exists(key)) {
            pageComponentDto = JSON.parseObject(jedis.get(key), PageComponentDto.class);
            judgeRequestTime(pageComponentDto);
        } else {
            LockResult pageLock = null;
            try {
                //添加锁，防止缓存并发、穿透问题
                pageLock = distLockSservice.tryLock(Constant.DECORATE_PAGE_LOCK, suffixUrl);
                if (!pageLock.isSuccess()) {
                    return pageComponentDto;
                }

                //根据suffixUrl获取要查询的页面
                List<PageDetailEntity> pageEntities = getPageDetailEntities(suffixUrl);
                if (CollectionUtils.isEmpty(pageEntities)) {
                    throw new GlobalException(DecorateExcepionEnum.PAGE_NOT_EXIST);
                }
                PageDetailEntity pageEntity = pageEntities.get(0);

                //查询该页面下的组件
                pageComponentDto.setPageId(pageEntity.getId());
                List<PageComponentEntity> componentEntities = getPageComponentEntities(pageEntity);

                List<ComponentDto> componentDtos = new ArrayList<>();
                for (PageComponentEntity componentEntity : componentEntities) {
                    ComponentDto componentDto = new ComponentDto();
                    BeanUtils.copyProperties(componentEntity, componentDto);
                    componentDtos.add(componentDto);
                }
                pageComponentDto.setComponentDtos(componentDtos);
                pageComponentDto.setStartTime(pageEntity.getStartTime());
                pageComponentDto.setEndTime(pageEntity.getEndTime());
                //如果是系统页面
                if (Constant.DO_NOT_DELETE.equals(pageEntity.getType())) {
                    jedis.set(key, JSON.toJSONString(pageComponentDto));
                }
                //如果是活动时间
                if (Constant.CAN_BE_DELETED.equals(pageEntity.getType())) {
                    int expireTime = Constant.ACTIVITY_PAGE_EXPIRE_TIME + (int) Math.random() * Constant.RANDOM_MAX_EXPIRE_TIME;
                    jedis.setex(key, expireTime, JSON.toJSONString(pageComponentDto));
                }
                judgeRequestTime(pageComponentDto);
            } catch (Exception ex) {
                log.info("查询该页面组件缓存失败!!!!!!" + ex.getMessage());
            } finally {
                distLockSservice.unlock(pageLock);
            }
        }
        return pageComponentDto;
    }

    private List<PageComponentEntity> getPageComponentEntities(PageDetailEntity pageEntity) {
        EntityWrapper<PageComponentEntity> componentCondition = new EntityWrapper<>();
        componentCondition.eq("pageId", pageEntity.getId());
        componentCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        return pageComponentDao.selectList(componentCondition);
    }

    private List<PageDetailEntity> getPageDetailEntities(String suffixUrl) {
        EntityWrapper<PageDetailEntity> pageCondition = new EntityWrapper<>();
        CRC32 crc32 = new CRC32();
        crc32.update(suffixUrl.getBytes());
        pageCondition.eq("cRC32", crc32.getValue());
        pageCondition.eq("suffixUrl", suffixUrl);
        pageCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        pageCondition.eq("status", Constant.ON_LINE);
        return pageDetailDao.selectList(pageCondition);
    }

    private void judgeRequestTime(PageComponentDto pageComponentDto) {
        Date sysDate = new Date();//系统时间
        Date startTime = pageComponentDto.getStartTime();
        Date endTime = pageComponentDto.getEndTime();
        if (null != startTime && null != endTime && sysDate.before(startTime) && sysDate.after(endTime)) {
            throw new GlobalException(DecorateExcepionEnum.NOT_IN_ACTIVITY_TIME);
        }
        if (null != startTime && null == endTime && sysDate.before(startTime)) {
            throw new GlobalException(DecorateExcepionEnum.NOT_IN_ACTIVITY_TIME);
        }
    }

    @Override
    @JedisOperation
    public ComponentContentDto getContent(Long id) {
        if (null == id) {
            throw new GlobalException(DecorateExcepionEnum.ID_NOT_NULL);
        }
        Jedis jedis = JedisContext.getJedis();
        ComponentContentDto componentContentDto = new ComponentContentDto();
        String key = Constant.COMPONENT_KEY_PREFIX + id;
        //cache get fail
        if (jedis.exists(key)) {
            componentContentDto = JSON.parseObject(jedis.get(key), ComponentContentDto.class);
        } else {
            componentContentDto = loadContentCache(id);
        }
        return componentContentDto;
    }

    @Override
    @JedisOperation
    public ComponentContentDto loadContentCache(Long id) {
        ComponentContentDto componentContentDto = new ComponentContentDto();
        Jedis jedis = JedisContext.getJedis();
        String key = Constant.COMPONENT_KEY_PREFIX + id;
        LockResult componentLock = null;
        try {
            componentLock = distLockSservice.tryLock(Constant.DECORATE_COMPONENT_LOCK, id);
            if (!componentLock.isSuccess()) {
                return componentContentDto;
            }
            componentContentDto = componentBuilder.loadDataFromDB(id);
            //获取页面信息
            PageComponentEntity componentEntity = pageComponentDao.selectById(id);
            PageDetailEntity pageEntity = pageDetailDao.selectById(componentEntity.getPageId());
            if (Constant.CAN_BE_DELETED.equals(pageEntity.getType())) {
                int expireTime = Constant.ACTIVITY_PAGE_EXPIRE_TIME + (int) Math.random() * Constant.RANDOM_MAX_EXPIRE_TIME;//防止缓存数据同时失效
                jedis.setex(key, expireTime, JSON.toJSONString(componentContentDto));
            }
            if (Constant.DO_NOT_DELETE.equals(pageEntity.getType())) {
                jedis.set(key, JSON.toJSONString(componentContentDto));
            }
        } catch (Exception ex) {
            log.info("查询组件内容DB失败!!!!!!" + ex.getMessage());
        } finally {
            distLockSservice.unlock(componentLock);
        }
        return componentContentDto;
    }
}
