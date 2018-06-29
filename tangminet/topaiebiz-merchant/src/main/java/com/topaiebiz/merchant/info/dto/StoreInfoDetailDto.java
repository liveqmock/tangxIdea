package com.topaiebiz.merchant.info.dto;

import java.io.Serializable;
import java.util.List;

import com.topaiebiz.merchant.info.entity.StoreInfoEntity;
import lombok.Data;

/**
 * Description： 创建店铺
 * 
 * Author Aaron.Xue
 * 
 * Date 2017年11月24日 下午9:46:31
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class StoreInfoDetailDto implements Serializable {

	/** 商家名称。 */
	private String name;

	/** 商家类型。 */
	private Integer merchantType;
	
	/**是否可以创建店铺。*/
	private Boolean isCreate;

	/** 店铺集合。 */
	private List<StoreInfoEntity> storeList;
}
