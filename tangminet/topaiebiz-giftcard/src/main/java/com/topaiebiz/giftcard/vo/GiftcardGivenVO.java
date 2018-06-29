package com.topaiebiz.giftcard.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description: 转赠页面展示
 * @author: Jeff Chen
 * @date: created in 下午4:55 2018/1/19
 */
@Data
public class GiftcardGivenVO implements Serializable{

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 卡名称
     */
    private String cardName;
    /**
     * 副标题
     */
    private String subtitle;
    /**
     * 面值
     */
    private BigDecimal faceValue;
    /**
     * 封面
     */
    private String cover;
}
