package com.topaiebiz.goods.dto;

import com.nebulapaas.base.po.PagePO;

/**
 * @description: 属性项分页请求参数
 * @author: Jeff Chen
 * @date: created in 下午7:38 2018/5/22
 */
public class AttrItemReq extends PagePO{

    /**
     * 类目id
     */
    private Long categoryId;

    /**
     * 分组ID
     */
    private Long groupId;

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
}
