package com.topaiebiz.dec.api;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.dec.dao.*;
import com.topaiebiz.dec.entity.*;
import com.topaiebiz.dec.exception.DecExceptionEnum;
import com.topaiebiz.dec.service.MQProducerService;
import com.topaiebiz.message.api.TemplateApi;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TemplateApiImpl implements TemplateApi {

    public static final String IS_USED = "1";
    @Autowired
    private TemplateModuleDao templateModuleDao;

    @Autowired
    private TemplateInfoDao templateInfoDao;

    @Autowired
    private ModuleGoodsDao moduleGoodsDao;

    @Autowired
    private TitleGoodsDao titleGoodsDao;

    @Autowired
    private MQProducerService mqProducerService;


    @Override
    public void addMerchantTemplate(Long storeId, String storeName) {
        //添加模板
        TemplateInfoEntity entity = new TemplateInfoEntity();
        entity.setStoreId(storeId);
        entity.setTemplateName(storeName);
        entity.setIsUsed(IS_USED);
        entity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        entity.setCreatedTime(new Date());
        templateInfoDao.insert(entity);
        Long templateId = entity.getId();
        //添加模块 一个商家模板3个模块
        List<TemplateModuleEntity> moduleEntities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TemplateModuleEntity moduleEntity = new TemplateModuleEntity();
            moduleEntity.setInfoId(templateId);
            moduleEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
            moduleEntity.setCreatedTime(new Date());
            moduleEntities.add(moduleEntity);
        }
        templateModuleDao.insertBatch(moduleEntities);
    }

    /**
     * 商家删除商品同步删除模板处数据，并更新缓存
     *
     * @param id
     */
    @Override
    public void removeItem(Long[] id) {
        if (null == id) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }

        //删除模块下这些商品
        ModuleGoodsEntity moduleGoodsEntity = new ModuleGoodsEntity();
        moduleGoodsEntity.cleanInit();
        moduleGoodsEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        moduleGoodsEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        moduleGoodsEntity.setCreatedTime(new Date());
        EntityWrapper<ModuleGoodsEntity> moduleGoodsCondition = new EntityWrapper<>();
        moduleGoodsCondition.in("goodsId", id);
        moduleGoodsCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        //通过这些商品ID查询相应的模块ID
        List<ModuleGoodsEntity> moduleGoodsEntities = moduleGoodsDao.selectList(moduleGoodsCondition);
        moduleGoodsDao.update(moduleGoodsEntity, moduleGoodsCondition);

        //删除标题下这些商品
        TitleGoodsEntity titleGoodsEntity = new TitleGoodsEntity();
        titleGoodsEntity.cleanInit();
        titleGoodsEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        titleGoodsEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        titleGoodsEntity.setCreatedTime(new Date());
        EntityWrapper<TitleGoodsEntity> titleGoodsCondition = new EntityWrapper<>();
        titleGoodsCondition.in("goodsId", id);
        titleGoodsCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<TitleGoodsEntity> titleGoodsEntities = titleGoodsDao.selectList(titleGoodsCondition);
        titleGoodsDao.update(titleGoodsEntity, titleGoodsCondition);
        //刷新缓存
        refreshModuleItemCache(moduleGoodsEntities);
        refreshTitleItemCache(titleGoodsEntities);

    }

    /**
     * 商家编辑商品同步更新商品相关缓存
     *
     * @param itemId
     */
    @Override
    public void editItem(Long itemId) {
        if (null == itemId) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        //模块商品相关
        EntityWrapper<ModuleGoodsEntity> moduleGoodsEntityWrapper = new EntityWrapper<>();
        moduleGoodsEntityWrapper.eq("goodsId", itemId);
        moduleGoodsEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<ModuleGoodsEntity> moduleGoodsEntities = moduleGoodsDao.selectList(moduleGoodsEntityWrapper);
        //标题商品相关
        EntityWrapper<TitleGoodsEntity> titleGoodsEntityWrapper = new EntityWrapper<>();
        titleGoodsEntityWrapper.eq("goodsId", itemId);
        titleGoodsEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<TitleGoodsEntity> titleGoodsEntities = titleGoodsDao.selectList(titleGoodsEntityWrapper);
        //刷新缓存
        refreshModuleItemCache(moduleGoodsEntities);
        refreshTitleItemCache(titleGoodsEntities);
    }

    /**
     * 刷新模块商品的缓存
     *
     * @param moduleGoodsEntities
     */
    public void refreshModuleItemCache(List<ModuleGoodsEntity> moduleGoodsEntities) {

        if (CollectionUtils.isNotEmpty(moduleGoodsEntities)) {
            Set<Long> moduleIds = new HashSet<>();
            for (ModuleGoodsEntity entity : moduleGoodsEntities) {
                moduleIds.add(entity.getModuleId());
            }
            for (Long moduleId : moduleIds) {
                mqProducerService.produceMQByModuleId(moduleId);
            }
        }

    }

    /**
     * 刷新标题商品的缓存
     *
     * @param titleGoodsEntities
     */
    public void refreshTitleItemCache(List<TitleGoodsEntity> titleGoodsEntities) {

        if (CollectionUtils.isNotEmpty(titleGoodsEntities)) {
            Set<Long> titleIds = new HashSet<>();
            for (TitleGoodsEntity entity : titleGoodsEntities) {
                titleIds.add(entity.getTitleId());
            }
            for (Long titleId : titleIds) {
                mqProducerService.produceMQByTitileId(titleId);
            }
        }

    }
}

