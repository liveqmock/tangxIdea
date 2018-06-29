package com.topaiebiz.promotion.mgmt.moble.controller;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.promotion.mgmt.dto.HomeSeckillDto;
import com.topaiebiz.promotion.mgmt.dto.MemberCouponDto;
import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.dto.coupon.BannerDto;
import com.topaiebiz.promotion.mgmt.dto.coupon.CouponSharePropertyDto;
import com.topaiebiz.promotion.mgmt.dto.coupon.ShareCouponDto;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import com.topaiebiz.promotion.mgmt.service.CouponActivityService;
import com.topaiebiz.promotion.mgmt.service.PromotionGoodsService;
import com.topaiebiz.promotion.mgmt.service.PromotionService;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Description C端营销活动
 * <p>
 * <p>
 * Author Joe
 * <p>
 * Date 2017年10月29日 下午5:14:58
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping(path = "/promotion/customer", method = RequestMethod.POST)
public class AppPromotionController {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private PromotionGoodsService promotionGoodsService;

    @Autowired
    private CouponActivityService couponActivityService;

    /**
     * Description 查询首页秒杀活动
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     * @throws ParseException
     */
    @RequestMapping(path = "/getHomePageSeckill")
    public ResponseInfo getHomePageSeckillList() throws GlobalException, ParseException {
        HomeSeckillDto homePageSeckill = promotionGoodsService.getHomePageSeckill();
        return new ResponseInfo(homePageSeckill);
    }

    /**
     * Description 查询秒杀活动集合
     * <p>
     * Author Joe
     *
     * @return
     */
    @RequestMapping(path = "/getSeckillList")
    public ResponseInfo getSeckillList() {
        // 查询活动数据
        List<HomeSeckillDto> seckillStartTimeList = promotionGoodsService.getSeckillList(null);
        return new ResponseInfo(seckillStartTimeList);
    }

    /**
     * Description 根据营销活动id查询活动商品分页列表
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    @MemberLogin
    @RequestMapping(path = "/getPromotionApplicableGoods")
    public ResponseInfo getPromotionApplicableGoods(@RequestBody PromotionDto promotionDto) {
        if (promotionDto.getId() == null) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        PageInfo<ItemDTO> promotionApplicableGoods = promotionGoodsService.getPromotionApplicableGoods(promotionDto);
        return new ResponseInfo(promotionApplicableGoods);
    }

    /**
     * C端获取店铺优惠券
     *
     * @param storeId
     * @return
     */
    @RequestMapping(path = "/getStoreCoupons/{storeId}")
    public ResponseInfo getStoreCoupons(@PathVariable Long storeId) throws ParseException {
        // 判断店铺id是否为空
        if (storeId == null) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTIONGOODS_STORE_OWNED_NOT_NULL);
        }
        List<PromotionDto> promotionCoupons = promotionService.getStoreCoupons(storeId);
        return new ResponseInfo(promotionCoupons);
    }

    /**
     * C端批量获取店铺优惠券
     *
     * @return
     */
    @RequestMapping(path = "/getStoreCouponsList")
    public ResponseInfo getStoreCouponsList(@RequestBody List<Long> storeIds) {
        if (CollectionUtils.isEmpty(storeIds)) {
            return new ResponseInfo();
        }
        Map<Long, List<PromotionDto>> map = promotionService.getStoreCouponsList(storeIds);
        return new ResponseInfo(map);
    }

    /**
     * 领取优惠券
     *
     * @param promotionId
     * @return
     */
    @MemberLogin
    @RequestMapping(path = "/getCoupon/{promotionId}")
    public ResponseInfo getCoupon(@PathVariable Long promotionId) {
        // 登录会员
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        if (promotionId == null) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        promotionService.getCoupon(memberId, promotionId);
        return new ResponseInfo();
    }

    /**
     * 查看会员优惠券
     *
     * @param memberCouponDto
     * @return
     */
    @MemberLogin
    @RequestMapping(path = "/getMemberCoupon")
    public ResponseInfo getMemberCoupon(@RequestBody MemberCouponDto memberCouponDto) {
        PageInfo<PromotionDto> promotionDtoList = null;
        // 登录会员
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        memberCouponDto.setMemberId(memberId);
        if (memberCouponDto.getUsageState() == null) {
            return new ResponseInfo();
        }
        if (memberCouponDto.getUsageState() == 2) {
            // 已过期优惠券
            promotionDtoList = promotionService.getOverdueCoupons(memberCouponDto);
        } else {
            // 未使用/已使用优惠券
            promotionDtoList = promotionService.getMemberCoupon(memberCouponDto);
        }
        return new ResponseInfo(promotionDtoList);
    }

    /**
     * 判断是否是新用户(已支付)
     * @return
     */
    @MemberLogin
    @RequestMapping(path = "/isNewUser")
    public ResponseInfo isNewUser() {
        // 登录会员
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        //判断是否是新用户
        Boolean res = promotionService.isNewUser(memberId);
        return new ResponseInfo(res);
    }

    /**
     *@Author: tangx.w
     *@Description: 支付页面baner入口
     *@param
     *@Date: 2018/5/16 13:32
     */
    @MemberLogin
    @RequestMapping(path = "/getPaymentPageBannerProperty")
    public ResponseInfo getPaymentPageBannerProperty(@RequestBody BannerDto bannerDto) throws ParseException {
        return new ResponseInfo(couponActivityService.getPaymentPageBannerProperty(bannerDto));
    }

    /**
     *@Author: tangx.w
     *@Description: 领取分享优惠券
     *@param
     *@Date: 2018/5/16 13:32
     */
    @MemberLogin
    @RequestMapping(path = "/getShareCoupon")
    public ResponseInfo getShareCoupon(@RequestBody ShareCouponDto shareCouponDto) {
        return new ResponseInfo(couponActivityService.getShareCoupon(shareCouponDto));
    }

}
