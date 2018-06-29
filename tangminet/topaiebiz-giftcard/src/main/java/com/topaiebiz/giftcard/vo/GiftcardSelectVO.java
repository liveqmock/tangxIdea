package com.topaiebiz.giftcard.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 礼卡精选展示信息
 * @author: Jeff Chen
 * @date: created in 下午7:28 2018/1/18
 */
@Data
public class GiftcardSelectVO implements Serializable {

    /**
     * 精选id
     */
    private Long selectId;
    /**
     * 发行id
     */
    private Long batchId;
    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 卡片名称
     */
    private String cardName;

    /**
     * 标签名称
     */
    private String labelName;
    /**
     * 面值
     */
    private BigDecimal faceValue;

    /**
     * 售价
     */
    private BigDecimal salePrice;

    /**
     * 优先级
     */
    private Integer priority;
    /**
     * 发行数量
     */
    private Integer issueNum;
    /**
     * 有效天数
     */
    private Integer validDays;
    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 适用范围
     */
    private Integer applyScope;

    /**
     * 封面
     */
    private String cover;
    /**
     * 序号
     */
    private Integer seq;

    /**
     * 卡属性
     */
    private Integer cardAttr;
}
