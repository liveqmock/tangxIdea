package com.topaiebiz.guider.service;

import com.topaiebiz.member.constants.LoginType;

import java.util.List;

/**
 * Created by ward on 2018-05-29.
 */
public interface AchievementService {


    void processOrderClose(Long memberId, Long orderId);

    void processOrderComplete(Long memberId, Long orderId);

    void processPaySuccess(Long memberId, Long payId, List<Long> orderIds, Boolean isPromotionOrder);

    void processMemberRegister(Long memberId, String srcCode);

    void processMemberLogin(Long memberId, LoginType loginType);
}
