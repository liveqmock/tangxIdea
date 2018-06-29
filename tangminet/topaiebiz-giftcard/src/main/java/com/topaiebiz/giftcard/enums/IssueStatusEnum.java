package com.topaiebiz.giftcard.enums;

/**
 * @description: 卡的发行状态
 * @author: Jeff Chen
 * @date: created in 下午4:13 2018/1/10
 */
public enum IssueStatusEnum {
    //状态：-1 草稿，0-待审核，1-审核通过（未上架/未生产），2-未通过，3-已上架/未入库，4-已入库
    DRAFT(-1, "草稿"),
    AUDIT_WAIT(0,"待审核"),
    AUDIT_PASS(1,"审核通过(未上架/未生产)"),
    AUDIT_FAIL(2, "审核未通过"),
    CARD_READY(3, "已上架/待入库"),
    CARD_IMPORT(4, "已入库"),
    ;
    private Integer statusId;
    private String desc;

    IssueStatusEnum(Integer statusId, String desc) {
        this.statusId = statusId;
        this.desc = desc;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public static IssueStatusEnum getById(Integer statusId) {
        for (IssueStatusEnum issueStatusEnum : IssueStatusEnum.values()) {
            if (issueStatusEnum.getStatusId().equals(statusId)) {
                return issueStatusEnum;
            }
        }
        return DRAFT;

    }
}
