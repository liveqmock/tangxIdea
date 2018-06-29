package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * 
 * Description 营销活动商家报名表
 * 
 * 
 * Author Joe
 * 
 * Date 2017年9月28日 上午9:42:52
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_pro_promotion_entry")
@Data
public class PromotionEntryEntity extends BaseBizEntity<Long> {

	/** 序列化版本号。 */
	@TableField(exist = false)
	private static final long serialVersionUID = 4308878044098120697L;

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

	public void clearInit() {
		this.setCreatedTime(null);
		this.setVersion((Long) null);
	}

}
