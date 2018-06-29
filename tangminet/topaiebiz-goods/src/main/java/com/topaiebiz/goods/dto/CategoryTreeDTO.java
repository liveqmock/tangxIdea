package com.topaiebiz.goods.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 类目树对象
 * @author: Jeff Chen
 * @date: created in 下午4:55 2018/5/21
 */
public class CategoryTreeDTO implements Serializable{

    /**
     * 一级类目列表
     */
    List<CategoryNodeDTO> categoryTree;

    public List<CategoryNodeDTO> getCategoryTree() {
        return categoryTree;
    }

    public void setCategoryTree(List<CategoryNodeDTO> categoryTree) {
        this.categoryTree = categoryTree;
    }
}
