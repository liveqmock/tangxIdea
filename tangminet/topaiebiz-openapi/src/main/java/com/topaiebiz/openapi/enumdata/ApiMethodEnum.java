package com.topaiebiz.openapi.enumdata;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.openapi.exception.OpenApiExceptionEnum;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/6 19:40
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public enum ApiMethodEnum {

    /**
     * 发货
     */
    ORDER_EXPRESS("mmg.order.express.add"),

    ORDER_PAGE_QUERY("mmg.order.query"),

    UPDATE_STOCK_NUM("mmg.goods.stock.update"),

    UPDATE_STOCK_OLD("mmg.good.stock.update"),

    GOODS_QUERY("mmg.goods.query"),

    GOODS_ADD("mmg.goods.add");


    private String method;


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    ApiMethodEnum(String method) {
        this.method = method;
    }


    public static boolean contains(String method) {
        for (ApiMethodEnum apiMethodEnum : ApiMethodEnum.values()) {
            if (apiMethodEnum.method.equals(method)) {
                return true;
            }
        }
        return false;
    }


    public static ApiMethodEnum getByMethod(String method) {
        for (ApiMethodEnum apiMethodEnum : ApiMethodEnum.values()) {
            if (apiMethodEnum.method.equals(method)) {
                return apiMethodEnum;
            }
        }
        throw new GlobalException(OpenApiExceptionEnum.METHOD_NAME_IS_ILLEGAL);
    }


}
