package com.topaiebiz.pay.api;

import com.topaiebiz.pay.dto.ReportCustomsDTO;

/**
 * Description 第三方支付接口
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/15 13:06
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface PaymentApi {

    void reportCustoms(ReportCustomsDTO reportCustomsDTO);

}
