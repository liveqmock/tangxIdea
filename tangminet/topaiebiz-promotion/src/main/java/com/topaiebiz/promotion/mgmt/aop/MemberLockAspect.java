package com.topaiebiz.promotion.mgmt.aop;

import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.topaiebiz.promotion.constants.PromotionConstants.CacheKey.MEMBER_LOCK_PREFIX;

/**
 * @Auther: xuyuhua
 * @Date: 2018/6/12 14:32
 * @Description: 控制重复操作
 */
@Aspect
@Component
@Slf4j
public class MemberLockAspect {
    @Autowired
    private DistLockSservice distLockSservice;

    @Pointcut("@annotation(com.topaiebiz.promotion.mgmt.aop.MemberLockOperation)")
    public void lockOperation() {

    }

    @Around("lockOperation()")
    public Object aroundExec(ProceedingJoinPoint joinPoint) throws Throwable {
        LockResult memberLock = null;
        try {
            memberLock = distLockSservice.tryLock(MEMBER_LOCK_PREFIX, MemberContext.getMemberId());
            if (!memberLock.isSuccess()) {
                log.warn("会员：{} 正在进行重复操作！", MemberContext.getMemberId());
                //重复操作
                throw new GlobalException(PromotionExceptionEnum.OPERATION_REPAIR);
            }
            return joinPoint.proceed();
        } catch (Throwable t) {
            throw t;
        } finally {
            distLockSservice.unlock(memberLock);
        }
    }
}
