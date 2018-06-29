
package com.topaiebiz.goods.spu.entity;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Description 商品SPU信息。
 * 
 * Author Hedda
 * 
 * Date 2017年8月23日 下午5:12:53
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_goo_goods_spu")
@Data
public class GoodsSpuEntity extends BaseBizEntity<Long>{

	/** 序列化版本号。 */
	@TableField(exist = false)
	private static final long serialVersionUID = 8556927217184966082L;

	/** 唯一编码 (本字段是从业务角度考虑的，相当于全局的唯一业务主键)。 */
	private String spuCode;

	/** 商品名称(标题显示的名称)。 */
	private String name;

	/** 商品描述。 */
	private String description;
	
	/** 市场价。*/
	private BigDecimal marketPrice;

	/** 默认价格（页面刚打开的价格）。 */
	private BigDecimal defaultPrice;

	/** 所属店铺。 */
	private Long belongStore;

	/** 所属品牌。 */
	private Long belongBrand;
	
	/** 适用年龄段。*/
	private Long ageId;

	/** 所属类目。 */
	private Long belongCategory;
	
	/** 图片所属类目属性。*/
	private Long imageField;
	
	/** 备注。用于备注其他信息。 */
	private String memo;

}
