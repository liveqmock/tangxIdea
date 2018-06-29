package com.topaiebiz.giftcard.vo;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: 下单参数
 * @author: Jeff Chen
 * @date: created in 下午4:58 2018/1/25
 */
public class PlaceOrderVO implements Serializable {

    @NotEmpty(message = "orderKey不能为空")
    private String orderKey;

    @NotNull(message = "请指定具体购买的礼卡")
    private Long batchId;
    @Range(min = 1,message = "至少购买一张")
    private Integer issueNum;

    private String memberName;

    private Long memberId;

    private String memberPhone;

    /**
     * 参数请求类型  1 填单  2 生单
     */
    private Integer reqType = 1;

    public String getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(String orderKey) {
        this.orderKey = orderKey;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Integer getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(Integer issueNum) {
        this.issueNum = issueNum;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberPhone() {
        return memberPhone;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }

    public Integer getReqType() {
        return reqType;
    }

    public void setReqType(Integer reqType) {
        this.reqType = reqType;
    }
}
