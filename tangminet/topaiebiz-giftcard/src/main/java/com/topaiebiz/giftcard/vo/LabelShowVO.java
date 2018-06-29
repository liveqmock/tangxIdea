package com.topaiebiz.giftcard.vo;

import java.io.Serializable;

/**
 * @description: C端标签列表
 * @author: Jeff Chen
 * @date: created in 下午7:01 2018/1/24
 */
public class LabelShowVO implements Serializable {

    /**
     * 标签id
     */
    private Long labelId;

    /**
     * 名称
     */
    private String labelName;

    public Long getlabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
