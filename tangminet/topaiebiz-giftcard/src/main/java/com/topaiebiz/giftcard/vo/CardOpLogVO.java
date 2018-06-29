package com.topaiebiz.giftcard.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 操作日志展示对象
 * @author: Jeff Chen
 * @date: created in 上午9:24 2018/3/20
 */
public class CardOpLogVO implements Serializable {
    /**
     * 备注
     */
    private String note;
    /**
     * 操作来源：1-发行 2-卡管理
     */
    private Integer opSrc;
    /**
     * 操作类型：通过审核，驳回，冻结，解冻等
     */
    private String opType;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 操作时间
     */
    private Date opTime;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getOpSrc() {
        return opSrc;
    }

    public void setOpSrc(Integer opSrc) {
        this.opSrc = opSrc;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOpTime() {
        return opTime;
    }

    public void setOpTime(Date opTime) {
        this.opTime = opTime;
    }
}
