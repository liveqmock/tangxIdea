package com.topaiebiz.goods.comment.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.Version;
import com.nebulapaas.data.mybatis.common.IdEntity;
import lombok.Data;

/**
 * Description 商品评价图片表，存储评价的图片。
 * 
 * Author Hedda
 * 
 * Date 2017年8月23日 下午5:25:18
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_goo_goods_sku_comment_picture")
@Data
public class GoodsSkuCommentPictureEntity extends IdEntity<Long>{

	/** 序列化版本号。 */
	@TableField(exist = false)
	private static final long serialVersionUID = 3571596724828276618L;

	/** 评价ID。 */
	private Long commentId;

	/** 图片描述。 */
	private String description;

	/** 评价上传的图片。 */
	private String image;
	
	/** 备注。用于备注其他信息。 */
	private String memo;
	
	/** 创建人编号。取值为创建人的全局唯一主键标识符。 */
	private Long creatorId;

	/** 创建时间。默认取值为系统的当前时间。 */
	private Date createdTime = new Date();
	
	/** 逻辑删除标识。仅且仅有0和1两个值，1表示已经被逻辑删除，0表示正常可用，默认为0。 */
	private Byte deletedFlag = 0;

	/** 版本号。信息的版本号。乐观锁机制的辅助字段，用于控制信息的一致性。默认取值为1，执行更新操作时，自动加1。 */
	@Version
	private Long version = 1L;

}
