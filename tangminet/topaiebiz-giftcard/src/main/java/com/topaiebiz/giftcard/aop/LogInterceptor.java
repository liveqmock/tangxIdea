package com.topaiebiz.giftcard.aop;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @description: Service日志拦截
 * @author: Jeff Chen
 * @date: created in 下午3:25 2018/1/30
 */
@Aspect
@Component
@Slf4j
public class LogInterceptor {
    private final String POINT_CUT = "execution(* com.topaiebiz.giftcard.service.*.*.*(..))||" +
            "execution(* com.topaiebiz.giftcard.api.*.*.*(..))";

    /**
     * 打印接口方法参数
     * @param joinPoint
     */
    @Before(value = POINT_CUT)
    public void beforeMethod(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        log.info("礼卡接口：{}，方法：{}，参数：{}", className, methodName, JSONObject.toJSON(joinPoint.getArgs()));

    }

    /**
     * 打印接口返回值
     * @param joinPoint
     * @param result
     */
    @AfterReturning(value = POINT_CUT, returning = "result")
    public void afterReturn(JoinPoint joinPoint,Object result) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
       // log.info("礼卡接口：{}，方法：{}，返回结果：{}", className, methodName, JSONObject.toJSON(result));
    }

    /**
     * 接口耗时
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = POINT_CUT)
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            throw e;
        }
        log.info("礼卡接口：{}，方法：{}，耗时：{} ms",className,methodName,stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }
}
