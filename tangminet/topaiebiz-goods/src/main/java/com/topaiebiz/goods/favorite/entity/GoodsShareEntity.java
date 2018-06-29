package com.topaiebiz.goods.favorite.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.Version;
import com.nebulapaas.data.mybatis.common.IdEntity;
import lombok.Data;

import java.util.Date;

/**
 * Description： 商品分享实体类
 *
 * Author Hedda
 *    
 * Date 2017年9月25日 下午2:01:28 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_goo_goods_share")
@Data
public class GoodsShareEntity extends IdEntity<Long> {

	/*** 版本化序列号*/
	private static final long serialVersionUID = 4666662074456430170L;
	
	/** 会员id*/
	private Long memberId;
	
	/** 商品id*/
	private Long goodsId;

	/** 备注。*/
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
