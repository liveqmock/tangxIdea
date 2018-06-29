package com.topaiebiz.openapi.core.operation;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.openapi.core.AbstractOperation;
import com.topaiebiz.openapi.dto.RequestParamDTO;
import com.topaiebiz.openapi.dto.ResponseResultDTO;
import com.topaiebiz.openapi.facade.OrderExpressServiceFacade;
import com.topaiebiz.trade.dto.order.openapi.OrderExpressDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description api-发货
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/27 11:07
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class OrderDeliveryOperation extends AbstractOperation {

    @Autowired
    private OrderExpressServiceFacade orderExpressServiceFacade;

    @Override
    public ResponseInfo operation(RequestParamDTO paramDTO) {
        // 1：发货物流信息
        OrderExpressDTO orderExpressDTO = super.convertStrToBean(paramDTO.getParams(), OrderExpressDTO.class);
        orderExpressDTO.setStoreId(paramDTO.getStoreId());
        log.info(">>>>>>>>>>order deliver operation params:{}", JSON.toJSONString(orderExpressDTO));
        orderExpressServiceFacade.shipOrder(orderExpressDTO);
        return ResponseResultDTO.success();
    }
}
