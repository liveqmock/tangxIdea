package com.topaiebiz.promotion.mgmt.moble.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.promotion.mgmt.dto.coupon.PromotionCouponDTO;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import com.topaiebiz.promotion.mgmt.service.CouponActivityService;
import com.topaiebiz.promotion.mgmt.service.PromotionService;
import com.topaiebiz.promotion.mgmt.vo.coupon.ActivityCouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/coupon/customer", method = RequestMethod.POST)
public class AppCouponActivityController {

    @Autowired
    private PromotionService promotionService;
    @Autowired
    private CouponActivityService couponActivityService;

    /**
     * 平台优惠券列表（C端展示）
     *
     * @return
     */
    @MemberLogin
    @RequestMapping(path = "/getPlatformCouponList/{promotionCode}")
    public ResponseInfo getPlatformCouponList(@PathVariable("promotionCode") String promotionCode) {
        //登录用户
        Long memberId = MemberContext.tryGetMemberId();
        PromotionCouponDTO pc = couponActivityService.getActivityCouponsByCode(promotionCode, memberId);
        return new ResponseInfo(pc);
    }

    /**
     * 平台优惠券列表（C端展示）
     *
     * @param vo
     * @return
     */
    @MemberLogin
    @RequestMapping(path = "/bindActivityCoupons")
    public ResponseInfo bindActivityCoupons(@RequestBody ActivityCouponVO vo) {
        if (vo.getPromotionId() == null) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        //登录用户
        Long memberId = MemberContext.getMemberId();
        Boolean res = couponActivityService.bindActivityCoupon(vo.getPromotionId(), vo.getCouponId(), memberId);
        return new ResponseInfo(res);
    }
}
