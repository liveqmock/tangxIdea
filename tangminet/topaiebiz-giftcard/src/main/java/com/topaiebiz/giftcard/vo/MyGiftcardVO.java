package com.topaiebiz.giftcard.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: C端展示礼卡
 * @author: Jeff Chen
 * @date: created in 下午1:29 2018/1/24
 */
@Data
public class MyGiftcardVO implements Serializable{

    /**
     * 卡片id
     */
    private Long unitId;

    /**
     * 批次id
     */
    private Long batchId;

    /**
     * 参考ApplyScopeEnum.java
     */
    private Integer applyScope;
    /**
     * 购买的订单id
     */
    private Long orderId;

    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 卡片名称
     */
    private String cardName;
    /**
     * 封面
     */
    private String cover;
    /**
     * 适用范围
     */
    private String scope;
    /**
     * 面值
     */
    private BigDecimal faceValue;
    /**
     * 余额
     */
    private BigDecimal balance;
    /**
     * 过期时间
     */
    private Date deadTime;

    /**
     * 卡状态
     */
    private Integer cardStatus;

    /**
     * 0实体卡 4电子卡
     */
    private Integer medium;

    /**
     * 0-不可转赠，1-可转赠
     */
    private Integer givenStatus;

}
