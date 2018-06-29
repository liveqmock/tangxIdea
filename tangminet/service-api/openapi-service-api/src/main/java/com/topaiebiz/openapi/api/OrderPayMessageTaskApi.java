package com.topaiebiz.openapi.api;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/5 13:47
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface OrderPayMessageTaskApi {

    /**
    *
    * Description: 定时查询，推送订单创建信息到第三方
    *
    * Author: hxpeng
    * createTime: 2018/3/5
    *
    * @param:
    **/
    void pushOrderPayMessage();

}
