package com.topaiebiz.guider.bo;

import lombok.Data;

/**
 * Created by ward on 2018-06-05.
 */
@Data
public class RegisterAchievementBo {

    public RegisterAchievementBo(Long srcMemberId, Long taskId, Long memberId) {
        this.srcMemberId = srcMemberId;
        this.taskId = taskId;
        this.memberId = memberId;
    }

    private Long srcMemberId;

    private Long taskId;

    private Long memberId;
}
