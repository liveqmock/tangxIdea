package com.topaiebiz.goods.entity.base;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;

import java.io.Serializable;

/**
 * <p>
 * 商品后台类目表，存储后台类目信息--基类
 * </p>
 *
 * @author MMG123
 * @since 2018-05-18
 */
public class BaseBackendCategory extends BaseBizEntity<Long> {
    /**
     * 类目名称。
     */
    private String name;
    /**
     * 类目等级(1 一级 2 二级 3 三级)。
     */
    private Integer level;
    /**
     * 类目排序号
     */
    @TableField("sortNo")
    private Integer sortNo;
    /**
     * 父类目。
     */
    @TableField("parentId")
    private Long parentId;
    /**
     * 是否叶子目录：0不是 1是
     */
    @TableField("isLeaf")
    private Integer isLeaf;
    /**
     * 备注
     */
    private String memo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Integer isLeaf) {
        this.isLeaf = isLeaf;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

}
