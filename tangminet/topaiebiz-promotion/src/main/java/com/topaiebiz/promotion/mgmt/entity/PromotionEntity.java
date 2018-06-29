package com.topaiebiz.promotion.mgmt.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * 
 * Description：营销活动信息表
 * 
 * 
 * Author Joe
 * 
 * Date 2017年9月22日 上午10:29:56
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@TableName("t_pro_promotion")
public class PromotionEntity extends BaseBizEntity<Long> {

	/** 序列化版本号。 */
	@TableField(exist = false)
	private static final long serialVersionUID = -7501041752804896436L;

	private Long id;
	/**
	 * 活动发起者
	 */
	private Long sponsorType;

	/**
	 * 营销级别
	 */
	private Integer gradeId;

	/**
	 * 营销类型
	 */
	private Integer typeId;

	/**
	 * 活动名称
	 */
	private String name;

	/**
	 * 显示标题
	 */
	private String showTitle;

	/**
	 * 显示类型
	 */
	private Integer showType;

	/**
	 * 活动开始时间
	 */
	private Date startTime;

	/**
	 * 活动结束时间
	 */
	private Date endTime;

	/**
	 * 活动说明
	 */
	private String description;

	/**
	 * 是否指定商品可用
	 */
	private Integer isGoodsArea;

	/**
	 * 限制会员类型
	 */
	private Long memberTypeId;

	/**
	 * 限制会员级别
	 */
	private Long memberGradeId;

	/**
	 * 条件类型
	 */
	private Integer condType;

	/**
	 * 条件值
	 */
	private BigDecimal condValue;

	/**
	 * 优惠类型
	 */
	private Integer discountType;

	/**
	 * 优惠值
	 */
	private BigDecimal discountValue;

	/**
	 * 发放数量
	 */
	private Integer amount;

	/**
	 * 已领数量
	 */
	private Integer usedAmount;

	/**
	 * 限制数额
	 */
	private Integer confineAmount;

	/**
	 * 平台补贴比例
	 */
	private Double platformRatio;

	/**
	 * 备注
	 */
	private String memo;

	/**
	 * 下单商品件数
	 */
	private Integer orderGoodsNum;

	/**
	 * 支付买家数
	 */
	private Integer payUserNum;

	/**
	 * 下单总金额
	 */
	private BigDecimal orderTotalPrice;

	/**
	 * 下单单量
	 */
	private Integer orderNum;
	
	/**
	 * 报名标题
	 */
	private String applyTitle;

	/**
	 * 报名开始时间
	 */
	private Date applyStartTime;

	/**
	 * 报名结束时间
	 */
	private Date applyEndTime;

	/**
	 * 报名说明
	 */
	private String applyDesc;

	/**
	 * 店铺等级要求
	 */
	private Long storeGrade;

	/**
	 * 最少报名商品数
	 */
	private Integer mixProductNum;

	/**
	 * 最大报名商品数
	 */
	private Integer maxProductNum;

	/**
	 * 报名状态
	 */
	private Integer state;

	/**
	 * 活动状态
	 */
	private Integer marketState;

	/**
	 * 子类型
	 */
	private Integer subType;

	/**
	 * 活动配置
	 */
	private String activeConfig;

	public void clearInit() {
		this.setCreatedTime(null);
		this.setVersion((Long) null);
	}

}
