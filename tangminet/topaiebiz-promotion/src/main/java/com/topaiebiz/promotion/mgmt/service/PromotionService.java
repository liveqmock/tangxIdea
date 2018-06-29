package com.topaiebiz.promotion.mgmt.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.promotion.mgmt.dto.*;
import com.topaiebiz.promotion.mgmt.dto.coupon.CouponReceiveDTO;
import com.topaiebiz.promotion.mgmt.dto.coupon.SkuStateDto;
import com.topaiebiz.promotion.mgmt.entity.PromotionGoodsEntity;
import com.topaiebiz.promotion.mgmt.entity.PromotionLogEntity;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Description： 营销活动
 * <p>
 * <p>
 * Author Joe
 * <p>
 * Date 2017年9月22日 下午1:56:09
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface PromotionService {

    /**
     * Description：创建平台优惠券营销活动
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     * @throws GlobalException
     */
    Long savePromotionCoupon(PromotionDto promotionDto) throws ParseException;

    /**
     * Description：删除营销活动
     * <p>
     * Author Joe
     *
     * @param ids
     * @return
     */
    Integer removePromotion(String ids);

    /**
     * Description： 修改平台优惠券
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     * @throws GlobalException
     */
    Long modifyPromotionCoupon(PromotionDto promotionDto) throws ParseException;

    /**
     * Description：查询全部活动
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    PageInfo<PromotionDto> getPromotionList(PromotionDto promotionDto);

    /**
     * Description 添加平台优惠码
     * <p>
     * Author Joe
     *
     * @param promotion
     * @return
     * @throws GlobalException
     */
    Long savePromotionCouponCode(PromotionDto promotion) throws ParseException;

    /**
     * Description 停止平台优惠活动
     * <p>
     * Author Joe
     *
     * @param id
     * @return
     */
    Integer modifyStopPromotion(Long id);

    /**
     * Description 添加秒杀活动
     * <p>
     * Author Joe
     *
     * @param promotion
     * @return
     */
    Long savePromotionSeckill(PromotionSingleDto promotion) throws ParseException;

    /**
     * Description 添加秒杀活动商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDtoList
     * @return
     * @throws GlobalException
     */
    void modifyPromotionSeckillGoods(List<PromotionGoodsDto> promotionGoodsDtoList) throws GlobalException;

    /**
     * Description 保存/发布秒杀活动
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    void savePromotionSeckillGoods(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException;

    /**
     * Description 修改营销活动回显
     * <p>
     * Author Joe
     *
     * @return
     */
    PromotionDto findPromotionById(Long id);

    /**
     * Description 发布活动
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    void releasePromotion(Long id, Integer typeId);

    /**
     * Description 添加单品折扣
     * <p>
     * Author Joe
     *
     * @param promotion
     * @return
     */
    Long addPromotionSingle(PromotionSingleDto promotion) throws ParseException;

    /**
     * Description 修改单品级活动(单品折扣,一口价,秒杀)
     * <p>
     * Author Joe
     *
     * @param promotionSingleDto
     */
    Long modifyPromotionSingle(PromotionSingleDto promotionSingleDto) throws ParseException;

    /**
     * Description 修改单品折扣商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    void modifyPromotionSingleGoods(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException;

    /**
     * Description 保存/发布单品折扣活动
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    void savePromotionSingle(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException;

    /**
     * Description 添加一口价
     * <p>
     * Author Joe
     *
     * @param promotion
     * @return
     */
    Long savePromotionPrice(PromotionSingleDto promotion) throws ParseException;

    /**
     * Description 添加/修改一口价商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    void modifyPromotionPriceGoods(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException;

    /**
     * Description 发布/保存一口价活动
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    void savePromotionPriceGoods(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException;

    /**
     * Description 添加满减活动
     * <p>
     * Author Joe
     *
     * @param promotion
     * @return
     */
    Long savePromotionReducePrice(PromotionDto promotion) throws ParseException;

    /**
     * Description 修改满减活动
     * <p>
     * Author Joe
     *
     * @param promotionDto
     */
    Long modifyPromotionReducePrice(PromotionDto promotionDto) throws ParseException;

    /**
     * Description 添加/修改活动商品(满减,包邮,店铺优惠券,平台优惠券,平台优惠码)
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    void modifyPromotionGoods(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException;

    /**
     * Description 修改平台优惠码活动
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    Long modifyPromotionCouponCode(PromotionDto promotionDto) throws GlobalException, ParseException;

    /**
     * Description 添加店铺优惠券
     * <p>
     * Author Joe
     *
     * @param promotion
     */
    Long savePromotionStoreCoupon(PromotionDto promotion) throws ParseException;

    /**
     * Description 修改店铺优惠券
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    Long modifyPromotionStoreCoupon(PromotionDto promotionDto) throws ParseException;

    /**
     * Description 添加包邮活动
     * <p>
     * Author Joe
     *
     * @param promotion
     * @throws GlobalException
     */
    Long savePromotionFreeShipping(PromotionSingleDto promotion) throws ParseException;

    /**
     * Description 修改包邮活动
     * <p>
     * Author Joe
     *
     * @throws GlobalException
     */
    Long modifyPromotionFreeShipping(PromotionSingleDto promotionSingleDto) throws ParseException;

    /**
     * Description 活动选择保存商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     */
    void savePromotionGoods(List<PromotionGoodsDto> promotionGoodsList);

    /**
     * Description 活动选择保存商品
     * <p>
     * Author Joe
     *
     * @param itemIds,promotionId
     */
    void saveGoods(String itemIds, Long promotionId);

    /**
     * Description 获取营销活动所选商品
     * <p>
     * Author Joe
     *
     * @return
     */
    PageInfo<PromotionGoodsDto> getPromotionGoods(ItemDTO itemDTO);

    /**
     * Description 发布/保存活动(满减,包邮,店铺优惠券,平台优惠券)
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    void savePromotion(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException;

    /**
     * Description 保存/发布平台优惠码活动
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    void savePromotionCouponCodeGoods(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException;

    /**
     * Description 取消活动商品
     * <p>
     * Author Joe
     *
     * @param promotionId
     * @param itemId
     */
    void removeGoods(Long promotionId, Long itemId);


    /**
     * Description 定时任务：活动统计
     * <p>
     * Author Joe
     */
    void promotionStatistical();

    /**
     * Description 定时任务：活动报名
     * <p>
     * Author Joe
     */
    void promotionApply();

    /**
     * Description 活动商品验证
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     * @throws GlobalException
     */
    void PromotionGoodsException(PromotionGoodsDto promotionGoodsDto) throws GlobalException;

    /**
     * Description 平台报名活动列表
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     * @throws ParseException
     */
    PageInfo<PromotionDto> getPlatformEnrolPromotionList(PromotionDto promotionDto) throws ParseException;

    /**
     * Description 发起报名
     * <p>
     * Author Joe
     *
     * @param promotionEnrolDto
     * @throws ParseException
     */
    void modifyInitiateEnrol(PromotionEnrolDto promotionEnrolDto) throws ParseException;

    /**
     * Description 发起报名获取活动列表
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    PageInfo<PromotionDto> getEnrolPromotionList(PromotionDto promotionDto);

    /**
     * Description 营销活动报名商家列表
     * <p>
     * Author Joe
     *
     * @param promotionEntryDto
     * @return
     * @throws ParseException
     */
    PageInfo<PromotionEntryDto> getPromotionEnrolStoreList(PromotionEntryDto promotionEntryDto) throws ParseException;

    /**
     * Description 报名活动商家不通过
     * <p>
     * Author Joe
     *
     * @param id
     */
    void modifyStoreAuditNonconformity(Long id);

    /**
     * Description 查看店铺报名详情
     * <p>
     * Author Joe
     *
     * @param id
     * @return
     */
    PromotionEntryDto getPromotionEnrolStore(Long id);

    /**
     * Description 商家营销活动报名列表(全部活动)
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    PageInfo<PromotionDto> selectStoreAllPromotionList(PromotionDto promotionDto) throws ParseException;

    /**
     * Description 商家营销活动报名列表(已报名活动)
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    PageInfo<PromotionDto> getStoreEnrolPromotionList(PromotionDto promotionDto) throws ParseException;

    /**
     * C端获取店铺优惠券
     *
     * @param storeId
     * @return
     */
    List<PromotionDto> getStoreCoupons(Long storeId) throws ParseException;

    /**
     * C端领取优惠券
     *
     * @param memberId
     * @param promotionId
     */
    void getCoupon(Long memberId, Long promotionId);

    /**
     * 会员优惠券
     *
     * @param memberCouponDto
     * @return
     */
    PageInfo<PromotionDto> getMemberCoupon(MemberCouponDto memberCouponDto);

    /**
     * 已过期优惠券
     *
     * @param memberCouponDto
     * @return
     */
    PageInfo<PromotionDto> getOverdueCoupons(MemberCouponDto memberCouponDto);


    /**
     * C端批量获取店铺优惠券
     *
     * @param storeIds
     * @return
     */
    Map<Long, List<PromotionDto>> getStoreCouponsList(List<Long> storeIds);

    /**
     * 检验新用户
     *
     * @param memberId
     * @return
     */
    Boolean isNewUser(Long memberId);

    /**
     * @param id
     * @Author: tangx.w
     * @Description: 商家端商品优惠券保存/发布
     * @Date: 2018/4/27 9:45
     */
    void editPromotionStatus(Long id);

    /**
     * 获取优惠券列表
     *
     * @param couponIds
     * @param memberId
     * @return
     */
    List<CouponReceiveDTO> getCouponListByCode(List<Long> couponIds, Long memberId);

    /**
     * @param id
     * @Author: tangx.w
     * @Description: 复制优惠券
     * @Date: 2018/5/2 10:14
     */
    Long copyOfCoupons(Long id);

    /**
     * @param promotionId,memo
     * @Author: tangx.w
     * @Description: 停止商家优惠券
     * @Date: 2018/5/2 15:53
     */
    void stopCoupon(Long promotionId, String memo);

    /**
     * @param promotionId
     * @Author: tangx.w
     * @Description: 查看优惠券操作日志
     * @Date: 2018/5/2 19:47
     */
    List<PromotionLogEntity> getPromotionLogLists(Long promotionId);

    /**
     * @param skuIds,promotionId
     * @Author: tangx.w
     * @Description: 获取圈中商品item的sku
     * @Date: 2018/5/3 14:32
     */
    List<PromotionGoodsEntity> getItemSkuList(String skuIds, Long promotionId);

    /**
     * @param skuId,promotionId
     * @Author: tangx.w
     * @Description: 更改sku选择状态
     * @Date: 2018/5/3 14:32
     */
    void changeCouponState(Long skuId, Long promotionId);

    /**
     * @Author: tangx.w
     * @Description: 获取sku状态
     * *@param  skuIds
     * @Date: 2018/5/4 16:24
     */
    List<SkuStateDto> getSkuState(String skuIds, Long promotionId);


    /**
     * @param id
     * @Author: tangx.w
     * @Description: 发布中的商家优惠券，修改完商品之后的保存
     * @Date: 2018/5/7 19:25
     */
    void save(Long id);

    /**
     * @param promotionDto
     * @Author: tangx.w
     * @Description: 获取优惠券列表 商家端typeId=7  平台端typeId=11
     * @Date: 2018/5/7 19:25
     */
    PageInfo<PromotionDto> getCouponsList(PromotionDto promotionDto) throws ParseException;

}
