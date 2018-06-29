package com.topaiebiz.trade.order.core.pay.handler.action;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.context.PaySummaryContext;
import com.topaiebiz.trade.order.core.pay.handler.AbstractPayContextHandler;
import com.topaiebiz.trade.order.dto.pay.PaySummaryDTO;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.topaiebiz.trade.order.exception.PaymentExceptionEnum.INPUT_AMOUNT_IS_NEGATIVE;
import static com.topaiebiz.trade.order.exception.PaymentExceptionEnum.PAY_AMOUNT_ERROR;
import static com.topaiebiz.trade.order.exception.PaymentExceptionEnum.PAY_AMOUNT_PRECISION_ERROR;

/***
 * @author yfeng
 * @date 2018-01-18 20:52
 */
@Slf4j
@Component("amountValidateHandler")
public class AmountValidateHandler extends AbstractPayContextHandler {

    @Override
    public void doPrepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        PaySummaryDTO paySummary = PaySummaryContext.get();

        List<BigDecimal> inputAmoutDatas = getInputAmountItems(payRequest);

        //校验金额精度
        checkPrecision(inputAmoutDatas);

        //检验金额非负数
        checkAmountItem(inputAmoutDatas);

        //输入支付的总金额
        BigDecimal inputSum = getSum(inputAmoutDatas);

        //step 3 : 站内不能大于允许金额
        BigDecimal pkgInput = payRequest.getCardAmount().add(payRequest.getBalance()).add(payRequest.getScore());
        if (MathUtil.greator(pkgInput, paySummary.getMaxPkgPay())) {
            log.warn("pkgInput {} but pkgAllow is {}", pkgInput, paySummary.getMaxPkgPay());
            throw new GlobalException(PAY_AMOUNT_ERROR);
        }

        if (!MathUtil.sameValue(inputSum, paySummary.getNeedPay())) {
            log.warn("inputSum {} but needPay is {}", inputSum, paySummary.getNeedPay());
            throw new GlobalException(PAY_AMOUNT_ERROR);
        }

        //是否站内支付全额
        paramContext.setPkgFull(MathUtil.sameValue(pkgInput, paySummary.getNeedPay()));
    }


    private List<BigDecimal> getInputAmountItems(PayRequest payReq) {
        //校验金额精度
        List<BigDecimal> amoutDatas = new ArrayList<>();
        amoutDatas.add(payReq.getCardAmount());
        amoutDatas.add(payReq.getScore());
        amoutDatas.add(payReq.getBalance());
        amoutDatas.add(payReq.getThirdPayAmount());
        return amoutDatas;
    }

    /**
     * 获取所有输入项的总和
     *
     * @param amoutDatas
     * @return
     */
    private BigDecimal getSum(List<BigDecimal> amoutDatas) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal amount : amoutDatas) {
            sum = sum.add(amount);
        }
        return sum;
    }

    /**
     * 检查每个金额项目不能为负数
     *
     * @param amoutDatas
     */
    private void checkAmountItem(List<BigDecimal> amoutDatas) {
        BigDecimal zero = BigDecimal.ZERO;
        for (BigDecimal amount : amoutDatas) {
            if (MathUtil.less(amount, zero)) {
                throw new GlobalException(INPUT_AMOUNT_IS_NEGATIVE);
            }
        }
    }

    /**
     * 检查每个金额项目精度不能小于分
     *
     * @param amoutDatas
     */
    private void checkPrecision(List<BigDecimal> amoutDatas) {
        for (BigDecimal amount : amoutDatas) {
            if (!MathUtil.isFenPrecision(amount)) {
                throw new GlobalException(PAY_AMOUNT_PRECISION_ERROR);
            }
        }
    }
}