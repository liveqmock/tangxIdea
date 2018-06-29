package com.topaiebiz.goods.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.topaiebiz.goods.entity.base.BaseCategoryBrand;

/**
 * @description: 类目关联品牌编辑实体
 * @author: Jeff Chen
 * @date: created in 上午9:55 2018/5/18
 */
@TableName("t_goo_category_brand_edit")
public class CategoryBrandEdit extends BaseCategoryBrand{
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
