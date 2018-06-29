package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * 
 * Description： 营销级别表
 * 
 * 
 * Author Joe
 * 
 * Date 2017年9月22日 上午10:37:16
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_pro_promotion_grade")
@Data
public class PromotionGradeEntity extends BaseBizEntity<Long> {

	private static final long serialVersionUID = -8191679694115507762L;

	/**
	 * id
	 */
	private Long id;

	/**
	 * 级别编码
	 */
	private String promotionCode;

	/**
	 * 级别名称
	 */
	private String name;

	/**
	 * 描述
	 */
	private String description;

}
