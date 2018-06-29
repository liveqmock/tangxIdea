package com.topaiebiz.guider.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by ward on 2018-06-05.
 */
@Data
public class NewUserAchievementBo {

    public NewUserAchievementBo() {

    }

    public NewUserAchievementBo(Long srcMemberId, Long taskId, Long memberId, BigDecimal awardMoney) {
        this.srcMemberId = srcMemberId;
        this.taskId = taskId;
        this.memberId = memberId;
        this.awardMoney = awardMoney;
    }

    private Long srcMemberId;

    private Long taskId;

    private Long memberId;

    private BigDecimal awardMoney;
}
