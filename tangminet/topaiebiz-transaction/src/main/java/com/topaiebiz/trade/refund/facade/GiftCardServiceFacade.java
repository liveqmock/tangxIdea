package com.topaiebiz.trade.refund.facade;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.card.api.GiftCardApi;
import com.topaiebiz.card.dto.RefundOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description 美礼卡接口
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/30 13:39
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component(value = "refundGiftCardServiceFacade")
public class GiftCardServiceFacade {

    @Autowired
    private GiftCardApi giftCardApi;

    public Boolean refund(RefundOrderDTO refundOrderDTO){
        Boolean result = giftCardApi.refundCards(refundOrderDTO);
        log.info("----------giftCardApi.refundCards-- request params:{}, response:{}", JSON.toJSONString(refundOrderDTO), result);
        return result;
    }

}
