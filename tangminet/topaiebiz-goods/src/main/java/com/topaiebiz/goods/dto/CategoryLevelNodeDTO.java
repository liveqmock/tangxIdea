package com.topaiebiz.goods.dto;

import java.io.Serializable;

/**
 * @description: 类目层级节点DTO
 * @author: Jeff Chen
 * @date: created in 下午5:14 2018/5/23
 */
public class CategoryLevelNodeDTO implements Serializable {

    /**
     * 类目ID
     */
    private Long categoryId;
    /**
     * 类目名称
     */
    private String name;
    /**
     *类目（排序，层级，是否叶子变动）是否需要同步：1是0否
     */
    private Integer categoryNeedSync;
    /**
     * 类目基本属性（类目名称，删除状态，叶子关联的数据）是否需要同步：1是 0否
     */
    private Integer dataNeedSync;
    /**
     * 0 普通类目 1叶子
     */
    private Integer isLeaf;

    /**
     * 层级
     */
    private Integer level;

    private Byte deletedFlag;

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

    public Integer getCategoryNeedSync() {
        return categoryNeedSync;
    }

    public void setCategoryNeedSync(Integer categoryNeedSync) {
        this.categoryNeedSync = categoryNeedSync;
    }
    public Integer getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Integer isLeaf) {
        this.isLeaf = isLeaf;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getDataNeedSync() {
        return dataNeedSync;
    }

    public void setDataNeedSync(Integer dataNeedSync) {
        this.dataNeedSync = dataNeedSync;
    }

    public Byte getDeletedFlag() {
        return deletedFlag;
    }

    public void setDeletedFlag(Byte deletedFlag) {
        this.deletedFlag = deletedFlag;
    }
}
