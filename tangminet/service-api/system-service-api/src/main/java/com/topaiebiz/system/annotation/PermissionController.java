package com.topaiebiz.system.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PermissionController {

    PermitType value() default PermitType.PLATFORM;

    //操作模块的名称。例如：商品管理  ，商品管理新增商品。便于以后好配置
    String operationName() default "";

}
