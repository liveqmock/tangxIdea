package com.topaiebiz.promotion.mgmt.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.dto.coupon.*;

import java.text.ParseException;
import java.util.List;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 13:33 2018/5/10
 * @Modified by:
 */
public interface CouponActivityService {

    /**
     * @param * @param null
     * @Author: tangx.w
     * @Description: 添加优惠券活动
     * @Date: 2018/5/10 14:23
     */
    Long addCouponActivity(PromotionDto promotionDto) throws ParseException;

    /**
     * @param promotionDto
     * @Author: tangx.w
     * @Description: 选择优惠券
     * @Date: 2018/5/10 16:51
     */
    PageInfo<PromotionDto> getCouponList(PagePO pagePO, PromotionDto promotionDto);

    /**
     * @param promotionId,promotionIds
     * @Author: tangx.w
     * @Description: 保存选择优惠券
     * @Date: 2018/5/10 16:30
     */
    void saveSelectedCoupons(Long promotionId, String promotionIds);

    /**
     * @param promotionDto
     * @Author: tangx.w
     * @Description: 获取选中的优惠券列表
     * @Date: 2018/5/12 9:18
     */
    PageInfo<PromotionDto> getSelectedCoupons(PagePO pagePO, PromotionDto promotionDto);

    /**
     * @param promotionId,couponId
     * @Author: tangx.w
     * @Description: 取消选择
     * @Date: 2018/5/11 11:03
     */
    void cancelCoupon(Long promotionId, Long couponId);

    /**
     * @param promotionId,couponIdList
     * @Author: tangx.w
     * @Description: 保存
     * @Date: 2018/5/11 15:59
     */
    void saveAcitivity(List<CouponDto> couponList, Long promotionId, Integer isRelease, Integer subType) throws ParseException;

    /**
     * @param id
     * @Author: tangx.w
     * @Description: 取消按钮-新增/复制的时候调用
     * @Date: 2018/5/12 13:46
     */
    void cancel(Long id);

    /**
     * @param id
     * @Author: tangx.w
     * @Description: 复制优惠券活动
     * @Date: 2018/5/12 14:13
     */
    Long copyCouponActive(Long id);

    /**
     * @param promotionDto
     * @Author: tangx.w
     * @Description: 停止优惠券活动
     * @Date: 2018/5/12 14:44
     */
    void stopCouponActive(PromotionDto promotionDto);

    /**
     * @param promotionDto
     * @Author: tangx.w
     * @Description: 获取优惠券活动列表
     * @Date: 2018/5/14 10:34
     */
    PageInfo<PromotionDto> getCouponActives(PromotionDto promotionDto) throws ParseException;

    /**
     * @param * @param null
     * @Author: tangx.w
     * @Description: 支付页面baner入口
     * @Date: 2018/5/16 13:53
     */
    CouponSharePropertyDto getPaymentPageBannerProperty(BannerDto bannerDto) throws ParseException;

    /**
     * @param shareCouponDto
     * @Author: tangx.w
     * @Description: 领取分享优惠券
     * @Date: 2018/5/16 14:35
     */
    ShareCouponDto getShareCoupon(ShareCouponDto shareCouponDto);

    /**
     * 获取优惠券列表
     *
     * @param promotionCode
     * @param memberId
     * @return
     */
    PromotionCouponDTO getActivityCouponsByCode(String promotionCode, Long memberId);

    /**
     * 领取优惠券
     *
     * @param promotionId 活动ID
     * @param couponId    优惠券ID
     * @param memberId    会员ID
     * @return
     */
    Boolean bindActivityCoupon(Long promotionId, Long couponId, Long memberId);
}
