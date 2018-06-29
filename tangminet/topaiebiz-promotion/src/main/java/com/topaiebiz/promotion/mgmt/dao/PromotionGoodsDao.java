package com.topaiebiz.promotion.mgmt.dao;

import java.util.List;

import com.topaiebiz.goods.dto.sku.ItemDTO;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.promotion.mgmt.dto.PromotionGoodsDto;
import com.topaiebiz.promotion.mgmt.entity.PromotionGoodsEntity;

/**
 * 
 * Description 营销活动商品
 * 
 * 
 * Author Joe
 * 
 * Date 2017年10月9日 下午4:01:57
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface PromotionGoodsDao extends BaseDao<PromotionGoodsEntity> {

	/**
	 * 
	 * Description 根据营销活动查询所选商品(根据所属item去重)
	 * 
	 * Author Joe
	 * 
	 * @return
	 */
	List<PromotionGoodsDto>  findPromotionGoodsByPromotionId(Page<PromotionGoodsDto> page, ItemDTO itemDto);

	/**
	 * 
	 * Description 商家报名商品
	 * 
	 * Author Joe   
	 * 
	 * @param page
	 * @param promotionGoodsDto
	 * @return
	 */
	List<PromotionGoodsDto> selectStoreEnrolGoodsList(Page<PromotionGoodsDto> page, PromotionGoodsDto promotionGoodsDto);

	/**
	 * 
	 * Description 报名商家商品审核列表(分页查询)
	 * 
	 * Author Joe
	 *
	 * @param page
	 * @param promotionGoodsDto
	 * @return
	 */
	List<PromotionGoodsDto> selectStoreGoodsAuditList(Page<PromotionGoodsDto> page, PromotionGoodsDto promotionGoodsDto);

	/**
	 * C端获取活动适用商品
	 * @param promotionGoodsDto
	 * @return
	 */
    List<PromotionGoodsDto> selectPromotionApplicableGoods(PromotionGoodsDto promotionGoodsDto);
}
