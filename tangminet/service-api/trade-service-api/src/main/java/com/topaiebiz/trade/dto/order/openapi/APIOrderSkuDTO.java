package com.topaiebiz.trade.dto.order.openapi;

import com.topaiebiz.trade.dto.order.OrderGoodsDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description 订单明细
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/25 16:47
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
@NoArgsConstructor
public class APIOrderSkuDTO implements Serializable {
    private static final long serialVersionUID = 5243008257399192868L;

    /**
     * 商品ItemID, 商品ItemCODE, 商品名称, 商品货号, 商品数量
     */
    private Long goodsSkuId;
    private Long goodsItemId;
    private String goodsItemCode;
    private String goodsName;
    private String goodsSerial;
    private Integer goodsNum;


    /**
     * 商品单价, 商品总价, 商品被优惠总金额, 商品实际支付金额
     */
    private BigDecimal goodsPrice;
    private BigDecimal goodsTotalAmount;
    private BigDecimal goodsDiscountAmount;
    private BigDecimal goodsPayAmount;

    /**
     * 售后状态, 备注
     */
    private Integer refundState;
    private String memo;


    public APIOrderSkuDTO(OrderGoodsDTO orderGoodsDTO) {
        this.goodsSkuId = orderGoodsDTO.getSkuId();
        this.goodsItemId = orderGoodsDTO.getItemId();
        this.goodsItemCode = orderGoodsDTO.getItemCode();
        this.goodsName = orderGoodsDTO.getName();
        this.goodsSerial = orderGoodsDTO.getGoodsSerial();
        this.goodsPrice = orderGoodsDTO.getGoodsPrice();
        this.goodsTotalAmount = orderGoodsDTO.getTotalPrice();
        this.goodsDiscountAmount = orderGoodsDTO.getDiscount();
        this.goodsPayAmount = orderGoodsDTO.getPayPrice();
        this.goodsNum = orderGoodsDTO.getGoodsNum();
        this.refundState = orderGoodsDTO.getRefundState();
        this.memo = orderGoodsDTO.getMemo();
    }

}
