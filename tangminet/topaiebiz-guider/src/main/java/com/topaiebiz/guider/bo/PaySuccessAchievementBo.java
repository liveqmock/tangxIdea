package com.topaiebiz.guider.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by ward on 2018-06-04.
 */
@Data
public class PaySuccessAchievementBo {

    private BigDecimal awardRate;



    private BigDecimal payMomey;

    private BigDecimal freightMoney;



    private Long memberId;

    private Long taskId;


    private Long srcMemberId;

    private Integer sortingId;
}
