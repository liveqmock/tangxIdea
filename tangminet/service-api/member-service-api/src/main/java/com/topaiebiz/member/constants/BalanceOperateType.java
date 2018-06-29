package com.topaiebiz.member.constants;

/**
 * Created by ward on 2018-01-15.
 */
public enum BalanceOperateType {

    BUY_CONSUME("pt_rmb", "用余额购买商品"),
    CANCEL_ORDER_REFUND("pt_rtn", "余额退款"),
    REDRESS("redress", "余额补录");


    public final String operateType;
    public final String operateDesc;


    BalanceOperateType(String operateType, String operateDesc) {
        this.operateType = operateType;
        this.operateDesc = operateDesc;
    }

    public static BalanceOperateType get(String code) {
        for (BalanceOperateType temp : values()) {
            if (temp.operateType.equals(code)) {
                return temp;
            }
        }
        return null;
    }
}
