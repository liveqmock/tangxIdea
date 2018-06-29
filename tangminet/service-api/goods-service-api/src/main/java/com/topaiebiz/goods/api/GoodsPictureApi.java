package com.topaiebiz.goods.api;

import com.topaiebiz.goods.dto.sku.ItemPictureDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2018/1/4.
 */
public interface GoodsPictureApi {

    /**
     * Description 根据itemId查询商品图片集合
     * <p>
     * Author Hedda
     *
     * @param itemId 商品itemId
     * @return
     */
    ItemPictureDTO getMainPicture(Long itemId);



    /**
     * 批量获取商品主图
     * @param itemIds 商品ID集合
     * @return 返回以商品ID为键的主图Map
     */
    Map<Long,ItemPictureDTO> getMainPictureMap(List<Long> itemIds);
}