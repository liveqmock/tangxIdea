
package com.topaiebiz.goods.category.backend.entity;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * Description 商品类目属性  。
 * 
 * Author Hedda 
 *    
 * Date 2017年8月23日 下午4:59:29 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_goo_backend_category_attr")
@Data
public class BackendCategoryAttrEntity extends BaseBizEntity<Long>{

	/** 序列化版本号。 */
	@TableField(exist = false)
	private static final long serialVersionUID = 3014348620845679844L;

	/** 所属类目。*/
	private Long belongCategory;
	
	/** 是否为商家填的属性。（1为是，null为不是）*/
	private Long storeCustom;
	
    /** 类目属性名字。*/
	private String name;
	
	/**属性类型。(1.文本2.日期3.数字)。*/
	private Integer type;
	
	/** 默认单位。*/
	private String defaultUnit;
	
	/** 是否为销售属性         (1是，0不是)。*/
	private Integer isSale;
	
	/** 是否为必填项              (1是，0不是)。*/
	private Integer isMust;
	
	/**是否可以自定义   (1是，0不是)。*/
	private Integer isCustom;
	
	/**属性值集合，用逗号隔开。*/
	private String valueList;
	
	/** 排序号。*/
	private Integer sortNo;
	
	/** 备注。用于备注其他信息。 */
	private String memo;


	public void clearInit() {
		this.setVersion(null);
		this.setCreatedTime(null);
		this.setDeleteFlag(null);
	}


}
