package com.nebulapaas.common.redis.aop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by yangfeng on 2017/1/29.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JedisContext {
    private static final ThreadLocal<JedisHolder> itemThreadLocal = new ThreadLocal<>();

    private static JedisPool jedisPool;

    private static class JedisHolder {
        @Getter
        private Jedis instance;
        private int count;

        public JedisHolder(Jedis instance) {
            this.instance = instance;
            countInc();
        }

        public void countInc() {
            count++;
        }

        public void countDec() {
            count--;
        }

        public boolean canRelase() {
            return count == 0;
        }
    }

    protected static void setJedisPool(JedisPool inject) {
        JedisContext.jedisPool = inject;
    }

    public static void loadJedisInstance() {
        if (itemThreadLocal.get() == null) {
            Jedis jedis = jedisPool.getResource();
            JedisHolder jedisHolder = new JedisHolder(jedis);
            itemThreadLocal.set(jedisHolder);
        } else {
            JedisHolder item = itemThreadLocal.get();
            item.countInc();
        }
    }

    public static Jedis getJedis() {
        JedisHolder jedisHolder = itemThreadLocal.get();
        if (jedisHolder == null) {
            log.error(">>> get jedis fail !!!!!");
            return null;
        }
        return jedisHolder.getInstance();
    }

    public static void releaseJeids() {
        if (log.isInfoEnabled()) {
            log.debug("JedisContext.releaseJeids()");
        }
        JedisHolder jedisHolder = itemThreadLocal.get();
        jedisHolder.countDec();
        if (jedisHolder.canRelase()) {
            jedisHolder.getInstance().close();
            itemThreadLocal.remove();
        }
    }


}