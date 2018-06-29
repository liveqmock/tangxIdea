package com.topaiebiz.promotion.mgmt.dto.coupon;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PromotionCouponDTO implements Serializable {
    private static final long serialVersionUID = 1628064066639360996L;

    private Long promotionId;

    private List<CouponReceiveDTO> coupons;
}
