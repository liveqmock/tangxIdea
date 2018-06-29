package com.topaiebiz.openapi.contants;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/6 16:44
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface OpenApiContants {

    class Method {
        public static final String ORDER_CREATE_METHOD = "mmg.order.create";
    }


    class SignParams {

        /**
         * 已锁定
         */
        public static final Integer APPID = 1;
    }


    class OrderMessage {

        /**
         * 最多推送次数
         */
        public static final Integer MAX_PUSH_COUNT = 2;

        /**
         * 推送状态（0：未成功， 1：成功）
         */
        public static final Integer STATE_YES = 1;
        public static final Integer STATE_NO = 0;


        public static final String RESPONSE_SUCCESS_CODE = "200";
        public static final String RESPONSE_MSG = "msg";
    }

    class OrderCreateVersion {
        public static final String VERSION_ONE = "1.0";
        public static final String VERSION_TWO = "2.0";
    }


}
