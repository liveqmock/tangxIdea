package com.topaiebiz.giftcard.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: C端展示的礼卡信息
 * @author: Jeff Chen
 * @date: created in 下午7:09 2018/1/24
 */
@Data
public class GiftcardShowVO implements Serializable{

    /**
     *卡id
     */
    private Long batchId;

    /**
     * 封面
     */
    private String cover;

    /**
     * 名称
     */
    private String cardName;
    /**
     * 面值
     */
    private BigDecimal faceValue;
    /**
     * 售价
     */
    private BigDecimal salePrice;

    /**
     * 适用范围说明
     */
    private String scope;

    /**
     * 失效时间
     */
    private Date deadTime;
}
