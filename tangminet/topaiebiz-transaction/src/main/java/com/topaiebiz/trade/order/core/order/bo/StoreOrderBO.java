package com.topaiebiz.trade.order.core.order.bo;

import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.trade.order.util.MathUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-10 16:10
 */
@Data
public class StoreOrderBO {

    /**
     * 下单后的表主键ID
     */
    private Long orderId;

    /**
     * 店铺信息
     */
    private StoreInfoDetailDTO store;

    /**
     * 商品列表
     */
    private List<StoreOrderGoodsBO> goodsList = new ArrayList<>();

    /**
     * 商品总价
     */
    private BigDecimal goodsAmount = BigDecimal.ZERO;

    /**
     * 商品邮费
     */
    private BigDecimal goodsFreight = BigDecimal.ZERO;

    /**
     * 店铺优惠
     */
    private PromotionDTO storePromotion;
    /**
     * 店铺优惠券活动。
     */
    private PromotionDTO storeCoupon;
    /**
     * 包邮活动
     */
    private PromotionDTO freightPromotion;
    /**
     * 单品优惠总额
     */
    private BigDecimal totalGoodsDiscount = BigDecimal.ZERO;

    /**
     * 店铺优惠
     */
    private BigDecimal storeDiscount = BigDecimal.ZERO;
    /**
     * 店铺优惠金额
     */
    private BigDecimal storeCouponDiscount = BigDecimal.ZERO;
    /**
     * 包邮优惠
     */
    private BigDecimal freightDiscount = BigDecimal.ZERO;

    /**
     * 平台优惠分摊金额
     */
    private BigDecimal platformDiscount = BigDecimal.ZERO;

    /**
     * 所有优惠总额 = 单品优惠总额 + 店铺优惠 + 包邮优惠 + 平台优惠
     */
    private BigDecimal totalDiscountAmount = BigDecimal.ZERO;

    /**
     * 订单总额 = 商品价格 + 运费
     */
    private BigDecimal orderTotal = BigDecimal.ZERO;

    /**
     * 实际应付金额 = 订单总额 - 单品优惠总额 - 店铺优惠 - 平台优惠
     */
    private BigDecimal payPrice = BigDecimal.ZERO;

    public void updatePrice() {
        //商品总额
        goodsAmount = BigDecimal.ZERO;
        //商品优惠总额
        totalGoodsDiscount = BigDecimal.ZERO;
        //平台优惠
        for (StoreOrderGoodsBO goodsBO : goodsList) {
            goodsAmount = goodsAmount.add(goodsBO.getGoodsAmount());
            totalGoodsDiscount = totalGoodsDiscount.add(goodsBO.getGoodsDiscount());
            platformDiscount = platformDiscount.add(goodsBO.getPlatformDiscount());
        }

        //商品运费，取当前订单中运费最大的商品
        goodsFreight = BigDecimal.ZERO;
        for (StoreOrderGoodsBO goodsBO : goodsList) {
            if (MathUtil.greateEq(goodsBO.getFinalFreight(),goodsFreight)){
                goodsFreight = goodsBO.getFinalFreight();
            }
        }

        //订单总价 = 商品总价 + 运费总价
        orderTotal = goodsAmount.add(goodsFreight);

        //总优惠 = 单品优惠总额 + 店铺优惠 + 包邮优惠 + 平台优惠
        totalDiscountAmount = totalGoodsDiscount.add(storeDiscount).add(storeCouponDiscount).add(platformDiscount);

        //应付金额
        payPrice = orderTotal.subtract(totalDiscountAmount);
    }
}