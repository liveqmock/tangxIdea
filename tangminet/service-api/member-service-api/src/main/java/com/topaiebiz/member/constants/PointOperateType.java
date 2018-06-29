package com.topaiebiz.member.constants;

import lombok.Getter;

/**
 * Created by ward on 2018-01-15.
 */
public enum PointOperateType {
    LOGIN("points_login", "会员每天登陆"),
    REGISTER("points_reg", "会员注册(新会员注册成功获取50积分)"),
    // CHCEKIN("points_checkin", "会员签到"),
    CHCEKIN("points_checkin", "会员签到(每日签到获取1积分)"),
    //COMMENTS("points_comments", "订单商品评论"),
    COMMENTS("points_comments", "订单评价（确认收货后评价订单获取积分）"),
    BUY_CONSUME("pt_rmb", "积分抵现（购买商品抵现）"),
    BUY_CONSUME_AWARD("in125", "消费奖励"),
    // CRM_TO_MMG("integral_convert", "积分换购（贝因美crm积分转妈妈购积分）"),
    CRM_TO_MMG("integral_convert", "积分转换（贝因美积分转妈妈购积分）"),
    ACTIVITY_CONSUME("out120", "抽奖扣积分"),
    POINT_REFUND("pt_rtn", "积分回退（订单取消或退货抵现积分退回）"),
    @Deprecated
    POINT_REDRESS("point_redress", "积分补录"),
    REDRESS("redress", "积分补录"),
    ACTIVITY_REDUCE("activity_reduce","活动消耗"),
    ACTIVITY_PRIZE("activity_prize","活动奖励");
    // CANCEL_ORDER_REFUND("cancel_order_refund", "积分回退（订单取消抵现积分退回）");


    @Getter
    private String operateType;

    @Getter
    private String operateDesc;


    PointOperateType(String operateType, String operateDesc) {
        this.operateType = operateType;
        this.operateDesc = operateDesc;
    }

    public static PointOperateType get(String code) {
        for (PointOperateType temp : values()) {
            if (temp.operateType.equals(code)) {
                return temp;
            }
        }
        return null;
    }
}
