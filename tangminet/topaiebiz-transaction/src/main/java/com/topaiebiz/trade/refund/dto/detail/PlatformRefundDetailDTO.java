package com.topaiebiz.trade.refund.dto.detail;

import com.topaiebiz.trade.dto.order.OrderAddressDTO;
import com.topaiebiz.trade.refund.dto.common.RefundGoodDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Description 平台--售后订单详情DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/10 16:42
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
public class PlatformRefundDetailDTO implements Serializable {

    private static final long serialVersionUID = -3015923285065171268L;

    /**
     * 售后订单ID
     */
    private Long id;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 所属店铺名称
     */
    private String storeName;

    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 售后状态
     */
    private Integer refundState;

    /**
     * 售后类型
     */
    private Integer refundType;

    /**
     * 售后原因
     */
    private String refundReason;

    /**
     * 退还的美礼卡金额
     */
    private BigDecimal refundCardPrice;

    /**
     * 退还的积分
     */
    private Integer refundIntegral;

    /**
     * 退还得金额
     */
    private BigDecimal refundPrice;

    /**
     * 退货数量
     */
    private Integer refundGoodsNum;

    /**
     * 退款途径
     */
    private String orderPayType;

    /**
     * 售后说明
     */
    private String refundDescription;

    /**
     * 订单支付得金额
     */
    private BigDecimal orderPayPrice;

    /**
     * 订单总金额
     */
    private BigDecimal orderTotalPrice;

    /**
     * 退货凭证数组
     */
    private String[] refundImgs;

    /**
     * 售后的商品明细
     */
    private List<RefundGoodDTO> refundGoodDtos;

    /**
     * 收货人信息
     */
    private OrderAddressDTO orderAddressDTO;

    /**
     * 拒绝说明
     */
    private String refuseDescription;

    /**
     * 仅退款，买家备注
     */
    private String orderMemo;

    /**
     * 物流公司
     */
    private Long logisticsCompanyId;

    /**
     * 物流公司
     */
    private String logisticsCompanyName;

    /**
     * 物流编号
     */
    private String logisticsNo;

}
