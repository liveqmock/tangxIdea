package com.topaiebiz.trade.order.facade;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.card.api.GiftCardApi;
import com.topaiebiz.card.dto.MemberCardDTO;
import com.topaiebiz.card.dto.PayInfoDTO;
import com.topaiebiz.card.dto.RefundOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-18 20:08
 */
@Component
@Slf4j
public class GiftCardServiceFacade {

    @Autowired
    private GiftCardApi giftCardApi;

    /**
     * 获取指定用户的有效礼卡信息
     *
     * @param memberId
     * @return
     */
    public MemberCardDTO getMemberValidCards(Long memberId) {
        MemberCardDTO cardDTO = null;
        try {
            log.info("giftCardApi.getMemberValidCards({}) request send ....", memberId);
            cardDTO = giftCardApi.getMemberValidCards(memberId);
            log.info("return : {}", JSON.toJSONString(cardDTO));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return cardDTO;
    }

    /**
     * 用户用礼卡支付
     *
     * @param payInfoDTO
     * @return
     */
    public Boolean payByCards(PayInfoDTO payInfoDTO) {
        Boolean result = false;
        try {
            log.info("giftCardApi.payByCards({}) request send ....", JSON.toJSONString(payInfoDTO));
            result = giftCardApi.payByCards(payInfoDTO);
            log.info("return : {}", JSON.toJSONString(result));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return result;
    }

    /**
     * 礼卡退款
     *
     * @param refundOrderDTO
     * @return
     */
    public Boolean refundCards(RefundOrderDTO refundOrderDTO) {
        Boolean result = false;
        try {
            log.info("giftCardApi.refundCards({}) request send ....", JSON.toJSONString(refundOrderDTO));
            result = giftCardApi.refundCards(refundOrderDTO);
            log.info("return : {}", JSON.toJSONString(result));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return result;
    }
}