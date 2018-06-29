package com.topaiebiz.promotion.mgmt.dto.box.json;

import lombok.Data;

import java.io.Serializable;

/**
 * 奖品概率配置
 */
@Data
public class RateJsonDTO implements Serializable {
    private static final long serialVersionUID = 5772451878018802843L;
    /**
     * 奖品类型（1-优惠券，2-美礼卡，3-实物奖）
     */
    private Integer awardType;
    /**
     * 奖品爆率
     */
    private Double rate;
}
