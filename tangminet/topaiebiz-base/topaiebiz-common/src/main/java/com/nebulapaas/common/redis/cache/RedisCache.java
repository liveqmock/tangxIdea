package com.nebulapaas.common.redis.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.nebulapaas.common.redis.aop.JedisOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/***
 * @author yfeng
 * @date 2017-12-19 12:54
 */
@Component
public class RedisCache {

    @JedisOperation
    public boolean set(String key, Object value) {
        Jedis jedis = JedisContext.getJedis();
        String val = null;
        if (value instanceof String) {
            val = (String) value;
        } else {
            val = JSON.toJSONString(value);
        }
        String result = jedis.set(key, val);
        return result != null;
    }

    @JedisOperation
    public boolean set(String key, Object value, int expire) {
        Jedis jedis = JedisContext.getJedis();
        String val = null;
        if (value instanceof String) {
            val = (String) value;
        } else {
            val = JSON.toJSONString(value);
        }
        String result = jedis.setex(key, expire, val);
        return result != null;
    }

    @JedisOperation
    public boolean exist(String key) {
        return JedisContext.getJedis().exists(key);
    }

    @JedisOperation
    public <T> T get(String key, Class<T> clazz) {
        String data = getVal(key);
        if (data == null) {
            return null;
        }
        return JSON.parseObject(data, clazz);
    }

    private String getVal(String key) {
        Jedis jedis = JedisContext.getJedis();
        return jedis.get(key);
    }

    @JedisOperation
    public String get(String key) {
        return getVal(key);
    }

    @JedisOperation
    public Integer getInt(String key) {
        String val = get(key);
        if (StringUtils.isBlank(val)) {
            return null;
        }
        return Integer.parseInt(val);
    }

    @JedisOperation
    public Long getLong(String key) {
        String val = get(key);
        if (StringUtils.isBlank(val)) {
            return null;
        }
        return Long.parseLong(val);
    }

    @JedisOperation
    public Boolean getBoolean(String key) {
        String val = get(key);
        if (StringUtils.isBlank(val)) {
            return null;
        }
        return Boolean.parseBoolean(val);
    }

    @JedisOperation
    public <T> T getObject(String key, TypeReference<T> typeRef) {
        String value = getVal(key);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return JSON.parseObject(value, typeRef);
    }

    @JedisOperation
    public <T> List<T> getListValue(String key, Class<T> clazz) {
        String value = getVal(key);
        if (StringUtils.isBlank(value)) {
            return Collections.emptyList();
        }
        return JSON.parseArray(value, clazz);
    }

    @JedisOperation
    public Long delete(String key) {
        Jedis jedis = JedisContext.getJedis();
        return jedis.del(key);
    }

    @JedisOperation
    public Long incr(String key) {
        Jedis jedis = JedisContext.getJedis();
        return jedis.incr(key);
    }

    @JedisOperation
    public Long incr(String key, int value) {
        Jedis jedis = JedisContext.getJedis();
        return jedis.incrBy(key, value);
    }

    private Long decrKey(String key, int delta) {
        Jedis jedis = JedisContext.getJedis();
        return jedis.decrBy(key, delta);
    }

    @JedisOperation
    public Long decr(String key) {
        Jedis jedis = JedisContext.getJedis();
        return jedis.decr(key);
    }

    @JedisOperation
    public Long decr(String key, int value) {
        Jedis jedis = JedisContext.getJedis();
        return jedis.decrBy(key, value);
    }

    @JedisOperation
    public <T> List<T> mget(List<String> keys, Class<T> clazz) {
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptyList();
        }
        Jedis jedis = JedisContext.getJedis();

        //构建批量查询key 数组
        String[] keyArray = keys.toArray(new String[keys.size()]);
        //执行批量查询
        List<String> datas = jedis.mget(keyArray);

        List<T> result = new ArrayList<>();

        //遍历取出对应位置的结果
        for (int i = 0; i < keys.size(); i++) {
            String itemVal = datas.get(i);
            //若其中一个key不存在，则对应此位置value是一个null
            if (itemVal == null) {
                result.add(null);
            } else {
                result.add(JSON.parseObject(itemVal, clazz));
            }
        }
        return result;
    }

    @JedisOperation
    public Long expire(String key, int seconds) {
        Jedis jedis = JedisContext.getJedis();
        return jedis.expire(key, seconds);
    }

    /**
     * 删除符合给定模式 pattern* 的 key 。
     *
     * @param pattern
     */
    @JedisOperation
    public Long delKeys(String pattern) {
        Long count = 0L;
        Jedis jedis = JedisContext.getJedis();
        Set<String> timeKeys = jedis.keys(pattern + "*");
        if (CollectionUtils.isEmpty(timeKeys)) {
            return count;
        }
        for (String key : timeKeys) {
            count += jedis.del(key);
        }
        return count;
    }

    @JedisOperation
    public Long hset(String key, String field, String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field) || StringUtils.isBlank(value)) {
            return 0L;
        }
        Jedis jedis = JedisContext.getJedis();
        return jedis.hset(key, field, value);
    }

    @JedisOperation
    public String hget(String key, String field) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            return null;
        }
        Jedis jedis = JedisContext.getJedis();
        return jedis.hget(key, field);
    }
}