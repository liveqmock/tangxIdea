package com.topaiebiz.giftcard.enums;

/**
 * @description: 操作日志类型
 * @author: Jeff Chen
 * @date: created in 上午9:57 2018/3/16
 */
public enum CardOpTypeEnum {
    AUDIT_PASS("审核通过"),
    AUDIT_REJECT("驳回"),
    FREEZE("冻结"),
    UNFREEZE("解冻"),
    ACTIVE("激活"),
    RENEW("续期"),
    EDIT("编辑"),
    UPD_PRIORITY("更新优先级"),
    PRODUCE_CARD("生产"),
    EXPORT_CARD("导出卡密"),
    ;

    private String opType;

    CardOpTypeEnum(String opType) {
        this.opType = opType;
    }

    public String getOpType() {
        return opType;
    }
}
