package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * 
 * Description： 营销活动类型表
 * 
 * 
 * Author Joe
 * 
 * Date 2017年9月22日 上午10:54:33
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_pro_promotion_type")
@Data
public class PromotionTypeEntity extends BaseBizEntity<Long> {

	private static final long serialVersionUID = -7895482038090083627L;

	/**
	 * id
	 */
	private Long id;

	/**
	 * 营销级别
	 */
	private Long gradeId;

	/**
	 * 营销类型编码
	 */
	private String typeCode;

	/**
	 * 营销类型名称
	 */
	private String name;

	/**
	 * 发起者类型
	 */
	private Long sponsorType;

	/**
	 * 备注
	 */
	private String memo;

}
