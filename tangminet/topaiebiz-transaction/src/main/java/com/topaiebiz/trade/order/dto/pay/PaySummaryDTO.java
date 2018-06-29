package com.topaiebiz.trade.order.dto.pay;

import lombok.Data;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-01-08 21:10
 */
@Data
public class PaySummaryDTO {
    /*** 支付ID ***/
    private Long payId;

    /*** 订单应付总金额 ***/
    private BigDecimal orderAmount = BigDecimal.ZERO;
    /*** 当前应支付金额 ***/
    private BigDecimal needPay = BigDecimal.ZERO;
    /*** 当前站内支付允许支付总金额 ***/
    private BigDecimal maxPkgPay = BigDecimal.ZERO;

    /*** 已经支付摘要 ***/
    private PayedSummaryDTO payedSummary;
    /*** 用户资产摘要  ***/
    private MemberAssetDTO memberAsset;
    /**
     * 海淘订单
     */
    private Boolean haitao = false;
    /*** 是否有支付密码 ***/
    private Boolean hasPayPwd;
    /*** 用户手机号 ****/
    private String mobile;

    public void updateNeedPay() {
        if (payedSummary != null) {
            //应付金额 = 总订单金额 - 已经金额
            needPay = orderAmount.subtract(payedSummary.getTotalPayed());
        } else {
            //应付金额 = 总订单金额
            needPay = orderAmount;
        }
    }

    /**
     *
     */
    public void initMemberAsset() {
        if (memberAsset == null) {
            memberAsset = new MemberAssetDTO();
        }
    }
}