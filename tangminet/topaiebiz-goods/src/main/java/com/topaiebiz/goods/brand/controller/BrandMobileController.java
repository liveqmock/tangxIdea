package com.topaiebiz.goods.brand.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.brand.dto.BrandDto;
import com.topaiebiz.goods.brand.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description app端商品品牌控制层
 * 
 * Author Hedda
 * 
 * Date 2017年8月23日 下午4:14:40
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@RestController
@RequestMapping(value = "/goods/brand",method = RequestMethod.POST)
public class BrandMobileController {

	@Autowired
	private BrandService brandService;

	
	/**
	 * Description app端商品品牌列表
	 * 
	 * Author Hedda
	 * 
	 * @return ResponseInfo
	 * @throws GlobalException
	 */
	@RequestMapping(path = "/getAppBrandList")
	public ResponseInfo getAppBrandList() throws GlobalException {
		List<BrandDto> brandList = brandService.getAppBrandList();
		return new ResponseInfo(brandList);
	}

}
