package com.nebulapaas.common.redis.lock;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.nebulapaas.common.redis.aop.JedisOperation;
import com.nebulapaas.common.redis.vo.LockResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import org.apache.commons.lang3.StringUtils;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * Created by yangfeng on 2017/1/24.
 */
@Component
@Slf4j
public class DistLockServiceImpl implements DistLockSservice {

    private static final String DEL_SCRIPT =
            "if redis.call('get',KEYS[1]) == ARGV[1] then " +
                    "    return redis.call('del',KEYS[1]) " +
                    "else " +
                    "    return 0 " +
                    "end";
    private static final long ROLLING_WAIT_MILLS = 10;

    private static final long DEFAULT_TIMEOUT_MILLS = 100;

    private static final long DEFAULT_AUTO_RELEASE_TIME = 3000;

    @Override
    @JedisOperation
    public LockResult lock(String preKey, String resourceId) {
        return doLock(preKey, resourceId, DEFAULT_TIMEOUT_MILLS, DEFAULT_AUTO_RELEASE_TIME);
    }

    @Override
    public LockResult lock(String preKey, Long resourceId) {
        return doLock(preKey, resourceId.toString(), DEFAULT_TIMEOUT_MILLS, DEFAULT_AUTO_RELEASE_TIME);
    }

    @Override
    @JedisOperation
    public LockResult lock(String preKey, String resourceTypeId, long timeoutMills, long autoReleaseMills) {
        return doLock(preKey, resourceTypeId, timeoutMills, autoReleaseMills);
    }

    @Override
    @JedisOperation
    public LockResult lock(String preKey, Long resourceTypeId, long timeoutMills, long autoReleaseMills) {
        return doLock(preKey, resourceTypeId.toString(), timeoutMills, autoReleaseMills);
    }

    private LockResult doLock(String preKey, String resourceTypeId, long timeoutMills, long autoReleaseMills) {
        //step 1 : verify input params
        if (StringUtils.isBlank(preKey) || StringUtils.isBlank(resourceTypeId) || timeoutMills <= 0 || autoReleaseMills <= 0) {
            log.error("dist-lock fail preKey :{},resourceId:{},timeout:{},autoReleaseTime:{}", preKey, resourceTypeId, timeoutMills, autoReleaseMills);
            return new LockResult("");
        }
        //step 2 : generate lock params
        final String key = buildLockKey(preKey, resourceTypeId);
        final String token = UUID.randomUUID().toString();
        final long timeoutAt = System.currentTimeMillis() + timeoutMills;

        //step 3 : get lock
        Stopwatch sw = Stopwatch.createStarted();
        Jedis jedis = JedisContext.getJedis();
        if (jedis == null) {
            log.error("jedis is null when use dis-lock");
            return new LockResult("");
        }
        while (System.currentTimeMillis() < timeoutAt) {
            String result = jedis.set(key, token, "nx", "px", autoReleaseMills);
            log.info("lock key:{},token:{} >>> result:{}", key, token, result);
            if (result != null) {
                log.warn(">>> lock key:{},token:{},spend:{}ms", key, token, sw.elapsed(TimeUnit.MILLISECONDS));
                return new LockResult(key, token);
            }

            //rolling wait
            try {
                Thread.sleep(ROLLING_WAIT_MILLS);
            } catch (Exception ex) {
            }
        }
        return new LockResult("lock fail");
    }

    @Override
    @JedisOperation
    public LockResult tryLock(String preKey, String resourceId) {
        return doTryLock(preKey, resourceId, DEFAULT_AUTO_RELEASE_TIME);
    }

    @Override
    @JedisOperation
    public LockResult tryLock(String preKey, Long resourceId) {
        return doTryLock(preKey, resourceId.toString(), DEFAULT_AUTO_RELEASE_TIME);
    }

    @Override
    @JedisOperation
    public LockResult tryLock(String preKey, String resourceId, long autoReleaseTime) {
        return doTryLock(preKey, resourceId, autoReleaseTime);
    }

    @Override
    @JedisOperation
    public LockResult tryLock(String preKey, Long resourceId, long autoReleaseTime) {
        return doTryLock(preKey, resourceId.toString(), autoReleaseTime);
    }

    private LockResult doTryLock(String preKey, String resourceTypeId, long autoReleaseMills) {
        //step 1 : verify input params
        if (StringUtils.isBlank(preKey) || StringUtils.isBlank(resourceTypeId) || autoReleaseMills <= 0) {
            log.error("dist-lock fail preKey :{},resourceId:{},timeout:{},autoReleaseTime:{}", preKey, resourceTypeId, autoReleaseMills);
            return new LockResult("param error");
        }
        //step 2 : generate lock params
        final String key = buildLockKey(preKey, resourceTypeId);
        final String token = UUID.randomUUID().toString();

        //step 3 : get lock
        Stopwatch sw = Stopwatch.createStarted();
        Jedis jedis = JedisContext.getJedis();
        if (jedis == null) {
            log.error("jedis is null when use dis-lock");
            return new LockResult("Jedis instance get fail");
        }

        String result = jedis.set(key, token, "nx", "px", autoReleaseMills);
        log.info("tryLock key:{},token:{} >>> result:{}", key, token, result);
        if (result != null) {
            log.warn(">>> tryLock key:{},token:{},spend:{}ms", key, token, sw.elapsed(TimeUnit.MILLISECONDS));
            return new LockResult(key, token);
        }
        return new LockResult("tryLock fail");
    }

    @Override
    @JedisOperation
    public void unlock(LockResult lockResult) {
        //step 1 : check lockResult
        if (lockResult == null || !lockResult.isSuccess()) {
            log.warn("unlock fail with empty result {}", JSON.toJSONString(lockResult));
            return;
        }
        if (log.isInfoEnabled()) {
            log.info("unlock {}", JSON.toJSONString(lockResult));
        }
        //step 2 : validate input params
        if (StringUtils.isBlank(lockResult.getResourceKey()) || StringUtils.isBlank(lockResult.getLockToken())) {
            log.error("unlock fail with wrong params {}", JSON.toJSONString(lockResult));
            return;
        }

        //step 3 : eval script with param
        try {
            Jedis jedis = JedisContext.getJedis();
            jedis.eval(DEL_SCRIPT, 1, lockResult.getResourceKey(), lockResult.getLockToken());
        } catch (Exception ex) {
            log.error("unlock fail with lockResult {}", lockResult, ex);
        }
    }

    private String buildLockKey(String preKey, String resourceTypeId) {
        return StringUtils.join("dist_lock_", preKey, "_", resourceTypeId);
    }
}