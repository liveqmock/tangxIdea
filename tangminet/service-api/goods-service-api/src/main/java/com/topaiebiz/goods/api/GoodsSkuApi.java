package com.topaiebiz.goods.api;

import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.goods.dto.sku.StorageUpdateDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2018/1/4.
 */
public interface GoodsSkuApi {

    /**
     * Description 根据skuId查询商品sku信息
     * <p>
     * Author Hedda
     *
     * @param skuId 商品skuId
     * @return
     */
    GoodsSkuDTO getGoodsSku(Long skuId);

    /**
     * Description 根据skuId查询商品sku信息
     * <p>
     * Author Hedda
     *
     * @param itemId 商品itemId
     * @return
     */
    List<GoodsSkuDTO> getGoodsSkuList(Long itemId);


    /**
     * Description 根据商品集合skuId查询商品集合信息
     * <p>
     * Author Hedda
     *
     * @param skuIds 商品集合id
     * @return
     */
    Map<Long, GoodsSkuDTO> getGoodsSkuMap(List<Long> skuIds);

    /**
     * Description 根据商品集合skuId查询商品集合信息
     * <p>
     * Author Hedda
     *
     * @param itemList 商品集合id
     * @return
     */
    List<GoodsSkuDTO> getGoodsSkuList(List<Long> itemList);

    /**
     * Description 根据skuId和销售数量批量扣库存
     * <p>
     * Author Hedda
     *
     * @param updates
     * @return
     */
    Integer descreaseStorages(Long orderId, List<StorageUpdateDTO> updates);

    /**
     * Description 根据skuId和销售数量批量加库存
     * <p>
     * Author Hedda
     *
     * @param updates
     * @return
     */
    Integer inscreaseStorages(Long orderId, List<StorageUpdateDTO> updates);

    /**
     * Description 拼接商品规格
     * <p>
     * Author Hedda
     *
     * @param skuDTO
     */
    void loadSaleAttributes(GoodsSkuDTO skuDTO);

    /**
     * Description 批量拼接商品规格
     * <p>
     * Author Hedda
     *
     * @param skuDTOs
     */
    void loadSaleAttributes(List<GoodsSkuDTO> skuDTOs);

    /**
     * Description 根据skuId和店铺id把对应商品库存置0
     * <p>
     * Author Hedda
     *
     * @param skuId
     * @param storeId
     */
    Boolean inventoryIsZero(Long skuId, Long storeId,Long num);


    /**
     * Description 根据sku货号和店铺id把对应商品库存置0
     * <p>
     * Author Hedda
     *
     * @param articleNumber 货号
     * @param storeId 店铺id
     */
    Boolean stockNumberToZero(String articleNumber, Long storeId,Long num);
}
