package com.topaiebiz.member.member.constants;

public enum DependOperateType {

    SET_PWD("0"), FIND_PWD("3"), BIND_PHONE("4"),
    UPDATE_PWD("5"), UPDATE_PAY_PWD("6"), CHANGE_MOBILE("8");

    public final String type;

    DependOperateType(String type) {
        this.type = type;
    }

    public static DependOperateType get(String type) {
        for (DependOperateType temp : values()) {
            if (temp.type.equals(type)) {
                return temp;
            }
        }
        return null;
    }

}
