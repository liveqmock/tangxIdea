package com.topaiebiz.trade.order.core.order.bo;

import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.dto.PromotionGoodsDTO;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.trade.order.util.PromotionUtil;
import lombok.Data;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-01-10 16:12
 */
@Data
public class StoreOrderGoodsBO {

    /**
     * 商品SKU
     */
    private GoodsSkuDTO goods;

    /**
     * 商品数量
     */
    private Long goodsNum;

    /**
     * 商品优惠
     */
    private PromotionDTO goodsPromotion;

    /**
     * 商品优惠ID
     */
    private Long promotionId;

    /**
     * 商品运费
     */
    private BigDecimal freight = BigDecimal.ZERO;
    /**
     * 运费优惠
     */
    private BigDecimal freightDiscount = BigDecimal.ZERO;

    /**
     * 单品优惠总额
     */
    private BigDecimal goodsDiscount = BigDecimal.ZERO;
    /**
     * 店铺优惠总额
     */
    private BigDecimal storeDiscount = BigDecimal.ZERO;

    /**
     * 店铺券优惠总额
     */
    private BigDecimal storeCouponDiscount = BigDecimal.ZERO;

    /**
     * 平台优惠总额
     */
    private BigDecimal platformDiscount = BigDecimal.ZERO;
    /**
     * 优惠总额
     */
    protected BigDecimal totalPromotion = BigDecimal.ZERO;

    /**
     * 商品原始总额
     */
    private BigDecimal goodsAmount = BigDecimal.ZERO;

    /**
     * 此商品最终支付价格
     */
    private BigDecimal payAmount = BigDecimal.ZERO;
    private BigDecimal finalFreight = BigDecimal.ZERO;

    public void updatePrice() {
        //商品总额
        goodsAmount = MathUtil.multiply(goods.getPrice(), goodsNum);

        //计算单品优惠
        PromotionGoodsDTO promotionGoodsDTO = PromotionUtil.findPromotionGoods(goodsPromotion, goods.getId());
        if (promotionGoodsDTO != null) {
            BigDecimal promotionPrice = MathUtil.multiply(promotionGoodsDTO.getPromotionPrice(), goodsNum);

            //单品优惠幅度
            goodsDiscount = goodsAmount.subtract(promotionPrice);
        }

        //优惠总额
        totalPromotion = goodsDiscount.add(storeDiscount).add(storeCouponDiscount).add(platformDiscount);

        //计算最终价格
        payAmount = goodsAmount.subtract(totalPromotion);

        //最终运费
        finalFreight = freight.subtract(freightDiscount);
    }
}