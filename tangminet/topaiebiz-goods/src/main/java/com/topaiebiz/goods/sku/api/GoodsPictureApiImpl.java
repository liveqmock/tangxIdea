package com.topaiebiz.goods.sku.api;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Maps;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.api.GoodsPictureApi;
import com.topaiebiz.goods.constants.GoodsConstants;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.goods.dto.sku.ItemPictureDTO;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.dao.ItemPictureDao;
import com.topaiebiz.goods.sku.dto.ItemPictureDto;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import com.topaiebiz.goods.sku.entity.ItemPictureEntity;
import com.topaiebiz.goods.sku.exception.GoodsSkuExceptionEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2018/1/6.
 */
@Service
public class GoodsPictureApiImpl implements GoodsPictureApi{

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ItemPictureDao itemPictureDao;

    @Override
    public ItemPictureDTO getMainPicture(Long itemId) {
        if (null == itemId) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        ItemEntity item = itemDao.selectById(itemId);
        if (null == item) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
        }
        ItemPictureEntity itemPictureEntity = new ItemPictureEntity();
        itemPictureEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        itemPictureEntity.setIsMain(GoodsConstants.PicType.MAIN_PIC);
        ItemPictureEntity itemPicture = itemPictureDao.selectOne(itemPictureEntity);
        ItemPictureDTO itemPictureDTO = new ItemPictureDTO();
        BeanCopyUtil.copy(itemPicture, itemPictureDTO);
        return itemPictureDTO;
    }

    @Override
    public Map<Long,ItemPictureDTO> getMainPictureMap(List<Long> itemIds) {
        if (CollectionUtils.isEmpty(itemIds)) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        Map<Long,ItemPictureDTO> itemPictureDTOMap = new HashMap<Long,ItemPictureDTO>();

        EntityWrapper<ItemPictureEntity> itemPictureCondition = new EntityWrapper<>();
        itemPictureCondition.in("id", itemIds);
        itemPictureCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        itemPictureCondition.eq("isMain",GoodsConstants.PicType.MAIN_PIC);
        List<ItemPictureEntity> itemPictureEntities = itemPictureDao.selectList(itemPictureCondition);

        if (CollectionUtils.isEmpty(itemPictureEntities)) {
            return Maps.newHashMap();
        }
        for (ItemPictureEntity itemPictureEntity : itemPictureEntities){
            Long itemId = itemPictureEntity.getItemId();
            if (null == itemPictureEntity) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
            }
            ItemPictureDTO itemPictureDTO = new ItemPictureDTO();
            BeanCopyUtil.copy(itemPictureEntity, itemPictureDTO);
            itemPictureDTOMap.put(itemId,itemPictureDTO);
        }
        return itemPictureDTOMap;
    }
}
