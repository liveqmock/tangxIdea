package com.topaiebiz.trade.order.service.impl;

import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.order.core.pay.action.PayActionChain;
import com.topaiebiz.trade.order.core.pay.aop.PayContext;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.context.PaySummaryContext;
import com.topaiebiz.trade.order.core.pay.handler.PayContextHandlerChain;
import com.topaiebiz.trade.order.dao.OrderPayDao;
import com.topaiebiz.trade.order.dto.pay.PaySummaryDTO;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.service.OrderPayService;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.trade.order.util.PayOrderHelper;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static com.topaiebiz.trade.order.core.config.PaySubmitConfig.PayHandlerChain.SUBMIT_CHAIN;
import static com.topaiebiz.trade.order.core.config.PaySubmitConfig.PayHandlerChain.SUMMARY_CHAIN;
import static com.topaiebiz.trade.order.exception.PaymentExceptionEnum.PAY_SUBMIT_DUPLICATE;

/***
 * @author yfeng
 * @date 2018-01-18 15:35
 */
@Slf4j
@Component
public class OrderPayServiceImpl implements OrderPayService {

    @Resource(name = SUMMARY_CHAIN)
    private PayContextHandlerChain summaryChain;

    @Resource(name = SUBMIT_CHAIN)
    private PayContextHandlerChain submitChain;

    @Autowired
    private PayActionChain payActionChain;

    @Autowired
    private DistLockSservice distLockSservice;

    @Autowired
    private OrderPayDao orderPayDao;

    @Autowired
    private PayOrderHelper payOrderHelper;

    @Override
    @PayContext
    public PaySummaryDTO getPaySummary(BuyerBO buyerBO, Long payId) {
        PayRequest payRequest = new PayRequest();
        payRequest.setPayId(payId);
        summaryChain.prepareParamContext(buyerBO, payRequest);
        return PaySummaryContext.get();
    }

    @Override
    @PayContext
    @Transactional(rollbackFor = Exception.class)
    public PayParamContext submitPay(BuyerBO buyerBO, PayRequest payRequest) {
        LockResult memberLock = null;
        try {
            memberLock = distLockSservice.tryLock(Constants.LockOperatons.TRADE_ORDER_PAY_, payRequest.getPayId());
            if (!memberLock.isSuccess()) {
                throw new GlobalException(PAY_SUBMIT_DUPLICATE);
            }

            PayParamContext payParamContext = submitChain.prepareParamContext(buyerBO, payRequest);
            payActionChain.action(buyerBO, payParamContext, payRequest);
            return payParamContext;
        } finally {
            distLockSservice.unlock(memberLock);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean payNotify(Long payOrderId, String payMethod, BigDecimal amount, String outTradeNo) {
        log.info("----------进入OrderPayServiceImpl payNotify--------------");
        LockResult memberLock = null;
        try {
            memberLock = distLockSservice.tryLock(Constants.LockOperatons.TRADE_ORDER_PAY_, payOrderId);
            if (!memberLock.isSuccess()) {
                return false;
            }
            return doPayNotify(payOrderId, payMethod, amount, outTradeNo);
        } finally {
            distLockSservice.unlock(memberLock);
        }
    }

    private boolean doPayNotify(Long payOrderId, String payMethod, BigDecimal amount, String outTradeNo) {
        OrderPayEntity payEntity = orderPayDao.selectById(payOrderId);

        //step 1 : 已支付校验
        if (OrderConstants.PayStatus.SUCCESS.equals(payEntity.getPayState())) {
            log.info("pay order {} has bean success");
            // 已经支付过了
            return true;
        }
        log.info("----------通过已支付校验！");

        //step 2 : 三方支付金额校验
        BigDecimal thirdAmount = payOrderHelper.needPay(payEntity);
        if (!MathUtil.sameValue(thirdAmount, amount)) {
            log.warn("pay order {} with third amount {} but input is {}", payOrderId, thirdAmount, amount);
            return false;
        }
        log.info("----------通过三方支付金额校验！");

        //step 3 : 变更订单状态
        payOrderHelper.updatePayOrderAndNotify(payEntity, payMethod, outTradeNo);
        return true;
    }
}