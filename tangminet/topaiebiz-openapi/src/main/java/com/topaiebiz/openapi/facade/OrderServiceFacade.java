package com.topaiebiz.openapi.facade;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.trade.api.order.OrderServiceApi;
import com.topaiebiz.trade.dto.order.openapi.APIOrderDetailDTO;
import com.topaiebiz.trade.dto.order.params.OrderQueryParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/25 16:05
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class OrderServiceFacade {

    @Autowired
    private OrderServiceApi orderServiceApi;


    public PageInfo<APIOrderDetailDTO> queryOrderPageOpenApi(OrderQueryParams orderQueryParams) {
        log.info(">>>>>>>>>>openapi- query order page start!");
        PageInfo<APIOrderDetailDTO> pageInfo = orderServiceApi.queryOrderPageOpenApi(orderQueryParams);
        log.info(">>>>>>>>>>queryOrderPageOpenApi's result count:{}", pageInfo.getRecords().size());
        return pageInfo;
    }
}
