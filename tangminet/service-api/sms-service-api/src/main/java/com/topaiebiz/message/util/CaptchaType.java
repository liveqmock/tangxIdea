package com.topaiebiz.message.util;

public enum CaptchaType {

    REGISTER("1"), LOGIN("2"), FIND_PWD("3"), BIND_PHONE("4"),
    UPDATE_PWD("5"), UPDATE_PAY_PWD("6"), UPDATE_WITHDRAW_PWD("7"), CHANGE_MOBILE("8"), LOGIN_REGISTER("0"),
    BIND_PHONE_NOSESSION("9");

    private String type;

    public String getType() {
        return type;
    }

    CaptchaType(String type) {
        this.type = type;
    }

    public static CaptchaType get(String type) {
        for (CaptchaType temp : values()) {
            if (temp.type.equals(type)) {
                return temp;
            }
        }
        return null;
    }

}
