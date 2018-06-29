package com.topaiebiz.trade.dto.refund;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/***
 * @author yfeng
 * @date 2018-03-26 10:55
 */
@Data
public class RefundDTO implements Serializable {
    private static final long serialVersionUID = -401889978060024541L;
    private Long id;
    /**
     * 所属店铺ID
     */
    private Long storeId;

    /**
     * 所属店铺名称
     */
    private String storeName;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 所属会员
     */
    private Long memberId;

    /**
     * 所属会员名称
     */
    private String memberName;

    /**
     * 购货订单号
     */
    private Long orderId;

    /**
     * 处理状态（0待处理；1已处理；2已拒绝）
     */
    private Integer processState;

    /**
     * 售后状态（1：申请仅退款；2：申请退货退款；3：待寄回商品；4：待签收商品；5：已退款；6已拒绝）
     */
    private Integer refundState;

    /**
     * 0：非整单退，1：整单退
     */
    private Integer refundRange;

    /**
     * 退款类型（0:仅退款 / 1:退货退款）
     */
    private Integer refundType;

    /**
     * 是否需要平台介入（0：不需要，1：需要）
     */
    private Integer pfInvolved;

    /**
     * 退货理由描述
     */
    private String refundReason;

    /**
     * 退货理由code
     */
    private Integer refundReasonCode;

    /**
     * 售后说明
     */
    private String refundDescription;

    /**
     * 售后商品件数
     */
    private Integer refundGoodsNum;

    /**
     * 总退款（包含第三方，积分，余额等）
     */
    private BigDecimal refundPrice;

    /**
     * 退还的三分支付金额
     */
    private BigDecimal refundThirdAmount;

    /**
     * 退还的运费
     */
    private BigDecimal refundFreight;

    /**
     * 退还的美礼卡金额
     */
    private BigDecimal refundCardPrice;

    /**
     * 退还的积分抵扣金额
     */
    private BigDecimal refundIntegralPrice;

    /**
     * 退还的用户余额
     */
    private BigDecimal refundBalance;

    /**
     * 退货凭证图片1
     */
    private String refundImg1;

    /**
     * 退货凭证图片2
     */
    private String refundImg2;

    /**
     * 退货凭证图片3
     */
    private String refundImg3;

    /**
     * 售后申请时间
     */
    private Date refundTime;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 会员寄回商品时间
     */
    private Date shipmentsTime;

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

    /**
     * 调用第三方退款时间（审核通过已寄回得商品）
     */
    private Date expenditureTime;

    /**
     * 售后完成时间
     */
    private Date completeTime;

    /**
     * 退款返回的第三方流水号
     */
    private String callBackNo;

    /**
     * 取消时间
     */
    private Date cancelTime;
    /**
     * 拒绝说明
     */
    private String refuseDescription;

    private Date createdTime;
}