package com.topaiebiz.goods.category.backend.dto;

import lombok.Data;

import java.util.List;


/**
 * Description 商品后台类目集合dto 。 
 * 
 * Author Hedda 
 *    
 * Date 2017年9月24日 下午3:10:58 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
@Data
public class BackendCategorysDto {

    /** 绑定前后台类目id。*/
    private Long frontBackId;
	
	/** 商品后台类目集合。*/
    private List<BackendCategoryDto> backendCategoryDto;

}
