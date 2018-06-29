package com.topaiebiz.elasticsearch.service;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.elasticsearch.dto.GoodsDto;
import com.topaiebiz.elasticsearch.dto.SearchParamsDto;

/**
 * 
 * Description： 商品搜索service 接口
 * 
 * 
 * Author hxpeng 
 *    
 * Date 2017年11月4日 下午2:58:29 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface ElasticSearchService {

	//搜索商品
	PageInfo<GoodsDto> searchItem(SearchParamsDto searchParamsDto);
}
