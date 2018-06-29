package com.topaiebiz.openapi.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.openapi.api.OrderPayMessageTaskApi;
import com.topaiebiz.openapi.dto.RequestParamDTO;
import com.topaiebiz.openapi.exception.OpenApiExceptionEnum;
import com.topaiebiz.openapi.service.OpenApiService;
import com.topaiebiz.openapi.utils.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

/**
 * Created by ward on 2018-03-01.
 */

@RestController
@RequestMapping(value = "/openapi/order", method = RequestMethod.POST)
public class OrderController {

    @Autowired
    private SignUtil signUtil;

    @Autowired
    private OpenApiService openApiService;

    @Autowired
    private OrderPayMessageTaskApi orderPayMessageTaskApi;

    @RequestMapping("/detail")
    public ResponseInfo detail(@RequestBody RequestParamDTO requestParamDTO) {
        orderPayMessageTaskApi.pushOrderPayMessage();
        return new ResponseInfo();
    }

    @RequestMapping("/shipOrder")
    public ResponseInfo shipOrder(@RequestBody RequestParamDTO requestParamDTO) {
        signUtil.valid(requestParamDTO);
        return openApiService.execute(requestParamDTO);
    }

    @RequestMapping("/update/stocknum")
    public ResponseInfo updateStockNum(@RequestBody RequestParamDTO requestParamDTO) {
        signUtil.valid(requestParamDTO);
        return openApiService.execute(requestParamDTO);
    }

    /**
     * Description: catch @RequestBody bad json exception
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/26
     *
     * @param:
     **/
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseInfo handleBadJSONException(HttpMessageNotReadableException ex) {
        throw new GlobalException(OpenApiExceptionEnum.PARAMETER_FORMAT_NOT_CORRECT);
    }

}
