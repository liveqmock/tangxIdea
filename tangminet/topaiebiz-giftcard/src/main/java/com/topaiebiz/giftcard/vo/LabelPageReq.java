package com.topaiebiz.giftcard.vo;

import com.nebulapaas.base.po.PagePO;

/**
 * @description: 标签请求参数
 * @author: Jeff Chen
 * @date: created in 下午8:38 2018/1/15
 */
public class LabelPageReq extends PagePO {

    private String labelName;

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
