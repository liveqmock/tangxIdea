package com.topaiebiz.goods.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.topaiebiz.goods.entity.base.BaseBackendCategory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @description: 后台类目编辑实体
 * @author: Jeff Chen
 * @date: created in 上午9:56 2018/5/18
 */
@TableName("t_goo_backend_category_edit")
public class BackendCategoryEdit extends BaseBackendCategory{
    /**
     * 类目基本属性同步状态（名称，删除状态）：0未同步 1已同步
     */
    @TableField("syncStatus")
    private Integer syncStatus;

    @TableField("lastSyncTime")
    private Date lastSyncTime;

    /**
     * 类目的树属性同步状态（排序，层级）：0未同步 1同步
     */
    @TableField("treeSyncStatus")
    private Integer treeSyncStatus;

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Date getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(Date lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public Integer getTreeSyncStatus() {
        return treeSyncStatus;
    }

    public void setTreeSyncStatus(Integer treeSyncStatus) {
        this.treeSyncStatus = treeSyncStatus;
    }
}
