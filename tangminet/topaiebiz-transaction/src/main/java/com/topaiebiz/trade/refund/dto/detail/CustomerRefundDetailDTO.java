package com.topaiebiz.trade.refund.dto.detail;

import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.merchant.dto.merchantReturn.MerchantReturnDTO;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.refund.dto.common.RefundGoodDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Description 售后详情页
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/9 16:46
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
public class CustomerRefundDetailDTO implements Serializable {

    private static final long serialVersionUID = 3885820608069434904L;

    /**
     * 售后订单ID
     */
    private Long id;

    /**
     * 售后原因
     */
    private String refundReason;

    /**
     * 售后说明
     */
    private String refundDescription;

    /**
     * 售后金额
     */
    private BigDecimal refundPrice;

    /**
     * 售后运费
     */
    private BigDecimal refundFreight;

    /**
     * 售后商品数量
     */
    private Integer refundGoodsNum;

    /**
     * 售后时间
     */
    private Date refundTime;

    /**
     * 售后状态
     */
    private Integer refundState;

    /**
     * 退款类型（0:仅退款 / 1:退货退款）
     */
    private Integer refundType;

    /**
     * 退款时间
     */
    private Date expenditureTime;

    /**
     * 取消时间
     */
    private Date cancelTime;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 拒绝说明
     */
    private String refuseDescription;

    /**
     * 售后商品详情集合
     */
    private List<RefundGoodDTO> refundGoodDtos;

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
     * 剩余时间秒
     */
    private Long auditTimeLeft;

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
     * 寄回的东西
     */
    private MerchantReturnDTO merchantReturnDTO;

    /**
     * 展现申诉按钮
     */
    private Integer showAppealBtn = OrderConstants.OrderRefundStatus.ALL_REFUND_NO;

    /**
     * 平台介入中
     */
    private Integer refundIntervening = Constants.Refund.PLATFORM_IS_NOT_INVOLVED;

}
