package com.topaiebiz.elasticsearch.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.elasticsearch.dto.GoodsDto;
import com.topaiebiz.elasticsearch.dto.SearchParamsDto;
import com.topaiebiz.elasticsearch.exception.SearchExceptionEnum;
import com.topaiebiz.elasticsearch.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description： 商品搜索service 接口实现类
 * 
 * 
 * Author hxpeng
 * 
 * Date 2017年11月4日 下午3:08:53
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Service
@Slf4j
public class ElasticSearchServiceImpl implements ElasticSearchService {

	@Value("${elasticSearch.transport_ip}")
	private String transportIp;

	@Value("${elasticSearch.transport_port}")
	private Integer transportPort;

	@Value("${elasticSearch.cluster_name}")
	private String clusterName;

	@Value("${elasticSearch.security_user}")
	private String securityUser;

	@Value("${elasticSearch.index}")
	private String index;
	//搜索客户端
	private static TransportClient client = null;

	@PostConstruct
	public void initClient(){
		try {
			client = new PreBuiltXPackTransportClient(Settings.builder()
					.put("cluster.name", clusterName)
					.put("xpack.security.user", securityUser)
					.put("client.transport.sniff", false)
					.build())
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(transportIp), transportPort));
		} catch (UnknownHostException e) {
			log.error("创建client实例失败，exception = {}",e);
		}
	}

	@Override
	public PageInfo<GoodsDto> searchItem(SearchParamsDto searchParamsDto){
		if(StringUtils.isBlank(searchParamsDto.getParams())){
			throw new GlobalException(SearchExceptionEnum.PARAM_IS_NULL);
		}
		Page<GoodsDto> page = PageDataUtil.buildPageParam(searchParamsDto);
		List<GoodsDto> goodsDtos = new ArrayList<>();
		try {
			goodsDtos = search(searchParamsDto.getParams(), searchParamsDto.getStoreId(), searchParamsDto.getPageNo(), searchParamsDto.getPageSize());
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(SearchExceptionEnum.SEARCH_FAILED);
		}
		page.setRecords(goodsDtos);
		return PageDataUtil.copyPageInfo(page);
	}

	//搜索方法
	public  List<GoodsDto> search(String params, Long storeId, Integer pageNo, Integer pageSize) throws Exception {
		log.info("搜索参数为：{},店铺ID为：{}", params, storeId);
		//参数为空
		if (StringUtils.isEmpty(params)) {
			throw new GlobalException(SearchExceptionEnum.PARAM_IS_NULL);
		}
		SearchResponse response;
		if (null != storeId) {
			response = client.prepareSearch(index)
					// 设置搜索算法/类型
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("name", params))
							.filter(QueryBuilders.termQuery("deletedflag", Constants.DeletedFlag.DELETED_NO))
							.filter(QueryBuilders.termQuery("status", Constants.Status.GROUNDING))
							.filter(QueryBuilders.termQuery("belongstore", storeId))
							.filter(QueryBuilders.termQuery("frozenflag", Constants.FrozenFlag.FROZEN_NO)))
					.setFrom(pageSize * (pageNo - 1))
					// 设置返回值大小条数，默认为15条
					.setSize(pageSize)
					.setExplain(true).get();
		} else {
			response = client.prepareSearch(index)
					// 设置搜索算法/类型
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("name", params))
							.filter(QueryBuilders.termQuery("deletedflag", Constants.DeletedFlag.DELETED_NO))
							.filter(QueryBuilders.termQuery("status", Constants.Status.GROUNDING))
							.filter(QueryBuilders.termQuery("frozenflag", Constants.FrozenFlag.FROZEN_NO)))
					// 设置搜索起始的索引,默认为0
					.setFrom(pageSize * (pageNo - 1))
					// 设置返回值大小条数，默认为15条
					.setSize(pageSize)
					.setExplain(true).get();
		}
		log.info("查寻结果response ：{}", response.toString());
		SearchHit[] hitsArray = response.getHits().getHits();
		List<GoodsDto> goodsDtos = new ArrayList<>();
		if (null != hitsArray && hitsArray.length > 0) {
			log.info("查寻结果为：｛" + hitsArray.length + "｝条");
			for (SearchHit searchHit : hitsArray) {
				Map<String, Object> source = searchHit.getSource();
				GoodsDto dto = new GoodsDto();
				dto.setId(source.get("id") == null ? null : Long.valueOf(source.get("id").toString()));
				dto.setBelongStore(source.get("belongstore") == null ? null : Long.valueOf(source.get("belongstore").toString()));
				dto.setDefaultPrice(source.get("defaultprice") == null ? null : Double.valueOf(source.get("defaultprice").toString()));
				dto.setMarketPrice(source.get("marketprice") == null ? null : Double.valueOf(source.get("marketprice").toString()));
				dto.setName(source.get("name") == null ? null : source.get("name").toString());
				dto.setPictureName(source.get("picturename") == null ? null : source.get("picturename").toString());
				dto.setSalesVolome(source.get("salesvolume") == null ? null : Long.valueOf(source.get("salesvolume").toString()));
				goodsDtos.add(dto);
			}
		}
		return goodsDtos;
	}

}
