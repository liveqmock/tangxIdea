package com.topaiebiz.giftcard.vo;

import com.nebulapaas.base.po.PagePO;

/**
 * @description: 消费日志请求参数
 * @author: Jeff Chen
 * @date: created in 下午2:08 2018/1/24
 */
public class MyGiftcardLogReq extends PagePO {

    private Long memberId;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
