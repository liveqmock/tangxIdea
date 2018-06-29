package com.nebulapaas.common.msg.dto;

/***
 * @author yfeng
 * @date 2018-02-01 20:52
 */
public enum MessageTypeEnum {
    /**
     * 会员登录
     */
    MEMBER_LOGIN,

    /**
     * 购买礼卡
     */
    CARD_BUY,
    /**
     * 分享商品
     */
    SHARE_GOODS,


    /***************************  装修模块事件 **********************************/
    /**
     * 修改模块
     */
    MODIFY_MODULE,
    /**
     * 修改标题商品
     */
    MODIFY_TITLE_ITEM,
    /**
     * 删除商品
     */
    REMOVE_ITEM,
    /**
     * 修改商品
     */
    EDIT_ITEM,
    /**
     * 新增商家模板
     */
    NEW_MERCHANTS_TEMPLATE,
    /**
     * 活动商品
     */
    ACTIVE_ITEM,

    /***************************  交易事件 **********************************/
    /**
     * 订单关闭
     */
    ORDER_CLOSE,
    /**
     * 下单操作
     */
    ORDER_SUBMIT,
    /**
     * 订单完成
     */
    ORDER_COMPLETE,

    /**
     * 订单退款
     */
    ORDER_REFUND,

    /**
     * 订单支付
     */
    ORDER_PAY,

    /***************************  商品事件 **********************************/
    /**
     * 上架
     */
    GOODS_PUT,

    /**
     * 库存变更
     */
    GOODS_UNDERCARRIAGE,

    /**
     * 下架
     */
    GOODS_OUT,
    /***************************  营销事件 **********************************/
    /*** 单品营销活动状态变化 **/
    SINGLE_PROMOTION_UPDATE
}