package com.topaiebiz.member.constants;

/**
 * Created by ward on 2018-01-15.
 */
public enum AssetOperateType {

    BUY_CONSUME("pt_rmb", "用余额购买商品", "积分抵现（购买商品抵现）"),
    REFUND("pt_rtn", "余额退款", "积分回退（订单取消或退货抵现积分退回）"),
    //CANCEL_ORDER_REFUND("pt_rtn", "余额退款", "积分回退（订单取消或退货抵现积分退回）");
    REDRESS("redress", "余额补录", "积分补录");

    public final String operateType;
    public final String operatePointDesc;
    public final String operateBalanceDesc;


    AssetOperateType(String operateType, String operatePointDesc, String operateBalanceDesc) {
        this.operateType = operateType;
        this.operatePointDesc = operatePointDesc;
        this.operateBalanceDesc = operateBalanceDesc;
    }


    public static AssetOperateType get(String code) {
        for (AssetOperateType temp : values()) {
            if (temp.operateType.equals(code)) {
                return temp;
            }
        }
        return null;
    }
}
