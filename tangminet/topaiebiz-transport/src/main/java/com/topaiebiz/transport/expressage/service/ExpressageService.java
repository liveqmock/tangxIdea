package com.topaiebiz.transport.expressage.service;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.transport.dto.ExpressageParamDto;
import com.topaiebiz.transport.dto.LogisticsDto;
import com.topaiebiz.transport.expressage.dto.ExpressageDto;
import com.topaiebiz.transport.expressage.dto.LogisticsCompanyDto;

import java.util.List;

/**
 * Description 快递处理业务层 
 * 
 * Author Aaron.Xue 
 *    
 * Date 2017年10月17日 下午9:09:09 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface ExpressageService {

	/**
	 * 物流公司快递列表
	 * @param logisticsCompanyDto
	 * @return
	 */
	PageInfo<LogisticsCompanyDto> getList(LogisticsCompanyDto logisticsCompanyDto);

	/**
	 * 新增快递公司
	 * @param logisticsCompanyDto
	 */
	void add(LogisticsCompanyDto logisticsCompanyDto);

	/**
	 * 修改快递公司
	 * @param logisticsCompanyDto
	 */
	void edit(LogisticsCompanyDto logisticsCompanyDto);

	//获取物流公司列表
	List<LogisticsCompanyDto> getListLogisticsCompany();

	//订阅快递
	boolean subscriptionExpressage(ExpressageParamDto expressageParamDto);

	//回调添加快递信息
	String saveExpressageInfo(String param);

	//查询快递信息
	ExpressageDto getExpressInfo(com.topaiebiz.transport.dto.ExpressageParamDto expressageParamDto);

	//根据id查询快递公司信息
	LogisticsDto getLogistics(Long id);

	//删除快递公司
	void remove(List<Long> ids);

	//启用,禁用物流商家
	void editStatus(List<Long> ids, Integer status);

	/**
	*
	* Description: 根据物流公司CODE 查询物流公司信息
	*
	* Author: hxpeng
	* createTime: 2018/3/6
	*
	* @param:
	**/
	LogisticsDto getLogisticsByCode(String expressCompanyCode);
}