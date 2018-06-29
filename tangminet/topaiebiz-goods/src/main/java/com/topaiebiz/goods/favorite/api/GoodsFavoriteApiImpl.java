package com.topaiebiz.goods.favorite.api;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.api.GoodsFavoriteApi;
import com.topaiebiz.goods.favorite.dao.GoodsFavoriteDao;
import com.topaiebiz.goods.favorite.entity.GoodsFavoriteEntity;
import com.topaiebiz.goods.favorite.exception.GoodsFootprintExceptionEnum;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2018/1/8.
 */
@Service
public class GoodsFavoriteApiImpl implements GoodsFavoriteApi{

    @Autowired
    private GoodsFavoriteDao goodsFavoriteDao;

    @Autowired
    private ItemDao itemDao;

    @Transactional
    @Override
    public Integer addFavorite(Long memberId, List<Long> itemIds) {
        Integer i = 0;
        if (CollectionUtils.isEmpty(itemIds)) {
            throw new GlobalException(GoodsFootprintExceptionEnum.GOODSFAVOR_GOODSID_NOT_NULL);
        }
        EntityWrapper<GoodsFavoriteEntity> condition= new EntityWrapper<>();
        condition.eq("memberId",memberId);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<GoodsFavoriteEntity> goodsFavoriteEntities = goodsFavoriteDao.selectList(condition);
        //收藏夹等于300已满
        if (goodsFavoriteEntities.size() == 300) {
            throw new GlobalException(GoodsFootprintExceptionEnum.FAVORITE_FULL);
        }
        for (Long itemId : itemIds) {
            ItemEntity itemEntity = itemDao.selectById(itemId);
            if (null == itemEntity) {
                throw new GlobalException(GoodsFootprintExceptionEnum.GOODSCART_GOODSATTR_NOT_NULL);
            }
            GoodsFavoriteEntity goodsFavorite = new GoodsFavoriteEntity();
            goodsFavorite.setGoodsId(itemId);
            goodsFavorite.setMemberId(memberId);
            goodsFavorite.setDeletedFlag(Constants.DeletedFlag.DELETED_NO);
            GoodsFavoriteEntity goodsFavoriteEntity = goodsFavoriteDao.selectOne(goodsFavorite);
            // 收藏夹不存在的情况
            if (null == goodsFavoriteEntity) {
                GoodsFavoriteEntity goodsFavoriteEntity1 = new GoodsFavoriteEntity();
                goodsFavoriteEntity1.setGoodsId(itemId);
                goodsFavoriteEntity1.setMemberId(memberId);
                goodsFavoriteEntity1.setCreatorId(memberId);
                goodsFavoriteEntity1.setCreatedTime(new Date());
                i = goodsFavoriteDao.insert(goodsFavoriteEntity1);
            }
        }
        return i;
    }
}
