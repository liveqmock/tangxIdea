package com.topaiebiz.guider.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by ward on 2018-06-04.
 */
@Data
public class OrderCompleteAchievementBo {
    private BigDecimal awardRate;

    private BigDecimal awardMoney;

    /**
     * 奖励基数
     */
    private BigDecimal awardBaseMoney;


    private Long memberId;

    private Long taskId;


    private Long srcMemberId;

    private Long orderId;

    private BigDecimal refundMoney;

    private BigDecimal payMoney;

    private BigDecimal freightMoney;
}
