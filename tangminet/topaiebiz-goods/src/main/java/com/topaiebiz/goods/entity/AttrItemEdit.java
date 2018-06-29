package com.topaiebiz.goods.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.topaiebiz.goods.entity.base.BaseAttrItem;

/**
 * @description: 属性名编辑实体
 * @author: Jeff Chen
 * @date: created in 上午9:53 2018/5/18
 */
@TableName("t_goo_attr_item_edit")
public class AttrItemEdit extends BaseAttrItem {
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
