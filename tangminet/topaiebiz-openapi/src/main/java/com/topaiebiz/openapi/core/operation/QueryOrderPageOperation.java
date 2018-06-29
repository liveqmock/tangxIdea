package com.topaiebiz.openapi.core.operation;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.openapi.core.AbstractOperation;
import com.topaiebiz.openapi.dto.RequestParamDTO;
import com.topaiebiz.openapi.dto.ResponseResultDTO;
import com.topaiebiz.openapi.facade.OrderServiceFacade;
import com.topaiebiz.trade.dto.order.params.OrderQueryParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/27 13:30
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class QueryOrderPageOperation extends AbstractOperation {

    @Autowired
    private OrderServiceFacade orderServiceFacade;

    @Override
    public ResponseInfo operation(RequestParamDTO paramDTO) {
        OrderQueryParams orderQueryParams = super.convertStrToBean(paramDTO.getParams(), OrderQueryParams.class);
        log.info(">>>>>>>>>>query order'page params:{}", JSON.toJSONString(orderQueryParams));
        orderQueryParams.setStoreId(paramDTO.getStoreId());
        return ResponseResultDTO.success(orderServiceFacade.queryOrderPageOpenApi(orderQueryParams));
    }

}
