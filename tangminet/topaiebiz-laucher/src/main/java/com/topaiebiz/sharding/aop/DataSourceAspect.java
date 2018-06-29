package com.topaiebiz.sharding.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-02-05 19:10
 */
@Aspect
@Component
@Slf4j
@Order(1)
public class DataSourceAspect {

    @Pointcut("@annotation(com.nebulapaas.common.sharding.UseShardingDataSource)")
    public void shardingDBOperation() {
    }

    @Around("shardingDBOperation()")
    public Object aroundExec(ProceedingJoinPoint pjp) throws Throwable {
        log.info(">>>>>>> UseShardingDataSource ");
        DataSourceContextHolder.useShardingDataSource();
        try {
            return pjp.proceed();
        } finally {
            DataSourceContextHolder.clear();
        }
    }
}