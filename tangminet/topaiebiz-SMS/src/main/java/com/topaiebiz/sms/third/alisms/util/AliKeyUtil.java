package com.topaiebiz.sms.third.alisms.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class AliKeyUtil implements BeanPostProcessor{

    @Value("${alisms.ali_accessKey}")
    private String accessKeyId;

    @Value("${alisms.ali_accessSecret}")
    private String accessKeySecret;

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        AliSMSUtil.setAccessKeyId(accessKeyId);
        AliSMSUtil.setAccessKeySecret(accessKeySecret);
        return o;
    }
}
