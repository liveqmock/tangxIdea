package com.topaiebiz.giftcard.vo;

import com.nebulapaas.base.po.PagePO;

/**
 * @description: C端礼卡展示请求参数
 * @author: Jeff Chen
 * @date: created in 下午8:40 2018/1/24
 */
public class GiftcardShowReq extends PagePO {

    private Long batchId;

    private Long labelId;

    private Integer cardAttr;

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public Integer getCardAttr() {
        return cardAttr;
    }

    public void setCardAttr(Integer cardAttr) {
        this.cardAttr = cardAttr;
    }
}
