package com.topaiebiz.guider.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by ward on 2018-06-04.
 */
@Data
public class PayCompleteAchievementBo {

    private BigDecimal awardRate;
    
    @Deprecated
    private BigDecimal awardMoney;

    /**
     * 奖励基数
     */
    @Deprecated
    private BigDecimal awardBaseMoney;


    private Long memberId;

    private Long payId;

    private Long taskId;

    private Long srcMemberId;

    private BigDecimal refundMoney;

    private BigDecimal payMoney;

    private BigDecimal freightMoney;
}
