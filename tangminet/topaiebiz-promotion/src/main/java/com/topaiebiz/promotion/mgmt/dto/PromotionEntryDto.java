package com.topaiebiz.promotion.mgmt.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.util.Date;

/**
 * 
 * Description 营销活动商家报名表
 * 
 * 
 * Author Joe 
 *    
 * Date 2017年12月6日 下午8:42:09 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class PromotionEntryDto extends PagePO {

	/**
	 * ID
	 */
	private Long id;

	/**
	 * 营销活动ID
	 */
	private Long promotionId;

	/**
	 * 商家ID
	 */
	private Long storeId;

	/**
	 * 状态
	 */
	private Integer state;

	/**
	 * 备注
	 */
	private String memo;
	
	/**
	 * 店铺名称
	 */
	private String storeName;
	
	/**
	 * 店铺等级名称
	 */
	private String storeGradeName;
	
	/**
	 * 店铺等级id
	 */
	private Long storeGradeId;
	
	/**
	 * 报名开始时间
	 */
	private Date applyStartTime;
	
	/**
	 * 报名开始时间
	 */
	private String promotionEnrolStart;

	/**
	 * 报名结束时间
	 */
	private String promotionEnrolEnd;
	
	/**
	 * 报名结束时间
	 */
	private Date applyEndTime;
	
	/**
	 * 报名商品数量
	 */
	private Integer enrolGoodsNum;

	/**
	 * 活动类型
	 */
	private Integer typeId;
}
