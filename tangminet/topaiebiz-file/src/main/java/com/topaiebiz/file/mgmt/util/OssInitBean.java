package com.topaiebiz.file.mgmt.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OssInitBean implements InitializingBean {

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.access_key_id}")
    private String accessKeyId;

    @Value("${oss.access_key_secret}")
    private String accessKeySecret;

    @Value("${oss.bucket_name}")
    private String bucket;

    @Value("${oss.env}")
    private String env;

    @Override
    public void afterPropertiesSet() throws Exception {
        OssUtils.setEndpoint(endpoint);
        OssUtils.setAccessKeyId(accessKeyId);
        OssUtils.setAccessKeySecret(accessKeySecret);
        OssUtils.setBucket(bucket);
        OssUtils.setEnv(env);
    }
}