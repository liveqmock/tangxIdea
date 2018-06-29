package com.topaiebiz.goods.sku.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.goods.dto.sku.ItemEsDTO;
import com.topaiebiz.goods.sku.dto.ItemDto;
import com.topaiebiz.goods.sku.dto.ItemSellDto;
import com.topaiebiz.goods.sku.dto.app.ItemCustomerDto;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description 商品信息dao
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年10月3日 下午7:11:01
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Mapper
public interface ItemDao extends BaseDao<ItemEntity> {

    /**
     * Description 商品信息列表分页检索出售中的商品
     * <p>
     * Author Hedda
     *
     * @param page    分页单位
     * @param itemDto 商品信息dto
     * @return
     */
    List<ItemDto> selectListItemDto(Page<ItemDto> page, ItemDto itemDto);

    /**
     * Description 商品信息列表分页检索仓库中的商品
     * <p>
     * Author Hedda
     *
     * @param page    分页单位
     * @param itemDto 商品信息dto
     * @return
     */
    List<ItemDto> selectListStoreItemDto(Page<ItemDto> page, ItemDto itemDto);

    /**
     * Description 逻辑删除商品信息
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     */
    Integer deleteItem(Long[] id);

    /**
     * Description 根据id查询商品信息
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     */
    ItemDto selectItemById(Long id);

    /**
     * Description 商家商品信息列表分页检索
     * <p>
     * Author Hedda
     *
     * @param page    分页单位
     * @param itemDto 商品信息dto
     * @return
     */
    List<ItemDto> selectListMerchantItemDto(Page<ItemDto> page, ItemDto itemDto);

    /**
     * Description 根据类目id查询是否有有item商品
     * <p>
     * Author Hedda
     *
     * @param id
     * @return
     */
    List<ItemEntity> selectItemByBelongCategory(Long id);

    /**
     * Description 根据商品id查询商品销售数量
     * <p>
     * Author Hedda
     *
     * @param id
     * @return
     */
    Long selectSalesVolomeById(Long id);

    /**
     * Description 商家根据id查询商品信息
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     */
    ItemDto selectMerchantItemById(Long id);

    /**
     * Description 根据商品id查询商品库存
     * <p>
     * Author Hedda
     *
     * @param id
     * @return
     */
    Long selecStockNumbertById(Long id);

    /**
     * Description 平台统计管理商品销售情况
     * <p>
     * Author Hedda
     *
     * @param page
     * @param itemSellDto
     * @return
     */
    List<ItemSellDto> selectSellGoodsList(Page<ItemSellDto> page, ItemSellDto itemSellDto);

    /**
     * Description 根据商品goodsId查询销售数量
     * <p>
     * Author Hedda
     *
     * @param goodsId 商品skuId
     * @return
     */
    List<ItemSellDto> selectSaleNumber(Long goodsId);

    /**
     * Description 根据商品goodsId查询购买人数
     * <p>
     * Author Hedda
     *
     * @param goodsId 商品skuId
     * @return
     */
    Integer selectPaymentPeoples(Long goodsId);

    /**
     * Description 平台统计管理商品销售情况
     * <p>
     * Author Hedda
     *
     * @param page
     * @param itemSellDto
     * @return
     */
    List<ItemSellDto> selectMerchantSellGoodsList(Page<ItemSellDto> page, ItemSellDto itemSellDto);

    /**
     * Description 平台端统计管理商品类目销售分析
     * <p>
     * Author Hedda
     *
     * @param itemSellDto
     * @return
     */
    List<ItemSellDto> selectSellGoodsCategoryList(Page<ItemSellDto> page, ItemSellDto itemSellDto);

    /**
     * Description 商家端统计管理商品类目销售分析
     * <p>
     * Author Hedda
     *
     * @param page
     * @param itemSellDto
     * @return
     */
    List<ItemSellDto> selectStoreSellGoodsCategoryList(Page<ItemSellDto> page, ItemSellDto itemSellDto);

    /**
     * Description 根据店铺id和类目id查询商品
     * <p>
     * Author Hedda
     *
     * @param categoryId 类目id
     * @param storeId    店铺id
     * @return
     */
    List<ItemEntity> selectItemByCategoryIdAndStoreId(@Param("categoryId") Long categoryId, @Param("storeId") Long storeId);

    /**
     * Description app端根据年龄段和商家查询商品
     * <p>
     * Author Hedda
     *
     * @param itemCustomerDto 商品itemDto
     * @return
     */
    List<ItemCustomerDto> selectGoodsList(ItemCustomerDto itemCustomerDto, Page<ItemCustomerDto> page);

    /**
     * Description 根据店铺id查询商品信息
     * <p>
     * Author Hedda*
     *
     * @param page
     * @param belongStore
     * @return
     */
    List<ItemDTO> selectItems(Page<ItemDTO> page, ItemDTO belongStore);

    Integer countStore(@Param("logisticsId") Long logisticsId, @Param("storeId") Long storeId);

    Integer updateSales(@Param("salesVolume") Long salesVolume,@Param("itemId") Long itemId);

    List<ItemEsDTO> selectItemEs(@Param("num") Long num, @Param("limit") Integer limit);
}
