package com.topaiebiz.goods.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.topaiebiz.goods.entity.base.BaseAttrGroup;

/**
 * @description: 属性分组编辑对象
 * @author: Jeff Chen
 * @date: created in 上午9:46 2018/5/18
 */
@TableName("t_goo_attr_group_edit")
public class AttrGroupEdit extends BaseAttrGroup {

    /**
     * 同步状态：0未同步 1已同步
     */
    @TableField("syncStatus")
    private Integer syncStatus;

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    @Override
    public String toString() {
        return "AttrGroupEdit{" +
                "syncStatus=" + syncStatus +
                "} " + super.toString();
    }
}
