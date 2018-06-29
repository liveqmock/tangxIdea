package com.topaiebiz.goods.entity.base;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;

import java.io.Serializable;

/**
 * <p>
 * 后台类目关联品牌正式表--基类
 * </p>
 *
 * @author MMG123
 * @since 2018-05-18
 */
public class BaseCategoryBrand extends BaseBizEntity<Long> {
    /**
     * 类目id
     */
    @TableField("categoryId")
    private Long categoryId;
    /**
     * 品牌id
     */
    @TableField("brandId")
    private Long brandId;
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

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

}
