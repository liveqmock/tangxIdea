
package com.topaiebiz.goods.spu.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * Description 商品SPU图片。
 * 
 * Author Hedda
 * 
 * Date 2017年8月23日 下午5:16:07
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_goo_goods_spu_picture")
@Data
public class GoodsSpuPictureEntity extends BaseBizEntity<Long>{

	/** 序列化版本号。 */
	@TableField(exist = false)
	private static final long serialVersionUID = -5891824638911510501L;

	/** 商品SPU。 */
	private Long spuId;

	/** 图片名称。 */
	private String name;

	/** 图片类型。 */
	private Integer type;

	/** 是否为主图。 */
	private Integer isMain;

	/** 描述。 */
	private String description;
	
	/** 备注。用于备注其他信息。 */
	private String memo;
	

}
