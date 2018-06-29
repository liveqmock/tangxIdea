package com.topaiebiz.trade.order.core.order.aop;

import com.topaiebiz.trade.order.core.order.context.*;
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
public class ContextClearAspect {

    @Pointcut("@annotation(com.topaiebiz.trade.order.core.order.aop.ContextOperation)")
    public void contextOperation() {
    }

    @Around("contextOperation()")
    public Object aroundExec(ProceedingJoinPoint pjp) throws Throwable {
        try {          
            return pjp.proceed();
        } catch (Throwable throwable) {
            throw throwable;
        } finally {
            log.debug(">>>>>>>>>> clean ThreadLocal data");
            cleanThreadCache();
        }
    }

    private void cleanThreadCache() {
        CartIdContext.clear();
        SkuContext.clear();
        SkuIdContext.clear();
        StoreContext.clear();
        StoreIdContext.clear();
        AddressContext.clear();
        PromotionsContext.clear();
        BuyerContext.clear();
        CartMapContext.clear();
        PromotionDiscountContext.clear();
        GoodsPromotionsContext.clear();
        PayIdContext.clear();
        OrderSummaryContext.clear();
    }
}