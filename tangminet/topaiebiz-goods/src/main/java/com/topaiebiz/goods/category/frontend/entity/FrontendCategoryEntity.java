
package com.topaiebiz.goods.category.frontend.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * Description 前台类目 。
 * 
 * Author Hedda 
 *    
 * Date 2017年8月23日 下午5:08:21 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_goo_frontend_category")
@Data
public class FrontendCategoryEntity extends BaseBizEntity<Long>{

	/** 序列化版本号。 */
	@TableField(exist = false)
	private static final long serialVersionUID = -4713213711744452207L;

	/** 所属店铺。*/
	private Long belongStore;
	
	/** 类目名称。*/
	private String name;
	
	/** 类目描述。*/
	private String description;
	
	/** 类目等级。*/
	private Integer level;
	
	/** 类目排序号。*/
	private Integer sortNo;
	
	/** 父类目。*/
	private Long parentId;
	
	/** 类目图片。*/
	private String image;
	
	/** 备注。用于备注其他信息。 */
	private String memo;

    public void clearInit() {
		this.setVersion(null);
		this.setCreatedTime(null);
		this.setDeleteFlag(null);
    }
}
