package com.topaiebiz.goods.entity.base;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;

import java.io.Serializable;

/**
 * <p>
 * 属性分组正式表--基类
 * </p>
 *
 * @author MMG123
 * @since 2018-05-18
 */
public class BaseAttrGroup extends BaseBizEntity<Long> {

    /**
     * 类目id
     */
    @TableField("categoryId")
    private Long categoryId;
    /**
     * 属性分组名称。
     */
    private String name;
    /**
     * 类目描述。
     */
    private String description;
    /**
     * 排序号
     */
    @TableField("sortNo")
    private Integer sortNo;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }
}
