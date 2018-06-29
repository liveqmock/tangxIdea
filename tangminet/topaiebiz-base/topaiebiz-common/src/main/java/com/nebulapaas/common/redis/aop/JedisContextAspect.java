package com.nebulapaas.common.redis.aop;

/**
 * Created by yangfeng on 2017/2/7.
 */

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class JedisContextAspect {

    @Pointcut("@annotation(com.nebulapaas.common.redis.aop.JedisOperation)")
    public void jedisOperation() {
    }

    @Around("jedisOperation()")
    public Object aroundExec(ProceedingJoinPoint pjp) throws Throwable {
        JedisContext.loadJedisInstance();
        try {
            return pjp.proceed();
        } catch (Throwable t) {
            throw t;
        } finally {
            JedisContext.releaseJeids();
        }
    }
}