package com.topaiebiz.giftcard.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 礼卡相关后台操作日志表
 * </p>
 *
 * @author Jeff Chen
 * @since 2018-03-16
 */
@TableName("t_giftcard_op_log")
public class GiftcardOpLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 业务主键：batchId,unitId
     */
    @TableField("bizId")
    private Long bizId;
    /**
     * 备注
     */
    private String note;
    /**
     * 操作来源：1-发行 2-卡管理
     */
    @TableField("opSrc")
    private Integer opSrc;
    /**
     * 操作类型：通过审核，驳回，冻结，解冻等
     */
    @TableField("opType")
    private String opType;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 操作时间
     */
    @TableField("opTime")
    private Date opTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

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

    @Override
    public String toString() {
        return "GiftcardOpLog{" +
        "id=" + id +
        ", bizId=" + bizId +
        ", note=" + note +
        ", opSrc=" + opSrc +
        ", opType=" + opType +
        ", operator=" + operator +
        ", opTime=" + opTime +
        "}";
    }
}
