package com.topaiebiz.promotion.mgmt.dto.coupon;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 领取优惠券列表
 */
@Data
public class CouponReceiveDTO implements Comparable<CouponReceiveDTO> {
    private static final long serialVersionUID = -6765417622937311715L;

    /**
     * 优惠券ID
     */
    private Long couponId;
    /**
     * 优惠券名称
     */
    private String name;
    /**
     * 优惠券描述
     */
    private String description;
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
    /**
     * 领取状态 0-未领取 1-已领取 2-已领完
     */
    private Integer received;

    @Override
    public int compareTo(CouponReceiveDTO o) {
        if (o.condValue != null) {
            if (this.condValue.compareTo(o.condValue) > 0) {
                return 1;
            } else if (this.condValue.compareTo(o.condValue) < 0) {
                return -1;
            }
        }
        return 0;
    }
}
