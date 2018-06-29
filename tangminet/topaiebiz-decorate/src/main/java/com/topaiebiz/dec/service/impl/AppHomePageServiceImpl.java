package com.topaiebiz.dec.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.nebulapaas.common.redis.aop.JedisOperation;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.dec.constans.RedisKey;
import com.topaiebiz.dec.dao.ModuleGoodsDao;
import com.topaiebiz.dec.dao.ModuleInfoDao;
import com.topaiebiz.dec.dao.TemplateInfoDao;
import com.topaiebiz.dec.dao.TemplateModuleDao;
import com.topaiebiz.dec.dto.*;
import com.topaiebiz.dec.entity.*;
import com.topaiebiz.dec.exception.DecExceptionEnum;
import com.topaiebiz.dec.service.AppHomePageService;
import com.topaiebiz.dec.service.TemplateTitleService;
import com.topaiebiz.dec.service.TitleGoodsService;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.dto.sku.GoodsDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.*;

@Service
public class AppHomePageServiceImpl implements AppHomePageService {

    @Autowired
    private TemplateModuleDao templateModuleDao;

    @Autowired
    private ModuleInfoDao moduleInfoDao;

    @Autowired
    private ModuleGoodsDao moduleGoodsDao;

    @Autowired
    private TemplateTitleService templateTitleService;

    @Autowired
    private TitleGoodsService titleGoodsService;

    @Autowired
    private TemplateInfoDao templateInfoDao;

    @Autowired
    private GoodsApi goodsApi;

    @JedisOperation
    @Override
    public List<AppHomePageDto> search(Long templateId) {
        if (null == templateId) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        if (null == templateInfoDao.selectById(templateId)) {
            return Collections.emptyList();
        }
        Jedis jedis = JedisContext.getJedis();
        String key = RedisKey.DECORATE_APP_HOMEPAGE_PREFIX + templateId;
        if (jedis.exists(key)) {
            List<AppHomePageDto> cacheData = JSON.parseArray(jedis.get(key), AppHomePageDto.class);
            return cacheData;
        } else {
            return gainTemplate(templateId);
        }
    }

    @JedisOperation
    @Override
    public void refreshCache(Long templateId) {
        if (null == templateId) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        gainTemplate(templateId);
    }

    @Override
    @JedisOperation
    public List<AppHomePageDto> storeSearch(Long storeId) {
        if (null == storeId) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        List<AppHomePageDto> storeData = new ArrayList<>();
        EntityWrapper<TemplateInfoEntity> condition = new EntityWrapper<>();
        condition.eq("storeId", storeId);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<TemplateInfoEntity> templateList = templateInfoDao.selectList(condition);
        if (CollectionUtils.isEmpty(templateList)) {
            return Collections.emptyList();
        }
        storeData = search(templateList.get(0).getId());
        return storeData;
    }

    @JedisOperation
    @Override
    public List<AppHomePageDto> gainTemplate(Long templateId) {
        if (null == templateId) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        if (null == templateInfoDao.selectById(templateId)) {
            return Collections.emptyList();
        }
        String key = RedisKey.DECORATE_APP_HOMEPAGE_PREFIX + templateId;
        Jedis jedis = JedisContext.getJedis();
        List<AppHomePageDto> appHomePageDtoList = new ArrayList<>();
        //模板下所有模块
        EntityWrapper<TemplateModuleEntity> moduleCondition = new EntityWrapper<>();
        moduleCondition.eq("infoId", templateId);
        moduleCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<TemplateModuleEntity> moduleEntities = templateModuleDao.selectList(moduleCondition);

        if (CollectionUtils.isEmpty(moduleEntities)) {
            throw new GlobalException(DecExceptionEnum.MODULE_NOT_NULL);
        }
        //将该模板下的moduleId整理成集合
        List<Long> moduleIds = new ArrayList<>();
        for (TemplateModuleEntity moduleEntity : moduleEntities) {
            moduleIds.add(moduleEntity.getId());
        }


        //获取所有模块下的moduleInfo集合
        EntityWrapper<ModuleInfoEntity> moduleInfoCondition = new EntityWrapper<>();
        moduleInfoCondition.in("moduleId", moduleIds);
        moduleInfoCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        moduleInfoCondition.orderBy("sortNo", true);
        List<ModuleInfoEntity> moduleInfoEntities = moduleInfoDao.selectList(moduleInfoCondition);
        Map<Long, List<AppModuleInfoDto>> moduleInfoDtoMap = new HashMap<>();
        for (Long moduleId : moduleIds) {
            List<AppModuleInfoDto> appModuleInfoDtos = new ArrayList<>();
            for (ModuleInfoEntity entity : moduleInfoEntities) {
                if (entity.getModuleId().equals(moduleId)) {
                    AppModuleInfoDto appModuleInfoDto = new AppModuleInfoDto();
                    BeanUtils.copyProperties(entity, appModuleInfoDto);
                    appModuleInfoDtos.add(appModuleInfoDto);
                }
            }
            moduleInfoDtoMap.put(moduleId, appModuleInfoDtos);
        }

        //获取模块下的所有模块商品
        EntityWrapper<ModuleGoodsEntity> moduleGoodsCondition = new EntityWrapper<>();
        moduleGoodsCondition.in("moduleId", moduleIds);
        moduleGoodsCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        moduleGoodsCondition.orderBy("sortNo", true);
        List<ModuleGoodsEntity> moduleGoodsEntities = moduleGoodsDao.selectList(moduleGoodsCondition);
        Map<Long, List<ModuleGoodsEntity>> moduleGoodsMap = new HashMap<>();
        for (Long moduleId : moduleIds) {
            List<ModuleGoodsEntity> moduleGoodsEntityList = new ArrayList<>();
            for (ModuleGoodsEntity entity : moduleGoodsEntities) {
                if (entity.getModuleId().equals(moduleId)) {
                    moduleGoodsEntityList.add(entity);
                }
            }
            moduleGoodsMap.put(moduleId, moduleGoodsEntityList);
        }

        for (Long moduleId : moduleIds) {
            AppHomePageDto homePageDto = new AppHomePageDto();
            AppModuleGoodsDto appModuleGoodsDto = new AppModuleGoodsDto();
            List<GoodsInfoDto> goodsInfoDtoList = new ArrayList<>();
            List<TemplateTitleDto> titleDtoList = templateTitleService.getTemplateTitleDto(moduleId);
            TemplateModuleEntity templateModuleEntity = templateModuleDao.selectById(moduleId);
            List<GoodsDTO> moduleItem = new ArrayList<>();
            homePageDto.setModuleId(moduleId);
            homePageDto.setParentId(templateModuleEntity.getParentId());
            homePageDto.setTitleList(titleDtoList);
            homePageDto.setModuleInfoList(moduleInfoDtoMap.get(moduleId));
            if (CollectionUtils.isNotEmpty(moduleGoodsMap.get(moduleId))) {
                for (ModuleGoodsEntity moduleGoodsEntity : moduleGoodsMap.get(moduleId)) {
                    GoodsInfoDto goodsInfoDto = new GoodsInfoDto();
                    BeanUtils.copyProperties(moduleGoodsEntity, goodsInfoDto);
                    goodsInfoDtoList.add(goodsInfoDto);
                }
            }

            List<GoodsDTO> moduleGoodsIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(goodsInfoDtoList)) {
                for (GoodsInfoDto goodsInfoDto : goodsInfoDtoList) {
                    GoodsDTO goodsDTO = new GoodsDTO();
                    BeanUtils.copyProperties(goodsInfoDto, goodsDTO);
                    moduleGoodsIds.add(goodsDTO);
                }
            }
            if (CollectionUtils.isNotEmpty(moduleGoodsIds)) {
                List<GoodsDTO> moduleItems = goodsApi.getGoodsSort(moduleGoodsIds);
                if (CollectionUtils.isNotEmpty(moduleItems)) {
                    for (GoodsDTO GoodsDTO : moduleItems) {
                        moduleItem.add(GoodsDTO);
                    }
                }
            }
            appModuleGoodsDto.setModuleGoodsDetail(moduleItem);
            homePageDto.setAppModuleGoodsDto(appModuleGoodsDto);
            appHomePageDtoList.add(homePageDto);
        }
        jedis.set(key, JSON.toJSONString(appHomePageDtoList));
        return appHomePageDtoList;
    }

}