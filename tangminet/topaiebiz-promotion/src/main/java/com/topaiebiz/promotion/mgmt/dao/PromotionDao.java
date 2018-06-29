package com.topaiebiz.promotion.mgmt.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.promotion.mgmt.dto.HomeSeckillDto;
import com.topaiebiz.promotion.mgmt.dto.MemberCouponDto;
import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.entity.PromotionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description： 营销活动
 * <p>
 * <p>
 * Author Joe
 * <p>
 * Date 2017年9月22日 下午2:03:19
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Mapper
public interface PromotionDao extends BaseDao<PromotionEntity> {

    /**
     * Description：营销活动列表
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    List<PromotionDto> selectPromotionList(Page<PromotionDto> page, PromotionDto promotionDto);

    /**
     * Description 查询秒杀活动时间集合
     * <p>
     * Author Joe
     *
     * @return
     */
    List<HomeSeckillDto> selectSeckillStartTimeList();


    /**
     * Description 定时任务活动结束（时间间隔：1小时）
     * <p>
     * Author Joe
     *
     * @return
     */
    List<PromotionDto> selectPromotionEnd();

    /**
     * Description 平台报名活动列表
     * <p>
     * Author Joe
     *
     * @param page
     * @param promotionDto
     * @return
     */
    List<PromotionDto> selectPlatformEnrolPromotionList(Page<PromotionDto> page, PromotionDto promotionDto);

    /**
     * Description 发起报名获取活动列表
     * <p>
     * Author Joe
     *
     * @param page
     * @param promotionDto
     * @return
     */
    List<PromotionDto> selectEnrolPromotionList(Page<PromotionDto> page, PromotionDto promotionDto);

    /**
     * Description 商家营销活动报名列表(全部活动)
     * <p>
     * Author Joe
     *
     * @param page
     * @param promotionDto
     * @return
     */
    List<PromotionDto> selectStoreAllPromotionList(Page<PromotionDto> page, PromotionDto promotionDto);

    /**
     * Description 商家营销活动报名列表(已报名活动)
     * <p>
     * Author Joe
     *
     * @param page
     * @param promotionDto
     * @return
     */
    List<PromotionDto> selectStoreEnrolPromotionList(Page<PromotionDto> page, PromotionDto promotionDto);

    /**
     * Description 定时任务报名开始（时间间隔：1小时）
     * <p>
     * Author Joe
     *
     * @return
     */
    List<PromotionDto> selectPromotionApplyStart();

    /**
     * Description 定时任务报名结束（时间间隔：1小时）
     * <p>
     * Author Joe
     *
     * @return
     */
    List<PromotionDto> selectPromotionApplyEnd();

    /**
     * 会员优惠券
     *
     * @param page
     * @param memberCouponDto
     * @return
     */
    List<PromotionDto> selectMemberCoupon(Page<PromotionDto> page, MemberCouponDto memberCouponDto);

    /**
     * 已过期优惠券
     *
     * @param page
     * @param memberCouponDto
     * @return
     */
    List<PromotionDto> selectOverdueCoupons(Page<PromotionDto> page, MemberCouponDto memberCouponDto);

    /**
     * @param * @param null
     * @Author: tangx.w
     * @Description： 选择优惠券列表获取
     * @Date: 2018/5/10 17:33
     */
    List<PromotionDto> getPromotionCouponsList(Page<PromotionDto> page, PromotionDto promotionDto);

    /**
     * @param page,promotionDto
     * @Author: tangx.w
     * @Description: 分页查询平台优惠券活动
     * @Date: 2018/5/12 9:31
     */
    List<PromotionDto> getCouponActives(Page<PromotionDto> page, PromotionDto promotionDto);

    /**
     * @param storeId
     * @Author: tangx.w
     * @Description: 获取店铺优惠券
     * @Date: 2018/5/14 15:45
     */
    List<PromotionEntity> selectCouponsList(@Param("storeId") Long storeId);

    /**
     * @param * @param null
     * @Author: tangx.w
     * @Description: 获取已经发布的优惠券
     * @Date: 2018/5/16 15:00
     */
    List<PromotionEntity> selectReleaseList(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 更新活动中的优惠券使用数量
     *
     * @param id
     * @param number
     * @return
     */
    Integer updateUsedAmountById(@Param("id") Long id, @Param("number") Integer number);
}
