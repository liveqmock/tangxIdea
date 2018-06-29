package com.topaiebiz.goods.sku.service;

import com.baomidou.mybatisplus.service.IService;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.category.backend.dto.BackendCategorysDto;
import com.topaiebiz.goods.comment.dto.GoodsSkuCommentDto;
import com.topaiebiz.goods.sku.dto.*;
import com.topaiebiz.goods.sku.dto.app.GoodsPraiseDto;
import com.topaiebiz.goods.sku.dto.app.GoodsSkuCommentAppDto;
import com.topaiebiz.goods.sku.dto.app.ItemAppDto;
import com.topaiebiz.goods.sku.dto.app.ItemCustomerDto;
import com.topaiebiz.goods.sku.entity.ItemEntity;

import java.util.List;

/**
 * Description 商品sku接口
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年10月3日 下午7:05:40
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface ItemService extends IService<ItemEntity> {

    /**
     * Description 商品item批量逻辑删除
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    Integer removeItems(Long[] id) throws GlobalException;

    /**
     * Description 商品item批量上架
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    Integer putItems(Long[] id) throws GlobalException;

    /**
     * Description 商品item批量下架
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    Integer outItems(Long[] id) throws GlobalException;

    /**
     * Description 商品item信息修改
     * <p>
     * Author Hedda
     *
     * @param itemDto         商品item信息
     * @param itemPictureDtos 商品item图片信息
     * @param goodsSkuDtos    商品sku信息
     * @return
     * @throws GlobalException
     */
    Integer modifyItem(ItemDto itemDto, List<ItemPictureDto> itemPictureDtos, List<GoodsSkuDto> goodsSkuDtos)
            throws GlobalException;

    /**
     * Description 根据id查询item
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     */
    ItemDto findItemById(Long id) throws GlobalException;

    /**
     * Description 根据id查询 商品属性
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     */
    List<GoodsSkuDto> findGoodsSkuById(Long id) throws GlobalException;

    /**
     * Description 根据id查询商品图片
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     */
    List<ItemPictureDto> findItemPictureById(Long id) throws GlobalException;

    /**
     * Description 商家站点管理商品列表
     * <p>
     * Author Hedda
     *
     * @param itemDto 商品信息dto
     * @return
     * @throws GlobalException
     */
    PageInfo<ItemDto> getDecorateItem(ItemDto itemDto);

    /**
     * Description 商品item冻结
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    Integer freezeItem(Long[] id) throws GlobalException;

    /**
     * Description 商品item解冻
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    Integer unFreezeItem(Long[] id) throws GlobalException;

    /**
     * Description 商家商品信息列表分页检索
     * <p>
     * Author Hedda
     *
     * @param itemDto 商品信息dto
     * @return
     */
    PageInfo<ItemDto> getListMerchantItemDto(ItemDto itemDto);

    /**
     * Description 平台查询最近使用类目
     * <p>
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    List<BackendCategorysDto> getItemRecentlyCategoryList();

    /**
     * Description 根据商品id和销售数量计算商品剩余库存
     * <p>
     * Author Hedda
     *
     * @param skuId       商品sku的id
     * @param salesNumber 商品销售数量
     * @return
     * @throws GlobalException
     */
    Integer modifyGoodsSkuStockNumber(Long skuId, Long salesNumber) throws GlobalException;

    /**
     * Description 根据商品id和销售数量计算商品库存
     * <p>
     * Author Hedda
     *
     * @param skuId       商品sku的id
     * @param salesNumber 商品销售数量
     * @return
     * @throws GlobalException
     */
    Integer addGoodsSkuStockNumber(Long memberId, Long skuId, Long salesNumber) throws GlobalException;

    /**
     * Description 商家商品信息列表分页检索出售中的商品
     * <p>
     * Author Hedda
     *
     * @param itemDto 商品信息dto
     * @return
     */
    PageInfo<ItemDto> getMerchantListItemDto(ItemDto itemDto);

    /**
     * Description 商家商品信息列表分页检索仓库中的商品
     * <p>
     * Author Hedda
     *
     * @param itemDto 商品信息dto
     * @return
     * @throws GlobalException
     */
    PageInfo<ItemDto> getMerchantListStoreItemDto(ItemDto itemDto);

    /**
     * Description 商家商品item信息添加
     * <p>
     * Author Hedda
     *
     * @param itemDto         商品item信息
     * @param itemPictureDtos 商品item图片信息
     * @param goodsSkuDtos    商品sku信息
     * @return
     * @throws GlobalException
     */
    Integer saveMerchantItem(ItemDto itemDto, List<ItemPictureDto> itemPictureDtos, List<GoodsSkuDto> goodsSkuDtos) throws GlobalException;

    /**
     * Description 商家查询最近使用类目
     * <p>
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    List<BackendCategorysDto> getMerchantItemRecentlyCategoryList();

    /**
     * Description 商家根据id查询商品信息
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    ItemDto findMerchantItemById(Long id) throws GlobalException;

    /**
     * Description app端根据年龄段,品牌，分类商家查询商品
     * <p>
     * Author Hedda
     *
     * @param itemCustomerDto 商品dto
     * @return
     * @throws GlobalException
     */
    PageInfo<ItemCustomerDto> getGoodsList(ItemCustomerDto itemCustomerDto) throws GlobalException;

    /**
     * Description app根据商品id查询商品信息
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    ItemAppDto findAppItemById(Long id) throws GlobalException;

    /**
     * Description 平台统计管理商品销售情况
     * <p>
     * Author Hedda
     *
     * @param itemSellDto 商品销售dto
     * @return
     */
    PageInfo<ItemSellDto> getSellGoodsList(ItemSellDto itemSellDto);

    /**
     * Description 商家统计管理商品销售情况
     * <p>
     * Author Hedda
     *
     * @param itemSellDto 商品销售dto
     * @return
     */
    PageInfo<ItemSellDto> getMerchantSellGoodsList(ItemSellDto itemSellDto);

    /**
     * Description 平台端统计管理商品类目销售分析
     * <p>
     * Author Hedda
     *
     * @param itemSellDto 商品销售dto
     * @return
     */
    PageInfo<ItemSellDto> getSellGoodsCategoryList(ItemSellDto itemSellDto);

    /**
     * Description 商家端端统计管理商品类目销售分析
     * <p>
     * Author Hedda
     *
     * @param itemSellDto 商品销售dto
     * @return
     */
    PageInfo<ItemSellDto> getStoreSellGoodsCategoryList(ItemSellDto itemSellDto);

    /**
     * Description 根据商品id查询对应商品评价
     * <p>
     * Author Hedda
     * @param goodsSkuCommentAppDto
     * @return
     */
    PageInfo<GoodsSkuCommentDto> findAppSkuCommentById(GoodsSkuCommentAppDto goodsSkuCommentAppDto);

    /**
     * Description 平台端给商家商品配置佣金比例
     * <p>
     * Author Hedda
     *
     * @param commissionRateDto
     * @return
     * @throws GlobalException
     */
    Integer saveGoodsCommissionRate(CommissionRateDto commissionRateDto);

    /**
     * Description  根据商品id查询商品信息
     * <p>
     * Author Hedda
     *
     * @param id
     * @return
     * @throws GlobalException
     */
    GoodsDto findGoodsById(Long id) throws GlobalException;

    /**
     * Description  根据商品id查询商品信息
     * <p>
     * Author Hedda
     *
     * @param id
     * @return
     * @throws GlobalException
     */
    List<GoodsDto> findItemsById(Long[] id) throws GlobalException;

    /**
     * Description 平台端给商家商品配置积分比例
     * <p>
     * Author Hedda
     *
     * @param integralRatioDto
     * @return
     */
    Integer saveIntegralRatio(List<IntegralRatioDto> integralRatioDto);

    /**
     * Description 平台端商家商品积分比例回显
     * <p>
     * Author Hedda
     *
     * @param id
     * @return
     * @throws GlobalException
     */
    IntegralRatioDto findIdIntegralRatio(Long id) throws GlobalException;

    /**
     * Description 平台端商家类目佣金比例回显
     * <p>
     * Author Hedda
     *
     * @param commissionRateDto
     * @return
     */
    CommissionRateDto findCommissionRate(CommissionRateDto commissionRateDto);

    /**
     * Description 根据商品id查询对应评价等级与条数
     * <p>
     * Author Hedda
     * @param itemId 商品id
     * @return
     */
    GoodsPraiseDto findAppGoodsPraiseById(Long itemId) throws GlobalException;

    /**
     * Description  根据商品ids查询商品信息
     * <p>
     * Author Hedda
     *
     * @param id
     * @return
     */
    List<GoodsDto> findGoodsList(Long[] id);

    /**
     * Description 商家营销活动需要展示的商品
     * <p>
     * Author Hedda
     *
     * @param itemDto
     * @return
     */
    PageInfo<ItemDto> getPromotionItem(ItemDto itemDto);

    /**
     * Description 根据itemId查询商品sku信息
     * <p>
     * Author Hedda
     *
     * @param itemId 商品id
     * @return
     */
    List<GoodsSkusDto> findGoodsSku(Long itemId);
}
