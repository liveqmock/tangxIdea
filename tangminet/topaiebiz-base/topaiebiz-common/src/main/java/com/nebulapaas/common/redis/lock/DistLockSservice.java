package com.nebulapaas.common.redis.lock;


import com.nebulapaas.common.redis.vo.LockResult;

public interface DistLockSservice {

    /**
     * 锁定资源
     *
     * @param preKey
     * @param resourceId
     * @return 获取成功的锁凭证
     */
    LockResult lock(String preKey, String resourceId);

    LockResult lock(String preKey, Long resourceId);

    LockResult tryLock(String preKey, String resourceId);

    LockResult tryLock(String preKey, Long resourceId);

    LockResult tryLock(String preKey, String resourceId, long autoReleaseTime);

    LockResult tryLock(String preKey, Long resourceId, long autoReleaseTime);

    /**
     * 锁定资源
     *
     * @param preKey
     * @param resourceId
     * @param timeout
     * @param autoReleaseTime
     * @return 获取成功的锁凭证
     */
    LockResult lock(String preKey, String resourceId, long timeout, long autoReleaseTime);

    LockResult lock(String preKey, Long resourceId, long timeout, long autoReleaseTime);

    /**
     * 解锁资源
     *
     * @param lockResult 锁凭证
     */
    void unlock(LockResult lockResult);
}