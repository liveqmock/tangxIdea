package com.topaiebiz.giftcard.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description: 卡实体展示信息
 * @author: Jeff Chen
 * @date: created in 上午9:17 2018/1/17
 */
@Data
public class GiftcardUnitVO implements Serializable{

    /**
     * 主键id
     */
    private Long unitId;
    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 发行数量
     */
    private Integer issueNum;
    /**
     * 卡片名称
     */
    private String cardName;

    /**
     * 卡属性
     */
    private Integer cardAttr;
    /**
     * 关联标签id
     */
    private Long labelId;

    /**
     * 标签名称
     */
    private String labelName;

    /**
     * 封面
     */
    private String cover;

    /**
     * 绑定时间
     */
    private Date bindingTime;

    /**
     * 失效时间
     */
    private Date deadTime;
    /**
     * 有效天数
     */
    private Integer validDays;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 面值
     */
    private BigDecimal faceValue;

    /**
     * 售价
     */
    private BigDecimal salePrice;
    /**
     * 平台贴现
     */
    private BigDecimal platformDiscount;

    /**
     * 店铺贴现
     */
    private BigDecimal storeDiscount;
    /**
     * 卡状态:0-未绑定，1-已绑定，2-已激活，3-已用完，4-已过期，5-已冻结
     */
    private Integer cardStatus;

    /**
     * 介质
     */
    private String mediumStr;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 操作日志列表
     */
    List<CardOpLogVO> opLogList;
}
