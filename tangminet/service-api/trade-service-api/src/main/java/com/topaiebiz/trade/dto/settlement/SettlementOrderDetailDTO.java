package com.topaiebiz.trade.dto.settlement;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-03-27 16:44
 */
@Data
public class SettlementOrderDetailDTO implements Serializable{
    private static final long serialVersionUID = -2102862718389116965L;
    private Long id;
    /**
     * 订单id
     */
    private Long orderId;
    private Long memberId;
    /**
     * 订单状态
     */
    private Integer orderState;
    /**
     * 商品id
     */
    private Long itemId;

    /**
     * skuId
     */
    private Long skuId;

    /**
     * spuId
     */
    private Long spuId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品条形码
     */
    private String barCode;

    /**
     * 商品属性集
     */
    private String fieldValue;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 商品原单价
     */
    private BigDecimal goodsPrice;

    /**
     * 商品数量
     */
    private Long goodsNum;

    /**
     * 商品原总价
     */
    private BigDecimal totalPrice;

    /**
     * 所使用的营销活动
     */
    private Long promotionId;

    /**
     * 优惠金额
     */
    private BigDecimal discount;

    /**
     * 优惠数据详情(含店铺、平台、单品优惠详情)
     */
    private String promotionDetail;

    /**
     * 实际运费
     */
    private BigDecimal freight;

    /**
     * 优惠后应支付金额
     */
    private BigDecimal payPrice;

    /**
     * 支付详情(站内支付+站外支付详情)
     */
    private String payDetail;

    /**
     * 商品货号
     */
    private String goodsSerial;

    /** 佣金比例。小数形式。平台收取商家的佣金。*/
    private BigDecimal brokerageRatio;

    /**
     * 订单支付比例限制
     */
    private BigDecimal scoreRate;

    private BigDecimal taxRate;
}