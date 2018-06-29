package com.topaiebiz.promotion.mgmt.dto.box.json;

import lombok.Data;

/**
 * 奖池配置
 */
@Data
public class AwardPoolJsonDTO {
    /**
     * 奖品类型（1-优惠券，2-美礼卡，3-实物奖）
     */
    private Integer awardType;
    /**
     * 是否可用 0-可用，1-不可用
     */
    private Integer state;
    /**
     * 配置的奖品ID（格式：id1,id2,id3……）
     */
    private String awardIds;
}