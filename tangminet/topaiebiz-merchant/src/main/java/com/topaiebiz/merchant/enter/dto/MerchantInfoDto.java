package com.topaiebiz.merchant.enter.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description: 商家信息表Dto
 * 
 * Author : Anthony
 * 
 * Date :2017年9月27日 下午1:36:24
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerchantInfoDto  implements Serializable {

	/** 商家入驻信息的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。 */
	private Long id;

	/** 公司名称 */
	private String name;

	/** 连锁店、直营店等暂定 */
	@NotNull(message = "{validation.MerchantInfo.merchantType}")
	private Integer merchantType;

	/** 上级商户 */
	private Long parentMerchant;

	/** 入驻状态。1申请，2审核通过 3 审核不通过 4待付款 5已完成 */
	private Integer state;

	/** 商家联系人姓名 */
	private String contactName;

	/** 联系人手机号 */
	private String contactTele;

	/** 店铺的积分。和后期奖惩有关系 */
	private Long integral;

	/** 商家等级积分 */
	private Long gradeIntegral;

	/** 商家等级 */
	private Long merchantGradeId;
}
