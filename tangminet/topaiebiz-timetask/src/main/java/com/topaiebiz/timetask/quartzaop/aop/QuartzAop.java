package com.topaiebiz.timetask.quartzaop.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.topaiebiz.basic.api.ConfigApi;
import com.topaiebiz.timetask.quartzaop.constants.QuartzConstants;
import com.topaiebiz.timetask.quartzaop.uitl.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 11:25 2018/4/10
 * @Modified by:
 */
@Aspect
@Component
@Slf4j
public class QuartzAop {

    @Autowired
    private ConfigApi configApi;

    private String localIp = NetUtil.getCurrentIp();

    @Pointcut("@annotation(com.topaiebiz.timetask.quartzaop.aop.QuartzContextOperation)")
    public void quartzAllCut() {
    }


    @Around("quartzAllCut()")
    public void TimedTaskLock(ProceedingJoinPoint joinPoint) throws Throwable {
        if (isIpRight(localIp)) {
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            String name = className + "#" + methodName;
            if (isTaskRunning(name)) {
                joinPoint.proceed();
            } else {
                log.warn("该定时任务---{}---未配置到config表 或 该定时任务的flog为0,该定时任务未能启动！", name);
            }
        } else {
            log.warn("该定时任务不能运行在{}的机器上，详情查看配置表！", localIp);
        }

    }

    public boolean isIpRight(String localIp) {
        String runIp = configApi.getConfig(QuartzConstants.CODE);
        if (StringUtils.isBlank(runIp)) {
            return false;
        }
        return localIp.equals(runIp);
    }

    public boolean isTaskRunning(String name) {
        String result = configApi.getConfig(QuartzConstants.TIME_TASK_LIST_STATUS);
        if (StringUtils.isBlank(result)) {
            return true;
        }
        Map<String, Boolean> config = JSON.parseObject(result, new TypeReference<Map<String, Boolean>>() {
        });
        if (config.containsKey(name)) {
            return config.get(name);
        }
        return true;
    }

}
