package com.topaiebiz.openapi.dto;

import com.nebulapaas.web.response.ResponseInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * Description
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/6 20:05
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class ResponseResultDTO implements Serializable {

    private static final long serialVersionUID = 528815928291193087L;

    private Boolean ifSuccess;
    private String message;
    private Object data;


    public static ResponseInfo success() {
        return new ResponseInfo();
    }

    public static ResponseInfo success(Object data) {
        return new ResponseInfo(data);
    }

    private ResponseResultDTO(Boolean ifSuccess, String message, Object data) {
        this.ifSuccess = ifSuccess;
        this.message = message;
        this.data = data;
    }
}
