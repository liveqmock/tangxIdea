package com.topaiebiz.merchant.freight.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;

/**
 * Description: 运费模板dto
 * 
 * Author : Anthony
 * 
 * Date :2017年11月2日 下午2:17:47
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerFreightTempleteDto extends PagePO {

	/** 商家入驻信息的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。 */
	private Long id;
	
	private Long storeId;

	/** 运费模板名称 */
	private String freightName;

	/** 计价方式。1 件数 2体积 3重量 */
	private Integer pricing;
}
