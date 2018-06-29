package com.topaiebiz.merchant.freight.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 运费模板类
 * 
 * Author : Anthony
 * 
 * Date :2017年10月13日 上午10:58:44
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class FreightTempleteDto implements Serializable {

	/** 全局主键id */
	private Long id;

	/** 店铺ID */
	private Long storeId;

	/** 运费名称 */
	private String freightName;

	/** 计价方式。1 件数 2体积 3重量 */
	private Integer pricing;

	/** 是否仅配送特定地区。（1 为是 ，0为否） */
	private Integer onlyThis;

	/**运费模版详情*/
	private List<FreightTempleteDetailDto> freightTempleteDetailList;


}
