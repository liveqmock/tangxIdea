package com.nebulapaas.common.redis.config;

import org.springframework.context.annotation.PropertySource;
import redis.clients.jedis.JedisPoolConfig;

/***
 * @author yfeng
 * @date 2017-12-19 12:45
 */
@PropertySource("redis.pool")
public class JedisPoolConfigWrapper extends JedisPoolConfig {
}