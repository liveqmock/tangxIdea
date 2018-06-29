package com.topaiebiz.merchant.freight.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Description: 添加运费模板所需字段
 * 
 * Author : Anthony
 * 
 * Date :2017年11月2日 下午4:21:13
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class AddFreightTempleteDto implements Serializable {
	
	private Long id;

	/** 店铺ID */
	private Long storeId;

	/** 模板名称 */
	@NotNull(message = "{validation.AddFreightTemplete.freightName}")
	private String freightName;

	/** 计价方式。1 件数 2体积 3重量 */
	@NotNull(message = "{validation.AddFreightTemplete.pricing}")
	private Integer pricing;

	/** 是否仅配送特定地区。（1 为是 ，0为否） */
	private Integer onlyThis;
	
	/**运费模板详情*/
	private List<FreightTempleteDetailDto> freightTempleteDetails;
}
