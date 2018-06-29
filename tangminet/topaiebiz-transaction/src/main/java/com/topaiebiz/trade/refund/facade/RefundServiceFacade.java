package com.topaiebiz.trade.refund.facade;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.pay.api.RefundApi;
import com.topaiebiz.pay.dto.refund.RefundParamDTO;
import com.topaiebiz.pay.dto.refund.RefundResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description 退款接口
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/20 19:25
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class RefundServiceFacade {

    @Autowired
    private RefundApi refundApi;

    public String refund(RefundParamDTO refundParamDTO) {
        log.info("----------refund params:{}", JSON.toJSONString(refundParamDTO));
        RefundResultDTO refundResultDTO = refundApi.refund(refundParamDTO);
        log.info("----------refundOrderId:{}, after refund processing the results:{}", refundParamDTO.getRefundOrderId(), JSON.toJSONString(refundResultDTO));
        if (refundResultDTO != null && refundResultDTO.getResultCode().equals(Constants.Order.REFUND_SUCCESS)) {
            return refundResultDTO.getCallBackNo();
        }
        log.error(">>>>>>>>>>refund was failed!");
        return null;
    }
}
