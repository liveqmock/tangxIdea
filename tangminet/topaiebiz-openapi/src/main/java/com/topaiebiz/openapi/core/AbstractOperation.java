package com.topaiebiz.openapi.core;

import com.alibaba.fastjson.JSONObject;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.openapi.dto.RequestParamDTO;
import com.topaiebiz.openapi.exception.OpenApiExceptionEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/27 11:05
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public abstract class AbstractOperation {

    /**
     * Description: xx
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/27
     *
     * @param:
     **/
    public abstract ResponseInfo operation(RequestParamDTO paramDTO);

    /**
     * Description: 请求参数字符串转换成业务bean
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/27
     *
     * @param:
     **/
    protected <T> T convertStrToBean(String str, Class<T> clazz) {
        try {
            if (StringUtils.isBlank(str)){
                throw new GlobalException(OpenApiExceptionEnum.PARAMETER_FORMAT_NOT_CORRECT);
            }
            return JSONObject.parseObject(str, clazz);
        } catch (Exception e) {
            throw new GlobalException(OpenApiExceptionEnum.PARAMETER_FORMAT_NOT_CORRECT);
        }
    }

}
