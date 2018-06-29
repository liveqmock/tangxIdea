package com.topaiebiz.goods.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 类目树上的一个节点
 * @author: Jeff Chen
 * @date: created in 下午4:47 2018/5/21
 */
public class CategoryNodeDTO implements Serializable{

    /**
     * 类目Id
     */
    private Long categoryId;
    /**
     * 类目名称
     */
    private String name;
    /**
     * 是否叶子目录：0不是 1是
     */
    private Integer isLeaf;
    /**
     * 当前节点所处层级
     */
    private Integer level;
    /**
     * 父类目。
     */
    private Long parentId;
    /**
     * 类目（排序，层级，是否叶子变动）是否需要同步：1是0否
     */
    private Integer categoryNeedSync;

    /**
     * 类目基本属性（类目名称，删除状态，关联的数据）是否需要同步：1是 0否
     */
    private Integer dataNeedSync;

    private Byte deletedFlag;
    /**
     * 下一级
     */
    private List<CategoryNodeDTO> children;

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

    public List<CategoryNodeDTO> getChildren() {
        return children;
    }

    public void setChildren(List<CategoryNodeDTO> children) {
        this.children = children;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getCategoryNeedSync() {
        return categoryNeedSync;
    }

    public void setCategoryNeedSync(Integer categoryNeedSync) {
        this.categoryNeedSync = categoryNeedSync;
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
