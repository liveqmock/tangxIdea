package com.topaiebiz.goods.favorite.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.favorite.dao.GoodsShareDao;
import com.topaiebiz.goods.favorite.entity.GoodsShareEntity;
import com.topaiebiz.goods.favorite.exception.GoodsFootprintExceptionEnum;
import com.topaiebiz.goods.favorite.service.GoodsShareService;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.entity.GoodsSkuEntity;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2018/1/5.
 */
@Service
public class GoodsShareServiceImpl implements GoodsShareService{

    @Autowired
    private GoodsShareDao goodsShareDao;

    @Autowired
    private ItemDao itemDao;

    @Override
    public Integer saveGoodsSharing(Long memberId, Long[] itemIds) throws GlobalException {
        Integer i = null;
        if (null == itemIds) {
            throw new GlobalException(GoodsFootprintExceptionEnum.GOODSFAVOR_GOODSID_NOT_NULL);
        }

        EntityWrapper<ItemEntity> condition = new EntityWrapper<>();
        condition.in("id", itemIds);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<ItemEntity> itemEntities = itemDao.selectList(condition);
        if (CollectionUtils.isEmpty(itemEntities)) {
                throw new GlobalException(GoodsFootprintExceptionEnum.GOODSCART_GOODSATTR_NOT_NULL);
        }
        for (Long itemId : itemIds) {
            GoodsShareEntity goodsShareEntity = new GoodsShareEntity();
            goodsShareEntity.setMemberId(memberId);
            goodsShareEntity.setGoodsId(itemId);
            goodsShareEntity.setDeletedFlag(Constants.DeletedFlag.DELETED_NO);
            GoodsShareEntity goodsShareEntity2 = goodsShareDao.selectOne(goodsShareEntity);
            if (goodsShareEntity2 == null) {
                GoodsShareEntity goodsShareEntity1 = new GoodsShareEntity();
                goodsShareEntity1.setCreatedTime(new Date());
                goodsShareEntity1.setCreatorId(memberId);
                goodsShareEntity1.setGoodsId(itemId);
                goodsShareEntity1.setMemberId(memberId);
                i = goodsShareDao.insert(goodsShareEntity1);
            }
        }
        return i;
    }
}
