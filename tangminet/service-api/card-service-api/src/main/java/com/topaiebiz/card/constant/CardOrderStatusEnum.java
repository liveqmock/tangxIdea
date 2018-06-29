package com.topaiebiz.card.constant;

/**
 * @description: 礼卡订单状态（继承老系统）
 * @author: Jeff Chen
 * @date: created in 下午5:59 2018/1/8
 */
public enum CardOrderStatusEnum {
    //订单状态：0-已取消 10-未支付 20-已付款 30-已发货 40-已完成

    CANCELED(0, "已取消"),
    UNPAID(10, "未支付"),
    PAID(20, "已付款"),
    SHIPPED(30, "已发货"),
    FINISHED(40, "已完成"),
    /**
     * 新系统增加的状态
     */
    DISABLE(50, "已失效");


    private int statusCode;
    private String statusDesc;

    CardOrderStatusEnum(int statusCode, String statusDesc) {
        this.statusCode = statusCode;
        this.statusDesc = statusDesc;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }
}
