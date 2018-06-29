package com.topaiebiz.giftcard;

/**
 * @description: 业务常量
 * @author: Jeff Chen
 * @date: created in 下午8:39 2018/2/9
 */
public class BizConstants {

    /**
     * 填写订单
     */
    public static final Integer ORDER_REQ_FILL_IN= 1;
    /**
     * 生单
     */
    public static final Integer ORDER_REQ_PLACE_ORDER = 2;

    public static final String SYS_USER_ORDER = "系统下单";

    public static final String BINDING_DIRECT = "一键绑定";
    public static final String GIVEN_GET_USER = "转赠领取";
    public static final String CARD_PWD_USER = "卡密绑定";

    public static final String TIME_TASK_USER = "定时任务";

    /**
     * 针对用户id的下单分控开关
     */
    public static final String CARD_ORDER_RISK_USER = "card_order_risk_user";

    /**
     * 礼卡绑定密码输入次数prefix
     */
    public static final String BIND_CARD_KEY = "BIND_CARD_";
}
