package com.topaiebiz.openapi.service.impl;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.openapi.core.AbstractOperation;
import com.topaiebiz.openapi.core.operation.*;
import com.topaiebiz.openapi.dto.RequestParamDTO;
import com.topaiebiz.openapi.enumdata.ApiMethodEnum;
import com.topaiebiz.openapi.exception.OpenApiExceptionEnum;
import com.topaiebiz.openapi.service.OpenApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/6 19:59
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Service
public class OpenApiServiceImpl implements OpenApiService {

    @Autowired
    private ApplicationContext springContext;

    @Override
    public ResponseInfo execute(RequestParamDTO requestParamDTO) {
        ApiMethodEnum apiMethodEnum = ApiMethodEnum.getByMethod(requestParamDTO.getMethod());

        AbstractOperation abstractOperation;
        switch (apiMethodEnum) {
            case ORDER_EXPRESS:
                abstractOperation = springContext.getBean(OrderDeliveryOperation.class);
                break;

            case UPDATE_STOCK_NUM:
            case UPDATE_STOCK_OLD:
                abstractOperation = springContext.getBean(UpdateGoodStockOperation.class);
                break;

            case ORDER_PAGE_QUERY:
                abstractOperation = springContext.getBean(QueryOrderPageOperation.class);
                break;
            case GOODS_QUERY:
                abstractOperation = springContext.getBean(GoodsQueryOperation.class);
                break;
            case GOODS_ADD:
                abstractOperation = springContext.getBean(GoodsAddOperation.class);
                break;
            default:
                throw new GlobalException(OpenApiExceptionEnum.METHOD_NAME_IS_ILLEGAL);
        }
        return abstractOperation.operation(requestParamDTO);
    }

}

