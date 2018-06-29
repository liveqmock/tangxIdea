package com.topaiebiz.openapi.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.openapi.api.OrderPayMessageTaskApi;
import com.topaiebiz.openapi.dto.RequestParamDTO;
import com.topaiebiz.openapi.service.OpenApiService;
import com.topaiebiz.openapi.service.OrderService;
import com.topaiebiz.openapi.utils.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

/**
 * Created by ward on 2018-03-27.
 */
@RestController
@RequestMapping(value = "/openapi/index", method = RequestMethod.POST)
public class IndexController {

    @Autowired
    private OrderService orderService;

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

    @RequestMapping("/pushPayment/{orderId}")
    public ResponseInfo pushPayment(@PathVariable Long orderId) {
        return new ResponseInfo(orderService.pushPaymentToCustom(orderId));
    }

    @RequestMapping("/dispach")
    public ResponseInfo dispach(@RequestBody RequestParamDTO requestParamDTO) {
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
        return new ResponseInfo("501", "参数不正确！");
    }

}
