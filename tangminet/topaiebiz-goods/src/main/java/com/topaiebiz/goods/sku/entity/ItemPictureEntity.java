package com.topaiebiz.goods.sku.entity;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**  
 * Description 商品图片，存储商品的图片信息  。
 * 
 * Author Hedda 
 *    
 * Date 2017年8月23日 下午5:23:47 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_goo_item_picture")
@Data
public class ItemPictureEntity extends BaseBizEntity<Long>{

	/** 序列化版本号。 */
	@TableField(exist = false)
	private static final long serialVersionUID = 4663973015071797224L;

	/** 所属商品。*/
	private Long itemId;

	/** 图片名称。*/
	private String name;
	
	/** 图片类型（1为显示的5张主图，2 为详情图）。*/
	private Integer type;
	
	/** 是否为主图（1是 ,0否，空也为否）。*/
	private Integer isMain;
	
	/** 备注。用于备注其他信息。 */
	private String memo;

	public boolean isMainPic() {
		return isMain != null && isMain.intValue() == 1;
	}

}
