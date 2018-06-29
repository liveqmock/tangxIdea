package com.topaiebiz.giftcard.vo;

import java.util.List;

/**
 * @description: 卡操作请求
 * @author: Jeff Chen
 * @date: created in 上午10:15 2018/3/16
 */
public class CardOpReq {

    /**
     * 单条业务主键
     */
    private Long bizId;
    /**
     * 业务id列表
     */
    private List<Long> idList;
    /**
     * 操作说明
     */
    private String note;
    /**
     * 操作人
     */
    private String operator;

    /**
     * 整数参数
     */
    private Integer intParam;

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getIntParam() {
        return intParam;
    }

    public void setIntParam(Integer intParam) {
        this.intParam = intParam;
    }
}
