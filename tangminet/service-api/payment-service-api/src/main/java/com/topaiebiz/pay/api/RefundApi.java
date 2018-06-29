package com.topaiebiz.pay.api;

import com.topaiebiz.pay.dto.refund.RefundParamDTO;
import com.topaiebiz.pay.dto.refund.RefundResultDTO;

/**
 * Description 退款接口
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/17 17:50
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface RefundApi {

    /**
     * Description: 退款
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/19
     *
     * @param:
     **/
    RefundResultDTO refund(RefundParamDTO refundDTO);

}
