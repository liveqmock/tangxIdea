package com.topaiebiz.goods.api;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.goods.dto.sku.*;

import java.util.List;
import java.util.Map;

/**
 * Created by hecaifeng on 2018/1/4.
 */
public interface GoodsApi {

    /**
     * Description 根据itemId查询item商品信息
     * <p>
     * Author Hedda
     *
     * @param itemId 商品itemId
     * @return
     */
    ItemDTO getItem(Long itemId);

    /**
     * Description 根据itemId查询商品信息
     * <p>
     * Author Hedda
     *
     * @param goodsId 商品id
     * @return
     */
    ItemAppDTO getGoods(Long goodsId);

    /**
     * Description 根据itemId查询商品库存
     * <p>
     * Author Hedda
     *
     * @param itemId 商品itemId
     * @return
     */
    Long getAllSkusStorage(Long itemId);

    /**
     * Description 根据itemId查询商品信息
     * <p>
     * Author Hedda
     *
     * @param itemIds 商品itemId
     * @return
     */
    List<ItemDTO> getItemMap(List<Long> itemIds);

    /**
     * Description 根据店铺id查询商品信息
     * <p>
     * Author Hedda*
     *
     * @param storeId
     * @param page
     * @return
     */
    List<ItemDTO> getItems(Long storeId, Page page);

    /**
     * Description 根据运费id查询商品信息
     * <p>
     * Author Hedda*
     * @param logisticsId
     * @return
     */
    List<ItemDTO> getItemByLogisticsId(Long logisticsId);

    /**
     * Description 根据id查询商品信息，进行排序
     * <p>
     * Author Hedda*
     * @param goodsDTOS
     * @return
     */
    List<GoodsDTO> getGoodsSort(List<GoodsDTO> goodsDTOS);

    /**
     * Description 根据id查询商品信息，进行排序
     * <p>
     * Author Hedda*
     * @param goodsDecorateDTOS
     * @return
     */
    List<GoodsDecorateDTO> getGoodsDecorate(List<GoodsDecorateDTO> goodsDecorateDTOS);

    /**
     * Description 根据店铺id查询商品信息
     * <p>
     * Author Hedda*
     * @param storeId 店铺id
     * @return
     */
    List<StoreGoodsDTO> getStoreGoods(Long storeId);


    /**
     * Description 根据店铺id查询商品信息，冻结（解冻）本店铺的商品
     * <p>
     * Author Hedda*
     *
     * @param storeId 店铺id
     * @return
     */
    boolean updateGoods(Long storeId, Integer frozenFlag);

    /**
     * Description 批量添加商品
     *
     * @param goodsListDTOS
     * @return
     */
    boolean saveItems(List<ApiGoodsDTO> goodsListDTOS);

    /**
     * Description 根据店铺id查询商品
     *
     * @param apiGoodsQueryDTO 查询条件
     * @return
     */
    PageInfo<ApiGoodsDTO> getGoodsList(ApiGoodsQueryDTO apiGoodsQueryDTO);

    /**
     * Description 根据商品ids查询除此以外的商品
     *
     * @param itemIds 商品集合
     * @return
     */
    List<ItemDTO> getItemDTOs(List<Long> itemIds, Long storeId, Page page);

    /**
     * * Description 根据店铺ids查询商品
     *
     * @param storeIds 店铺ID
     * @return
     */
    List<ItemDTO> getStoreItems(List<Long> storeIds, Page page, Integer status);

    /**
     * Description 根据商品ids批量查询商品库存
     *
     * @param itemIds
     * @return
     */
    Map<Long, Long> getStockNumberMap(List<Long> itemIds);

    /**
     * Description  查询下架商品
     *
     * @param itemIds
     * @return
     */
    List<OutGoodsDTO> getOutGoods(List<Long> itemIds);

    /**
     * Description  es查询商品
     *
     * @param num
     * @param limit
     * @return
     */
    List<ItemEsDTO> getItemEs(Long num, Integer limit);

    void updateMinPrice(Long itemId);

    void updateMinPrice(List<Long> itemIds);
}
