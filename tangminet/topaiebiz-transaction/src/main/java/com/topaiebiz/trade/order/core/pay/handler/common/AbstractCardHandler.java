package com.topaiebiz.trade.order.core.pay.handler.common;

import com.topaiebiz.card.dto.MemberCardDTO;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.handler.AbstractPayContextHandler;
import com.topaiebiz.trade.order.facade.GiftCardServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

/***
 * @author yfeng
 * @date 2018-01-17 21:51
 */
@Slf4j
public abstract class AbstractCardHandler extends AbstractPayContextHandler {

    @Autowired
    private GiftCardServiceFacade giftCardServiceFacade;

    protected MemberCardDTO loadMemberCard(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        MemberCardDTO memberCardDTO = giftCardServiceFacade.getMemberValidCards(buyer.getMemberId());
        if (memberCardDTO == null) {
            log.warn("get member {} cardInfo fail", buyer.getMemberId());
            return null;
        }

        //若此用户卡集合为空或卡总额为0，则直接返回
        if (CollectionUtils.isEmpty(memberCardDTO.getBriefCardDTOList()) || !MathUtil.greaterThanZero(memberCardDTO.getTotalCardAmount())) {
            log.warn("member {} has no cards", buyer.getMemberId());
        }

        return memberCardDTO;
    }
}