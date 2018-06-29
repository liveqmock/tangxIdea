package com.topaiebiz.timetask.quartzaop.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 18:53 2018/4/10
 * @Modified by:
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QuartzContextOperation {
}
