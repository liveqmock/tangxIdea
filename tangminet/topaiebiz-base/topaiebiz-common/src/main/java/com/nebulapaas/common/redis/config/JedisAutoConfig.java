package com.nebulapaas.common.redis.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/***
 * @author yfeng
 * @date 2017-12-19 12:17
 */
@Configuration
@Slf4j
public class JedisAutoConfig {

    @Autowired
    private JedisConnectionConfig config;

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        return new JedisPoolConfigWrapper();
    }

    @Bean("defaultJedisPool")
    public JedisPool jedisPool() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        try {
            JedisPoolConfig poolConfig = jedisPoolConfig();
            return new JedisPool(poolConfig, config.getHost(), config.getPort(), config.getConnectionTimeout(),
                    config.getSoTimeout(), config.getPassword(), config.getDatabase(), config.getClientName(),
                    false, null, null, null);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

}