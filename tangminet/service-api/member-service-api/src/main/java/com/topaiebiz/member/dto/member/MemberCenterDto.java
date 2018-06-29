package com.topaiebiz.member.dto.member;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberCenterDto {


    private Integer id;
    /**
     * 会员名称
     */
    private String userName;

    /**
     * 会员昵称
     */
    private String nickName;

    /**
     * 手机号
     */
    private String telephone;

    /**
     * 小会员头像
     */
    private String smallIcon;


    private Integer point = 0;

    /**
     * balance
     * 预存款可用金额
     */
    private BigDecimal balance = BigDecimal.ZERO;


    /**
     * 优惠券数量
     */
    private Integer couponCount = 0;

    /**
     * 订单未评论数
     */
    private Integer orderUncomment;
    /**
     * 订单未支付数
     */
    private Integer orderUnpay;
    /**
     * 订单未收货
     */
    private Integer orderUunreceived;
    /**
     * 订单未发货
     */
    private Integer orderUnshipped;
    /**
     * 订单退款
     */
    private Integer orderRefund = 0;

    private Integer cardNum = 0;

    private BigDecimal cardBalance = BigDecimal.ZERO;

    private Boolean hasSetPaypwd = false;


}
