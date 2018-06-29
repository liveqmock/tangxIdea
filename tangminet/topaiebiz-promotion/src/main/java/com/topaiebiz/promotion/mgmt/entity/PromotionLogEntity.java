package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

@TableName("t_pro_promotion_log")
@Data
public class PromotionLogEntity extends BaseBizEntity<Long> {
	/**
	 * id
	 * 主键，全局唯一标识符
	 */
	private Long id;

	/**
	 * promotionId
	 * 优惠券id。
	 */
	private Long promotionId;

	/**
	 * createdTime
	 * 创建时间。取值为系统的当前时间。
	 */
	private Date createdTime;

	/**
	 * operationType
	 * 操作类型 0-新增 1-发布 2-编辑 3-停止 4-结束
	 */
	private Integer operationType;

	/**
	 * operationUser
	 * 操作用户名称
	 */
	private String operationUser;

	/**
	 * memo
	 * 备注
	 */
	private String memo;

	/**
	 * creatorId
	 * 创建人编号。取值为创建人的全局唯一主键标识符。
	 */
	private Long creatorId;

	/**
	 * lastModifierId
	 * 最后修改人编号。取值为最后修改人的全局唯一主键标识符。
	 */
	private Long lastModifierId;

	/**
	 * lastModifiedTime
	 * 最后修改时间。取值为系统的当前时间。
	 */
	private Date lastModifiedTime;

	/**
	 * version
	 * 信息版本号。乐观锁机制的辅助字段，用于控制信息的一致性。
	 */
	private Long version;


}