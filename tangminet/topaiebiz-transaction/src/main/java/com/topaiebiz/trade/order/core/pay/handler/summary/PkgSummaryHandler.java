package com.topaiebiz.trade.order.core.pay.handler.summary;

import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.context.PaySummaryContext;
import com.topaiebiz.trade.order.core.pay.context.PkgPayedContext;
import com.topaiebiz.trade.order.core.pay.handler.AbstractPayContextHandler;
import com.topaiebiz.trade.order.dto.pay.PaySummaryDTO;
import com.topaiebiz.trade.order.dto.pay.PayedSummaryDTO;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-18 17:46
 */
@Component("pkgSummaryHandler")
public class PkgSummaryHandler extends AbstractPayContextHandler {

    @Override
    protected boolean skipWhilePkgPayed() {
        return false;
    }

    @Override
    public void doPrepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        //没有站内支付，则无须加载已经支付的摘要信息
        if (!PkgPayedContext.get()) {
            return;
        }

        //step 1 : 加载站内支付摘要
        OrderPayEntity orderPayEntity = paramContext.getOrderPayEntity();
        PayedSummaryDTO payedSummaryDTO = new PayedSummaryDTO();

        if (MathUtil.greaterThanZero(orderPayEntity.getBalance())){
            payedSummaryDTO.setBalanceAmount(orderPayEntity.getBalance());
        }
        if (MathUtil.greaterThanZero(orderPayEntity.getCardPrice())){
            payedSummaryDTO.setCardAmount(orderPayEntity.getCardPrice());
        }
        if (MathUtil.greaterThanZero(orderPayEntity.getScorePrice())){
            payedSummaryDTO.setScoreAmount(orderPayEntity.getScorePrice());
            payedSummaryDTO.setUsedScore(orderPayEntity.getScoreNum());
        }
        payedSummaryDTO.updatePrice();

        //step 2 : 更新线程上下文中的支付摘要
        PaySummaryDTO paySummary = PaySummaryContext.get();
        paySummary.setPayedSummary(payedSummaryDTO);
        paySummary.updateNeedPay();
    }


}