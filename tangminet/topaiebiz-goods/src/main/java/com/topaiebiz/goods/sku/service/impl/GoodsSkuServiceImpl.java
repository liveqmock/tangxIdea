package com.topaiebiz.goods.sku.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.goods.sku.dao.GoodsSkuDao;
import com.topaiebiz.goods.sku.entity.GoodsSkuEntity;
import com.topaiebiz.goods.sku.exception.GoodsSkuExceptionEnum;
import com.topaiebiz.goods.sku.service.GoodsSkuService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017/12/20 20:04
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
public class GoodsSkuServiceImpl extends ServiceImpl<GoodsSkuDao ,GoodsSkuEntity> implements GoodsSkuService{

    @Autowired
    private GoodsSkuDao goodsSkuDao;

    @Override
    public boolean removeGoodsSkus(Long[] id) {
        boolean b = false;
        /** 判断id是否为空 */
        if (null == id || id.length == 0) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        EntityWrapper<GoodsSkuEntity> cond = new EntityWrapper<>();
        cond.in("id",id);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<GoodsSkuEntity> goodsSkuEntities =goodsSkuDao.selectList(cond);
        if(CollectionUtils.isNotEmpty(goodsSkuEntities)){
            for (GoodsSkuEntity goodsSku : goodsSkuEntities) {
                goodsSku.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                goodsSku.setLastModifiedTime(new Date());
                goodsSku.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
                int i = goodsSkuDao.updateById(goodsSku);
                b = i>0;
            }
        }
        return b;
    }
}
