package com.nebulapaas.common.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2017-12-19 12:09
 */
@Component
@ConfigurationProperties(prefix = "redis.pool")
@Data
public class JedisConnectionConfig {
    private String host;
    private int port;
    private String password;
    private int connectionTimeout;
    private int soTimeout;
    private int database;
    private String clientName;
}