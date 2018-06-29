package com.topaiebiz.promotion.api;

import com.topaiebiz.promotion.dto.PromotionConsumeDTO;
import com.topaiebiz.promotion.dto.PromotionDTO;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by Joe on 2018/1/8.
 */
public interface PromotionApi {

    /**
     * 批量单品活动查询
     *
     * @return
     */
    Map<Long, List<PromotionDTO>> getSkuPromotions(List<Long> skuIds);

    Map<Long, PromotionDTO> getSkuPromotionMap(List<Long> skuIds);

    /**
     * 批量店铺活动查询
     *
     * @return
     */
    Map<Long, List<PromotionDTO>> getStorePromotions(List<Long> storeIds);

    /**
     * 用户领取的店铺优惠券清单
     *
     * @param memberId
     * @param storeId
     * @return
     */
    List<PromotionDTO> getStoreCoupons(Long memberId, Long storeId);

    /**
     * 批量ID查询
     *
     * @return
     */
    Map<Long, PromotionDTO> getPromotionByIds(List<Long> promotionIds);

    /**
     * 查询所有可用平台活动
     *
     * @return
     */
    List<PromotionDTO> getPlatformPromotions();

    /**
     * 用户平台优惠券清单
     *
     * @param memberId
     * @return
     */
    List<PromotionDTO> getPlatformPromotions(Long memberId);

    /**
     * 优惠券消费
     *
     * @param memberId
     * @param promotionConsumeDTO
     * @return
     */
    Boolean usePromotions(Long memberId, PromotionConsumeDTO promotionConsumeDTO);

    /**
     * 优惠券消费回退
     *
     * @param memberId
     * @param promotionConsumeDTO
     * @return
     */
    Boolean backPromotions(Long memberId, PromotionConsumeDTO promotionConsumeDTO);

    /**
     * 获取会员优惠券总数量
     *
     * @param memberId
     * @return
     */
    Integer getCouponNum(Long memberId, Long storeId);

    /**
     * 根据商品sku查询秒杀信息
     *
     * @param goodsSkuId
     * @return
     */
    PromotionDTO getSeckill(Long goodsSkuId);

    /**
     * 根据店铺id与活动类型获取店铺活动集合
     *
     * @param storeId
     * @return
     */
    List<PromotionDTO> getStorePromotionList(Long storeId, Integer type);

    /**
     * 获取单品折扣与一口价
     *
     * @param goodsSkuId
     * @return
     */
    List<PromotionDTO> getSinglePromotions(Long goodsSkuId);

    /**
     * 判断会员优惠券列表是否可用
     *
     * @param memberId
     * @param couponPromIds
     * @return
     */
    Boolean checkHoldStatus(Long memberId, List<Long> couponPromIds);

    /**
     * 商品下架
     *
     * @param itemIds
     */
    void goodsSuspendSales(Long[] itemIds);

    /**
     * 根据goodsSkuId查询平台优惠券
     *
     * @param itemId
     * @return
     */
    List<PromotionDTO> getPlatFormCouponBySku(Long itemId, Long storeId) throws ParseException;

    /**
     * @param promotionId
     * @Author: tangx.w
     * @Description: 根据promotionId查询已经圈中的店铺id
     * @Date: 2018/5/4 10:49
     */
    List<Long> getStoreIdListByPromotionId(Long promotionId);

    /**
     * @param promotionId
     * @Author: tangx.w
     * @Description: 根据promotionId查询已经被选中的item
     * @Date: 2018/5/2 16:38
     */
    List<Long> getSelectItemIds(Long promotionId);

    /**
     * 判读是否存在限时秒杀活动
     *
     * @param itemIds
     */
    Boolean hasSecKill(List<Long> itemIds);
}
