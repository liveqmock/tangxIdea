package com.topaiebiz.trade.order.core.pay.handler.action;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.context.PaySummaryContext;
import com.topaiebiz.trade.order.core.pay.handler.AbstractPayContextHandler;
import com.topaiebiz.trade.order.core.pay.util.BalanceDispatcher;
import com.topaiebiz.trade.order.dto.pay.MemberAssetDTO;
import com.topaiebiz.trade.order.dto.pay.PaySummaryDTO;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.topaiebiz.trade.order.exception.PaymentExceptionEnum.BALANCE_LACK_ERROR;

/***
 * @author yfeng
 * @date 2018-01-18 20:48
 */
@Component("balanceDispatchHandler")
public class BalanceDispatchHandler extends AbstractPayContextHandler {

    @Autowired
    private BalanceDispatcher balanceDispatcher;

    @Override
    public void doPrepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        if (!MathUtil.greaterThanZero(payRequest.getBalance())) {
            return;
        }
        PaySummaryDTO paySummary = PaySummaryContext.get();
        MemberAssetDTO asset = paySummary.getMemberAsset();
        //用户余额不足
        if (!MathUtil.greateEq(asset.getBalance(), payRequest.getBalance())) {
            throw new GlobalException(BALANCE_LACK_ERROR);
        }

        //step 3 : 余额分摊
        BigDecimal dispatchLimit = payRequest.getBalance();
        balanceDispatcher.dispatch(dispatchLimit, paramContext.getStorePayDetails());
    }
}
