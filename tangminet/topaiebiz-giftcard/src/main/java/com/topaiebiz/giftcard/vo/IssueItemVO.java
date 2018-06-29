package com.topaiebiz.giftcard.vo;

import java.math.BigDecimal;

/**
 * @description: 最简礼卡信息
 * @author: Jeff Chen
 * @date: created in 下午2:06 2018/1/25
 */
public class IssueItemVO {

    private Long batchId;

    private BigDecimal faceValue;

    private BigDecimal salePrice;

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }
}
