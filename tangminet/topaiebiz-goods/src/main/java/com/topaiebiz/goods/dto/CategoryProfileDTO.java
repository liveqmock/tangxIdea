package com.topaiebiz.goods.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 类目概要
 * @author: Jeff Chen
 * @date: created in 上午10:21 2018/5/23
 */
public class CategoryProfileDTO implements Serializable {

    /**
     * 类目路径
     */
    private String categoryPath;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 同步时间
     */
    private Date lastSyncTime;
    /**
     * 叶子数
     */
    private Integer leafCount;
    /**
     * 商品数
     */
    private Integer goodsCount;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 是否叶子
     */
    private Integer isLeaf;

    /**
     * 品牌待同步
     */
    private Integer brandNeedSync;
    /**
     * 属性待同步
     */
    private Integer attrNeedSync;
    /**
     * 属性分组待同步
     */
    private Integer groupNeedSync;

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(Date lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public Integer getLeafCount() {
        return leafCount;
    }

    public void setLeafCount(Integer leafCount) {
        this.leafCount = leafCount;
    }

    public Integer getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Integer goodsCount) {
        this.goodsCount = goodsCount;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Integer isLeaf) {
        this.isLeaf = isLeaf;
    }

    public Integer getBrandNeedSync() {
        return brandNeedSync;
    }

    public void setBrandNeedSync(Integer brandNeedSync) {
        this.brandNeedSync = brandNeedSync;
    }

    public Integer getAttrNeedSync() {
        return attrNeedSync;
    }

    public void setAttrNeedSync(Integer attrNeedSync) {
        this.attrNeedSync = attrNeedSync;
    }

    public Integer getGroupNeedSync() {
        return groupNeedSync;
    }

    public void setGroupNeedSync(Integer groupNeedSync) {
        this.groupNeedSync = groupNeedSync;
    }
}
