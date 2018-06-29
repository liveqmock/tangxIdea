package com.topaiebiz.trade.api;

/***
 * @author yfeng
 * @date 2018-01-09 13:27
 */
public interface OrderTaskServiceApi {

    /**
     *
     * Description: 定时取消超时未支付的订单
     *
     * Author: hxpeng
     * createTime: 2018/2/1
     *
     * @param:
     **/
    void cancelUnPayOrder();

    /**
     *
     * Description: 收货超时 自动收货
     *
     * Author: hxpeng
     * createTime: 2018/2/1
     *
     * @param:
     **/
    void receivingOrder();

    /**
    *
    * Description: 收货七天自动完成订单
    *
    * Author: hxpeng
    * createTime: 2018/2/1
    *
    * @param:
    **/
    void completeOrders();


    /**
    *
    * Description: 默认好评
    *
    * Author: hxpeng
    * createTime: 2018/2/5
    *
    * @param:
    **/
    void automaticPraise();


}
