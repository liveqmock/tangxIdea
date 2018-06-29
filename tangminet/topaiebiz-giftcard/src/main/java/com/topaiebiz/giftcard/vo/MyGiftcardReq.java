package com.topaiebiz.giftcard.vo;

import com.nebulapaas.base.po.PagePO;

/**
 * @description: 我的礼卡请求参数
 * @author: Jeff Chen
 * @date: created in 下午12:59 2018/1/30
 */
public class MyGiftcardReq extends PagePO{

    /**
     * 用户id
     */
    private Long bindingMember;

    /**
     * 0-不可用礼卡，1-可用礼卡
     */
    private Integer category;

    public Long getBindingMember() {
        return bindingMember;
    }

    public void setBindingMember(Long bindingMember) {
        this.bindingMember = bindingMember;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }
}
