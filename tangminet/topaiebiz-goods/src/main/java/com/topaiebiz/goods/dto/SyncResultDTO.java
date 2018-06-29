package com.topaiebiz.goods.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 同步结果
 * @author: Jeff Chen
 * @date: created in 上午10:50 2018/5/24
 */
public class SyncResultDTO implements Serializable{

    /**
     * 同步总数
     */
    private Integer total;
    /**
     * 成功总数
     */
    private Integer succCount;
    /**
     * 是否总数
     */
    private Integer failCount;
    /**
     * 失败信息列表
     */
    private List<SyncFailDTO> failMsgList;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getSuccCount() {
        return succCount;
    }

    public void setSuccCount(Integer succCount) {
        this.succCount = succCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public List<SyncFailDTO> getFailMsgList() {
        return failMsgList;
    }

    public void setFailMsgList(List<SyncFailDTO> failMsgList) {
        this.failMsgList = failMsgList;
    }
}
