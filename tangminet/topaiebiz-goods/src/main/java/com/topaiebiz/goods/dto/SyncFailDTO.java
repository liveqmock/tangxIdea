package com.topaiebiz.goods.dto;

import java.io.Serializable;

/**
 * @description: 同步失败信息
 * @author: Jeff Chen
 * @date: created in 上午10:45 2018/5/24
 */
public class SyncFailDTO implements Serializable{

    /**
     * 同步项
     */
    private String syncItem;
    /**
     * 名称
     */
    private String name;
    /**
     * 失败信息
     */
    private String failMsg;

    public String getSyncItem() {
        return syncItem;
    }

    public void setSyncItem(String syncItem) {
        this.syncItem = syncItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }
}
