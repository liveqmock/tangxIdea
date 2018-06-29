package com.topaiebiz.trade.order.core.pay.handler.summary;

import com.topaiebiz.card.constant.ApplyScopeEnum;
import com.topaiebiz.card.dto.BriefCardDTO;
import com.topaiebiz.card.dto.MemberCardDTO;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.context.PaySummaryContext;
import com.topaiebiz.trade.order.core.pay.handler.common.AbstractCardHandler;
import com.topaiebiz.trade.order.core.pay.util.CardDispatcher;
import com.topaiebiz.trade.order.dto.pay.MemberAssetDTO;
import com.topaiebiz.trade.order.dto.pay.PaySummaryDTO;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-18 20:35
 */
@Slf4j
@Component("cardLoadHandler")
public class CardLoadHandler extends AbstractCardHandler {

    @Override
    public void doPrepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        //step 1 : 加载可用礼卡
        MemberCardDTO memberCardDTO = loadMemberCard(buyer, paramContext, payRequest);

        //step 2 : 礼卡校验
        if (memberCardDTO == null || CollectionUtils.isEmpty(memberCardDTO.getBriefCardDTOList())) {
            //用户并没有可用礼卡直接跳过此
            return;
        }

        //step 3 : 计算可用金额
        PaySummaryDTO paySummary = PaySummaryContext.get();
        BigDecimal dispatchLimit = MathUtil.min(paySummary.getMaxPkgPay(), memberCardDTO.getTotalCardAmount());

        //清空分摊结果
        paramContext.cleanDispatch();

        Pair<List<BriefCardDTO>, BigDecimal> dispatchCost = CardDispatcher.dispatch(dispatchLimit, memberCardDTO.getBriefCardDTOList(), paramContext.getStorePayDetails());

        //设置当前订单可用礼卡列表和支付金额
        paySummary.initMemberAsset();
        MemberAssetDTO asset = paySummary.getMemberAsset();
        asset.setCardAmount(dispatchCost.getRight());
        asset.setCardNum(dispatchCost.getLeft().size());
        asset.setCards(dispatchCost.getLeft());
        asset.setHasLimitCards(hasLimitCards(dispatchCost.getLeft()));
        asset.setAccountCard(memberCardDTO.getTotalCardAmount());
    }

    private boolean hasLimitCards(List<BriefCardDTO> cards) {
        if (CollectionUtils.isEmpty(cards)) {
            return false;
        }
        for (BriefCardDTO cardDTO : cards) {
            Integer applyScope = cardDTO.getApplyScope().intValue();
            if (!ApplyScopeEnum.APPLY_ALL.getScopeId().equals(applyScope)) {
                return true;
            }
        }
        return false;
    }
}