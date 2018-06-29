package com.topaiebiz.giftcard.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 消费日志
 * @author: Jeff Chen
 * @date: created in 下午6:54 2018/1/17
 */
@Data
public class GiftcardLogVO implements Serializable{
    /**
     * 关联卡单元id
     */
    private Long unitId;

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * goods_name
     * 对应支付的商品名称
     */
    private String goodsName;

    /**
     * amount
     * 变动的金额，可以正负
     */
    private BigDecimal amount;

    /**
     * balance
     * 当前卡余额
     */
    private BigDecimal balance;

    /**
     * log_type
     * 日志类型：1-消费，2-退款，3-绑定，4-冻结，5-解冻，6-续期
     */
    private Integer logType;

    /**
     * created_time
     * 创建时间
     */
    private Date createdTime;
}
