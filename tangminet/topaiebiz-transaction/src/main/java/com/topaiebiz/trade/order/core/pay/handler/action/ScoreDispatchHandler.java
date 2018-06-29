package com.topaiebiz.trade.order.core.pay.handler.action;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.context.PaySummaryContext;
import com.topaiebiz.trade.order.core.pay.handler.AbstractPayContextHandler;
import com.topaiebiz.trade.order.core.pay.util.ScoreDispatcher;
import com.topaiebiz.trade.order.core.pay.util.StorePayUtil;
import com.topaiebiz.trade.order.dto.pay.MemberAssetDTO;
import com.topaiebiz.trade.order.dto.pay.PaySummaryDTO;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.topaiebiz.trade.order.exception.PaymentExceptionEnum.SCORE_EXCEED_LIMIT;
import static com.topaiebiz.trade.order.exception.PaymentExceptionEnum.SCORE_GOODS_FORBID;
import static com.topaiebiz.trade.order.exception.PaymentExceptionEnum.SCORE_LACK_ERROR;

/***
 * @author yfeng
 * @date 2018-01-18 20:48
 */
@Component("scoreDispatchHandler")
public class ScoreDispatchHandler extends AbstractPayContextHandler {

    @Autowired
    private ScoreDispatcher scoreDispatcher;

    @Override
    public void doPrepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        //当需要校验输入的时候,客户端的支付请求并未使用礼卡进行支付
        if (!MathUtil.greaterThanZero(payRequest.getScore())) {
            return;
        }

        PaySummaryDTO paySummary = PaySummaryContext.get();
        MemberAssetDTO asset = paySummary.getMemberAsset();
        //用户积分不足
        if (MathUtil.greator(payRequest.getScore(), asset.getScore())) {
            throw new GlobalException(SCORE_LACK_ERROR);
        }

        //用户资产
        BigDecimal assetScoreAmount = MathUtil.getScoreAmount(asset.getScoreNum());
        //商品限制
        BigDecimal goodsScoreLimit = StorePayUtil.getScoreSupportAmount(paramContext.getStorePayDetails());
        if (MathUtil.sameValue(goodsScoreLimit, BigDecimal.ZERO)) {
            throw new GlobalException(SCORE_GOODS_FORBID);
        }

        BigDecimal curScoreLimit = MathUtil.min(goodsScoreLimit, assetScoreAmount);
        //用户选择的积分金额超过限制
        if (MathUtil.greator(payRequest.getScore(), curScoreLimit)) {
            throw new GlobalException(SCORE_EXCEED_LIMIT);
        }

        //step 3 : 积分分摊
        BigDecimal dispatchLimit = payRequest.getScore();
        scoreDispatcher.dispatch(dispatchLimit, paramContext.getStorePayDetails());
    }
}