package com.topaiebiz.transport.expressage.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.system.annotation.NotLoginPermit;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.transport.dto.ExpressageParamDto;
import com.topaiebiz.transport.expressage.service.ExpressageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description 处理快递100的处理器 
 * 
 * Author Aaron.Xue 
 *    
 * Date 2017年10月17日 下午8:56:51 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping(value = "/transport/expressage", method = RequestMethod.POST)
public class ExpressageController {
	
	@Autowired
	private ExpressageService expressageService;

	//查询快递
	@RequestMapping(path = "/getExpressageInfo")
	@NotLoginPermit
	public ResponseInfo getExpressageInfo(@RequestBody ExpressageParamDto expressageParamDto) {
		return new ResponseInfo(expressageService.getExpressInfo(expressageParamDto));
	}

	/**
	 *
	 * @param param 回调接口（添加快递信息）
	 * @return
	 */
	@RequestMapping(path = "/addExpressageInfo")
    @NotLoginPermit
	public String addExpressageInfo(String param) {
		return expressageService.saveExpressageInfo(param);
	}
	
	/**
	 * Description 获取所有快递公司信息
	 * 
	 * Author Aaron.Xue   
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/getLogistics")
	@NotLoginPermit
	public ResponseInfo getLogistics() {
		return new ResponseInfo(expressageService.getListLogisticsCompany());
	}
	
	
}
