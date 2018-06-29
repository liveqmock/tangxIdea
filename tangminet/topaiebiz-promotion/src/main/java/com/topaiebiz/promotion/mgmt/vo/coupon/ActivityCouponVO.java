package com.topaiebiz.promotion.mgmt.vo.coupon;

import lombok.Data;

import java.io.Serializable;

/**
 * 领取活动优惠券请求参数
 */
@Data
public class ActivityCouponVO implements Serializable {
    private static final long serialVersionUID = 5110569839277896941L;
    /**
     * 活动ID
     */
    private Long promotionId;

    /**
     * 优惠券ID集合
     */
    private Long couponId;
}
