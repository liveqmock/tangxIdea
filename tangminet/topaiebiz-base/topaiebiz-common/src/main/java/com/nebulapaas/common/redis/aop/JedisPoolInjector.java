package com.nebulapaas.common.redis.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

@Component
@Slf4j
public class JedisPoolInjector implements InitializingBean {

    @Autowired
    private JedisPool innerJjedisPool;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> afterPropertiesSet >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        JedisContext.setJedisPool(innerJjedisPool);
        log.info(">>>>>>>>>>>> inject JedisContext a jedisPool instances successful");
    }
}