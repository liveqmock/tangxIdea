package com.topaiebiz.giftcard.vo;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

/**
 * @description: 卡片绑定参数
 * @author: Jeff Chen
 * @date: created in 上午10:37 2018/1/27
 */
public class CardBindVO implements Serializable {

    /**
     * 卡号
     */
    @NotEmpty(message = "卡号填写不正确")
    private String cardNo;
    /**
     * 密码
     */
    @NotEmpty(message = "请输入六位密码")
    @Length(min = 6,max = 6,message = "请输入六位密码")
    private String pwd;

    /**
     * 绑定用户
     */
    private Long memberId;

    /**
     * 绑定途径 BindWayEnum
     */
    private Integer bindWay;
    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getBindWay() {
        return bindWay;
    }

    public void setBindWay(Integer bindWay) {
        this.bindWay = bindWay;
    }
}
