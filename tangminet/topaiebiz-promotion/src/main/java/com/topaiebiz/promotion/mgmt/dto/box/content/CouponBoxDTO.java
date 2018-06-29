package com.topaiebiz.promotion.mgmt.dto.box.content;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券宝箱
 */
@Data
public class CouponBoxDTO {
    /**
     * 优惠券ID
     */
    private Long couponId;
    /**
     * 所属店铺ID，平台该值为0
     */
    private Long storeId;
    /**
     * 店铺名称
     */
    private String storeName;
    /**
     * 条件类型（1.满   2.每满）
     */
    private Integer condType;
    /**
     * 条件值。具体多少钱
     */
    private BigDecimal condValue;
    /**
     * 优惠金额
     */
    private BigDecimal discountValue;
    /**
     * 有效期起始时间
     */
    private Date startTime;
    /**
     * 有效期结束时间
     */
    private Date endTime;
}
