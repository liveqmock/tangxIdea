package com.nebulapaas.common.redis.vo;

import lombok.Getter;

@Getter
public class LockResult {
    private boolean success = false;
    private String errorMsg;
    private String resourceKey;
    private String lockToken;

    public LockResult(String errorMsg) {
        this.success = false;
        this.errorMsg = errorMsg;
    }

    public LockResult(String resourceKey, String lockToken) {
        this.success = true;
        this.resourceKey = resourceKey;
        this.lockToken = lockToken;
    }
}