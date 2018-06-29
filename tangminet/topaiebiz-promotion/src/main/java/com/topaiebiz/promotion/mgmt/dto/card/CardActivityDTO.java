package com.topaiebiz.promotion.mgmt.dto.card;

import lombok.Data;

import java.util.Date;

@Data
public class CardActivityDTO {
    /**
     * ID
     */
    private Long id;
    /**
     * 活动的Id
     */
    private Long promotionId;
    /**
     * 秒杀开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 每个Id限制购买次数
     */
    private Integer buyLimit;
    /**
     * 活动备注
     */
    private String memo;
}
