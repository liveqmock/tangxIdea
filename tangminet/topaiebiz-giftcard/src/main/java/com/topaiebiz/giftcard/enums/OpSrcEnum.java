package com.topaiebiz.giftcard.enums;

/**
 * @description: 操作来源
 * @author: Jeff Chen
 * @date: created in 上午10:01 2018/3/16
 */
public enum OpSrcEnum {
    OP_BATCH(1, "卡发行"),
    OP_CARD(2, "卡管理"),
    ;
    private int srcId;
    private String scrDesc;

    OpSrcEnum(int srcId, String scrDesc) {
        this.srcId = srcId;
        this.scrDesc = scrDesc;
    }

    public int getSrcId() {
        return srcId;
    }

    public String getScrDesc() {
        return scrDesc;
    }
}
