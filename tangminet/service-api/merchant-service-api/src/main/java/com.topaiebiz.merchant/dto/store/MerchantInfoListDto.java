package com.topaiebiz.merchant.dto.store;

import lombok.Data;

import java.io.Serializable;

/**
 * Description: 商家管理--商家信息列表分页检索dto类
 * 
 * Author : Anthony
 * 
 * Date :2017年10月28日 下午8:46:30
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerchantInfoListDto  implements Serializable {

	/** 全局唯一标识符 */
	private Long id;

	/** 连锁店、直营店等暂定 */
	private Integer merchantType;

	/** 商家联系人姓名 */
	private String contactName;

	/** 公司名称 */
	private String name;

	/** 公司所在地 */
	private String address;

	/** 联系人手机号 */
	private String contactTele;

	/** 所需积分下限，达到该值就是该等级 */
	private Long integralValue;

	/** 商家等级名称 */
	private String gradeName;
}
