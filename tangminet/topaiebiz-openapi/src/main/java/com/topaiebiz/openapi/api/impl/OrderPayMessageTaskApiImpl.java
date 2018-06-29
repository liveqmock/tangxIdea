package com.topaiebiz.openapi.api.impl;

import com.topaiebiz.openapi.api.OrderPayMessageTaskApi;
import com.topaiebiz.openapi.service.OpenApiOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/5 14:07
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Service
public class OrderPayMessageTaskApiImpl implements OrderPayMessageTaskApi {

    @Autowired
    private OpenApiOrderService openApiOrderService;

    @Override
    public void pushOrderPayMessage() {
        openApiOrderService.pushOrderDetail();
    }

}
