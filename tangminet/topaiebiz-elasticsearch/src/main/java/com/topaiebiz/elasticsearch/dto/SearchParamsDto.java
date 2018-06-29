package com.topaiebiz.elasticsearch.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

/**
 * 参数Dto
 */
@Data
public class SearchParamsDto extends PagePO {

	private String params;

	private Long storeId;
	
}
