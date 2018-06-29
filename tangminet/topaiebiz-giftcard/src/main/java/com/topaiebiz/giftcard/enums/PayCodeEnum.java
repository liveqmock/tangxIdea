package com.topaiebiz.giftcard.enums;

/**
 * @description: 支付代码
 * @author: Jeff Chen
 * @date: created in 下午3:51 2018/1/26
 */
public enum PayCodeEnum {

    WX_PAY("wxpay", "微信支付"),
    ALI_PAY("alipay", "支付宝"),
    NOTHING("no", "无"),
    ;
    private String code;
    private String cnName;

    PayCodeEnum(String code, String cnName) {
        this.code = code;
        this.cnName = cnName;
    }

    public String getCode() {
        return code;
    }

    public String getCnName() {
        return cnName;
    }

    public static PayCodeEnum getByCode(String code) {
        for (PayCodeEnum payCodeEnum : PayCodeEnum.values()) {
            if (payCodeEnum.getCode().equalsIgnoreCase(code)) {
                return payCodeEnum;
            }
        }
        return NOTHING;
    }
}
