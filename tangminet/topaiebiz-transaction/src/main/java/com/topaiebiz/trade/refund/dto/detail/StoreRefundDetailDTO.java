package com.topaiebiz.trade.refund.dto.detail;

import com.topaiebiz.trade.dto.order.OrderAddressDTO;
import com.topaiebiz.trade.refund.dto.common.RefundGoodDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Description 商家--售后订单详情DTO
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
public class StoreRefundDetailDTO implements Serializable {

    private static final long serialVersionUID = -3015923285065171268L;

    /**
     * 售后订单ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 售后状态
     */
    private Integer refundState;

    /**
     * 0：非整单退，1：整单退
     */
    private Integer refundRange;

    /**
     * 售后类型
     */
    private Integer refundType;

    /**
     * 售后原因
     */
    private String refundReason;

    /**
     * 退还得金额
     */
    private BigDecimal refundPrice;

    /**
     * 退货数量
     */
    private Integer refundGoodsNum;

    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 售后说明
     */
    private String refundDescription;

    /**
     * 订单支付得金额
     */
    private BigDecimal orderPayPrice;

    /**
     * 收货人信息
     */
    private OrderAddressDTO orderAddressDTO;

    /**
     * 售后的商品明细
     */
    private List<RefundGoodDTO> refundGoodDtos;

    /**
     * 拒绝说明
     */
    private String refuseDescription;

    /**
     * 退货凭证数组
     */
    private String[] refundImgs;

    /**
     * 售后是否能被拒绝  1：不能被拒绝， 0：能被拒绝
     */
    private Integer cantRefuse;

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
