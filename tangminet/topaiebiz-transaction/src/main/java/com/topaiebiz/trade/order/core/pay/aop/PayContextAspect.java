package com.topaiebiz.trade.order.core.pay.aop;

import com.topaiebiz.trade.order.core.pay.context.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-09 19:51
 */
@Aspect
@Component
@Slf4j
public class PayContextAspect {

    @Pointcut("@annotation(com.topaiebiz.trade.order.core.pay.aop.PayContext)")
    public void payContextOperation() {
    }

    @Around("payContextOperation()")
    public Object aroundExec(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        } catch (Throwable throwable) {
            throw throwable;
        } finally {
            log.debug(">>>>>>>>>> clean pay ThreadLocal data");
            cleanThreadCache();
        }
    }

    private void cleanThreadCache(){
        MemberCardsContext.clear();
        MemberContext.clear();

        PayConfigContext.clear();
        PkgPayedContext.clear();
        PaySummaryContext.clear();
    }
}