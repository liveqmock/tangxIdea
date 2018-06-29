package com.topaiebiz.openapi.facade;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.trade.api.order.OrderServiceApi;
import com.topaiebiz.trade.dto.order.openapi.OrderExpressDTO;
import com.topaiebiz.transport.api.ExpressageApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/6 20:28
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class OrderExpressServiceFacade {

    @Autowired
    private OrderServiceApi orderServiceApi;

    @Autowired
    private ExpressageApi expressageApi;

    public boolean shipOrder(OrderExpressDTO orderExpressDTO) {
        boolean result = orderServiceApi.orderShip(orderExpressDTO);
        log.warn(">>>>>>>>>>orderServiceApi.orderShip, params:{}, result:{}", JSON.toJSONString(orderExpressDTO), result);
        return result;
    }

}
