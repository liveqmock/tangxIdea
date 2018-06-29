package com.topaiebiz.trade.order.core.pay.handler.action;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.card.dto.MemberCardDTO;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.handler.common.AbstractCardHandler;
import com.topaiebiz.trade.order.core.pay.util.CardDispatcher;
import com.topaiebiz.trade.order.exception.PaymentExceptionEnum;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-18 20:35
 */
@Component("cardDispatchHandler")
public class CardDispatchHandler extends AbstractCardHandler {

    @Override
    public void doPrepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        //当需要校验输入的时候,客户端的支付请求并未使用礼卡进行支付
        if (!MathUtil.greaterThanZero(payRequest.getCardAmount())) {
            return;
        }

        //step 1 : 加载可用礼卡
        MemberCardDTO memberCardDTO = loadMemberCard(buyer, paramContext, payRequest);

        //step 2 : 校验是否有礼卡可以使用
        if (memberCardDTO == null || CollectionUtils.isEmpty(memberCardDTO.getBriefCardDTOList())) {
            //用户并没有可用礼卡
            throw new GlobalException(PaymentExceptionEnum.HAS_NO_USEFUL_CARDS);
        }

        //step 3 : 金额分摊
        CardDispatcher.dispatch(payRequest.getCardAmount(), memberCardDTO.getBriefCardDTOList(), paramContext.getStorePayDetails());
    }
}