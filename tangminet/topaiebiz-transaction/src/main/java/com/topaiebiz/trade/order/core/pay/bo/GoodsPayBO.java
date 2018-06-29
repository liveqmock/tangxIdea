package com.topaiebiz.trade.order.core.pay.bo;

import com.topaiebiz.trade.order.util.MathUtil;
import lombok.Data;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-01-19 12:57
 */
@Data
public class GoodsPayBO extends PkgDispatchBO {

    /**
     * 订单详情ID
     */
    private Long detailId;
    /**
     * 商品SKU ID
     */
    private Long skuId;
    /**
     * Item Id
     */
    private Long itemId;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品应付金额
     */
    private BigDecimal payPrice;

    /**
     * 积分支付比例限制
     */
    private BigDecimal scoreRate;

    @Override
    public boolean isDispatchFinished() {
        return MathUtil.sameValue(payPrice, getPkgDispatchAmount());
    }

    @Override
    public BigDecimal getUndispatchPrice() {
        return payPrice.subtract(getPkgDispatchAmount());
    }
}