package com.topaiebiz.system.util;

import com.topaiebiz.system.annotation.NotLoginPermit;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.dto.ResourceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

@Slf4j
@Component
public class PermissionURLProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = bean.getClass();
        if (AnnotationUtils.findAnnotation(clazz, Controller.class) == null) {
            return bean;
        }
        loadPermitUrls(clazz);
        return bean;
    }

    private void loadPermitUrls(Class clazz) {
        RequestMapping classRequestMapping = AnnotationUtils.findAnnotation(clazz, RequestMapping.class);
        String[] classMappings = {};
        if(classRequestMapping != null){
            classMappings = classRequestMapping.path();
        }

        //获取类中所有成员方法
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            PermissionController permissionController = AnnotationUtils.findAnnotation(method, PermissionController.class);
            //不是后台的放过
            if(permissionController == null)  continue;
            PermitType permitType = permissionController.value();
            RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
            PostMapping postMapping = AnnotationUtils.findAnnotation(method, PostMapping.class);
            GetMapping getMapping = AnnotationUtils.findAnnotation(method, GetMapping.class);
            NotLoginPermit notLoginPermit = AnnotationUtils.findAnnotation(method, NotLoginPermit.class);
            //不是requestmapping的放过
            if (requestMapping == null && postMapping == null && getMapping == null) continue;
            String[] methodValues = {};
            if (requestMapping != null) {
                methodValues = requestMapping.value().length == 0 ? methodValues : requestMapping.value();
            }
            if (postMapping != null) {
                methodValues = postMapping.value().length == 0 ? methodValues : postMapping.value();
            }
            if (getMapping != null) {
                methodValues = getMapping.value().length == 0 ? methodValues : getMapping.value();
            }
            for (String classMapping : classMappings) {
                classMapping = classMapping.startsWith("/") ? classMapping : ("/" + classMapping);
                classMapping = classMapping.endsWith("/") ? classMapping.substring(0, classMapping.length() - 1) : classMapping;
                for (String methodValue : methodValues) {
                    methodValue = methodValue.startsWith("/") ? methodValue : ("/" + methodValue);
                    methodValue = methodValue.endsWith("/") ? methodValue.substring(0, methodValue.length() - 1) : methodValue;
                    //平台
                    if (permitType.equals(PermitType.PLATFORM)) {
                        CustomerUrlUtil.addPlatformURLList(classMapping + methodValue);
                        //商家
                    } else if (permitType.equals(PermitType.MERCHANT)) {
                        CustomerUrlUtil.addMerchantURLList(classMapping + methodValue);
                    } else if (permitType.equals(PermitType.ENTER)){
                        CustomerUrlUtil.addEnterURLList(classMapping + methodValue);
                    }
                    if (notLoginPermit != null) {
                        CustomerUrlUtil.addPermissionURLList(classMapping + methodValue);
                    }
                    ResourceDto resourceDto = new ResourceDto();
                    resourceDto.setOperationName(permissionController.operationName());
                    resourceDto.setPermitType(permitType);
                    resourceDto.setURL(classMapping + methodValue);
                    ResourceCollectUtil.addResourceList(resourceDto);
//                    log.info("PermissionURLProcessor postProcessAfterInitialization addUrl-------" + (classMapping + methodValue));
                }
            }
        }
    }

}
