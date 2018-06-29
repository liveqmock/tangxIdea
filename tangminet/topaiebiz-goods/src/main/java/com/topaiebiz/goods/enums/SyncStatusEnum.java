package com.topaiebiz.goods.enums;

/**
 * @description: 同步状态
 * @author: Jeff Chen
 * @date: created in 下午2:34 2018/5/19
 */
public enum SyncStatusEnum {
    //0-未同步  1-已同步 2-编辑后未同步再删除且未同步
    SYNC_NO(0),
    SYNC_YES(1),
    EDIT_THEN_DELETE(2),
    ;
    private Integer value;

    SyncStatusEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
