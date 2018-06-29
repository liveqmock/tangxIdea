package com.topaiebiz.goods.entity.base;

import com.baomidou.mybatisplus.annotations.TableField;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;

/**
 * <p>
 * 属性名正式表--基类
 * </p>
 *
 * @author MMG123
 * @since 2018-05-18
 */
public class BaseAttrItem extends BaseBizEntity<Long> {

    /**
     * 所属类目。
     */
    @TableField("categoryId")
    private Long categoryId;
    /**
     * 属性分组id
     */
    @TableField("groupId")
    private Long groupId;
    /**
     * 属性名
     */
    @TableField("attrName")
    private String attrName;
    /**
     * 属性类型。1 文本 2日期 3 数字 4 时间
     */
    @TableField("valueType")
    private Integer valueType;
    /**
     * 默认单位。
     */
    @TableField("defaultUnit")
    private String defaultUnit;
    /**
     * 是否为销售属性（1是，0不是）。
     */
    @TableField("isSale")
    private Integer isSale;
    /**
     * 是否为必填项（1是，0不是）。
     */
    @TableField("isMust")
    private Integer isMust;
    /**
     * 是否可以自定义（1是，0不是）
     */
    @TableField("isCustom")
    private Integer isCustom;
    /**
     * 是否用来筛选（1是，0不是）
     */
    @TableField("isFilter")
    private Integer isFilter;
    /**
     * 排序号。
     */
    @TableField("sortNo")
    private Long sortNo;
    /**
     * 描述
     */
    @TableField("description")
    private String description;
    /**
     * 属性类型：1 平台定义 2商家定义
     */
    @TableField("attrType")
    private Integer attrType;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }

    public String getDefaultUnit() {
        return defaultUnit;
    }

    public void setDefaultUnit(String defaultUnit) {
        this.defaultUnit = defaultUnit;
    }

    public Integer getIsSale() {
        return isSale;
    }

    public void setIsSale(Integer isSale) {
        this.isSale = isSale;
    }

    public Integer getIsMust() {
        return isMust;
    }

    public void setIsMust(Integer isMust) {
        this.isMust = isMust;
    }

    public Integer getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(Integer isCustom) {
        this.isCustom = isCustom;
    }

    public Integer getIsFilter() {
        return isFilter;
    }

    public void setIsFilter(Integer isFilter) {
        this.isFilter = isFilter;
    }

    public Long getSortNo() {
        return sortNo;
    }

    public void setSortNo(Long sortNo) {
        this.sortNo = sortNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAttrType() {
        return attrType;
    }

    public void setAttrType(Integer attrType) {
        this.attrType = attrType;
    }

}
