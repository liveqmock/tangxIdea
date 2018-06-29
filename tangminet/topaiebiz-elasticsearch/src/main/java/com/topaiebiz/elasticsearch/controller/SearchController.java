package com.topaiebiz.elasticsearch.controller;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.elasticsearch.dto.GoodsDto;
import com.topaiebiz.elasticsearch.dto.SearchParamsDto;
import com.topaiebiz.elasticsearch.service.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 搜索处理器
 */
@RestController
@RequestMapping(value = "/search", method = RequestMethod.POST)
public class SearchController {

	@Autowired
	private ElasticSearchService elasticSearchService;


	@RequestMapping(path = "/item")
	public ResponseInfo item(@RequestBody SearchParamsDto searchParamsDto) {
		PageInfo<GoodsDto> goodsDtoPageInfo = elasticSearchService.searchItem(searchParamsDto);
		return new ResponseInfo(goodsDtoPageInfo);
	}

}
