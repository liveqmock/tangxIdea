package com.topaiebiz.goods.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.topaiebiz.goods.entity.base.BaseAttrValue;

/**
 * @description: 属性值编辑实体
 * @author: Jeff Chen
 * @date: created in 上午9:54 2018/5/18
 */
@TableName("t_goo_attr_value_edit")
public class AttrValueEdit extends BaseAttrValue{
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
}
