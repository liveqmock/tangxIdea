package com.topaiebiz.promotion.mgmt.service;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.promotion.mgmt.dto.HomeSeckillDto;
import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.dto.PromotionGoodsDto;

import java.text.ParseException;
import java.util.List;

/**
 * Description 营销活动商品信息
 * <p>
 * <p>
 * Author Joe
 * <p>
 * Date 2017年10月13日 下午4:42:39
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface PromotionGoodsService {

    /**
     * Description 查询首页秒杀活动
     * <p>
     * Author Joe
     *
     * @return
     * @throws ParseException
     */
    HomeSeckillDto getHomePageSeckill() throws ParseException;

    /**
     * Description 根据营销活动id查询活动商品分页列表
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    PageInfo<ItemDTO> getPromotionApplicableGoods(PromotionDto promotionDto);

    /**
     * Description 商家报名商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     * @return
     */
    PageInfo<PromotionGoodsDto> getStoreEnrolGoodsList(PromotionGoodsDto promotionGoodsDto);

    /**
     * Description 报名商家商品审核列表
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     * @return
     */
    PageInfo<PromotionGoodsDto> getStoreGoodsAuditList(PromotionGoodsDto promotionGoodsDto);

    /**
     * Description 审核sku商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsId
     * @param state
     */
    void modifyAuditSingleSkuGoods(Long promotionGoodsId, Integer state);

    /**
     * Description sku商品审核完成
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     */
    void modifyAuditSkuGoods(List<PromotionGoodsDto> promotionGoodsList);

    /**
     * Description 审核item商品通过/不通过
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     */
    void modifyAuditItemGoods(PromotionGoodsDto promotionGoodsDto);

    /**
     * Description 商家商品审核完成
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     */
    void modifyAuditGoods(PromotionGoodsDto promotionGoodsDto);

    /**
     * 查询所选item商品下所选sku商品
     *
     * @param promotionId
     * @param itemId
     * @return
     */
    List<PromotionGoodsDto> getPromotionSkuGoodsList(Long promotionId, Long itemId);

    /**
     * Description 查询秒杀活动集合
     * <p>
     * Author Joe
     *
     * @param plateCode 活动板块CODE
     * @return
     */
    List<HomeSeckillDto> getSeckillList(String plateCode);

    /**
     * 清空活动商品
     *
     * @param promotionId
     * @param itemId
     * @return
     */
    void cleanPromotionGoods(Long promotionId, Long itemId);
}
