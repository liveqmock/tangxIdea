package com.nebulapaas.base.enumdata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PayMethodEnum {
    ALIPAY("alipay"),
    WXPAY("wxpay"),
    WX_JSAPI("wx_jsapi"),
    PREDEPOSIT("predeposit");

    @Getter
    private String name;


    /**
    *
    * Description: 根据枚举值 查询 支付描述
    *
    * Author: hxpeng
    * createTime: 2018/3/8
    *
    * @param:
    **/
    public static String getDescByName(String name){
        PayMethodEnum payMethodEnum = getByName(name);
        if (payMethodEnum == null){
            return "未知支付";
        }
        switch (payMethodEnum){
            case ALIPAY:
                return "支付宝支付";
            case WXPAY:
                return "微信支付";
            case WX_JSAPI:
                return "微信公众号支付";
            case PREDEPOSIT:
                return "站内余额支付";
        }
        return "未知支付";
    }

    public static PayMethodEnum getByName(String name){
        for (PayMethodEnum payMethodEnum : PayMethodEnum.values()) {
            if (payMethodEnum.getName().equals(name)){
                return payMethodEnum;
            }
        }
        return null;
    }
}