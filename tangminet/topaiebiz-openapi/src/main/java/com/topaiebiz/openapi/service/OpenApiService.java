package com.topaiebiz.openapi.service;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.openapi.dto.RequestParamDTO;
import com.topaiebiz.openapi.dto.ResponseResultDTO;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/6 19:58
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface OpenApiService {

    ResponseInfo execute(RequestParamDTO requestParamDTO);

}
