package com.topaiebiz.goods.category.frontend.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.category.frontend.dto.FrontendCategoryDto;
import com.topaiebiz.goods.category.frontend.service.FrontendCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description 商品前台类目控制层
 * 
 * Author Hedda
 * 
 * Date 2017年8月25日 下午3:13:52
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@RestController
@RequestMapping(value = "/goods/frontendCategory",method = RequestMethod.POST)
public class FrontendCategoryMobileController {

	@Autowired
	private FrontendCategoryService frontendCategoryService;
	
	/**
	 * Description app端商家前台类目一,二，三级类目列表
	 * 
	 * Author Hedda
	 * 
	 * @param frontendCategoryDto
	 *            商品前台类目dto
	 * @return
	 * @throws GlobalException
	 */
	@RequestMapping(path = "/getMerchantAppFrontendCategoryList")
	public ResponseInfo getMerchantAppFrontendCategoryList(@RequestBody FrontendCategoryDto frontendCategoryDto) throws GlobalException {
		List<FrontendCategoryDto> listFrontendCategoryEntity = frontendCategoryService
				.getMerchantAppFrontendCategoryList(frontendCategoryDto);
		return new ResponseInfo(listFrontendCategoryEntity);
	}
	
}
