package com.topaiebiz.member.constants;

/**
 * Created by ward on 2018-06-05.
 */
public enum LoginType {


    WECHAT("1"),
    IOS("2"),
    ANDROID("3");


    public final String operateType;


    LoginType(String operateType) {
        this.operateType = operateType;
    }

    public static LoginType get(String code) {
        for (LoginType temp : values()) {
            if (temp.operateType.equals(code)) {
                return temp;
            }
        }
        return null;
    }
}
