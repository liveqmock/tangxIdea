package com.topaiebiz.promotion.mgmt.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 
 * Description： 营销活动信息表DTO
 * 
 * 
 * Author Joe
 * 
 * Date 2017年9月25日 下午7:56:46
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class PromotionDto extends PagePO{

	/**
	 * ID
	 */
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
	@NotNull(message = "{validation.promotion.name}")
	@Length(max = 15, message = "{validation.promotion.nameLength}")
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
	 * 活动开始时间
	 */
	@NotNull(message = "{validation.promotion.startTime}")
	private String promotionStart;

	/**
	 * 活动结束时间
	 */
	@NotNull(message = "{validation.promotion.endTime}")
	private String promotionEnd;

	/**
	 * 活动结束时间
	 */
	private Date endTime;

	/**
	 * 活动说明
	 */
	@Length(max = 40, message = "{validation.promotion.descriptionLength}")
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
	@NotNull(message = "{validation.promotion.condType}")
	private Integer condType;

	/**
	 * 条件值
	 */
	@NotNull(message = "{validation.promotion.condValue}")
	@Min(0)
	private BigDecimal condValue;

	/**
	 * 优惠类型
	 */
	private Integer discountType;

	/**
	 * 优惠值
	 */
	@NotNull(message = "{validation.promotion.discountValue}")
	@Min(0)
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
	 * 报名开始时间
	 */
	private String promotionEnrolStart;

	/**
	 * 报名结束时间
	 */
	private String promotionEnrolEnd;
	
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
	@NotNull(message = "{validation.promotion.marketState}")
	private Integer marketState;

	/**
	 * 优惠总额
	 */
	private BigDecimal totalDiscount;

	/**
	 * 创建时间
	 */
	private Date createdTime;
	
	/**
	 * 商家报名数量
	 */
	private Integer storeEnrolNum;
	
	/**
	 * 已审核商家数量
	 */
	private Integer auditStoreNum;
	
	/**
	 * 是否可报名
	 */
	private Integer whetherEnrol;
	
	/**
	 * 报名店铺
	 */
	private Long storeId;
	
	/**
	 * 报名店铺审核状态
	 */
	private Integer auditState;

	/**
	 * 等级名称
	 */
	private String storeGradeName;

	/**
	 * 报名商品数量
	 */
	private Integer enrolGoodsNum;

	/**
	 * 删除标识
	 */
	private byte deletedFlag;

	/**
	 * 发行起始时间r
	 */
	private String releaseStartTime;

	/**
	 * 发行结束时间
	 */
	private String releaseEndTime;

	/**
	 * 每个ID每日限制领取数量
	 */
	private Integer dayConfineAmount;

	/**
	 * 是否直接直接发布
	 */
	private Integer isRelease;

	/**
	 * 领取方式
	 */
	private String receiveType;


	/**
	 * 用户类型 0-新用户 1-老用户
	 */
	private Integer userType;

	/**
	 * 分享限制领取人数
	 */
	private Integer shareConfinePeopleAmount;

	/**
	 * 单人活动领取数量
	 */
	private Integer receiveConfineAmount;

	/**
	 *
	 */
	private Integer subType;

	/**
	 * 活动配置
	 */
	private String activeConfig;


	/**
	 * 创建起始时间
	 */
	private String createdStartTime;


	/**
	 * 创建结束时间
	 */
	private String createdEndTime;

	/**
	 * 分享优惠券发放总份数
	 */
	private Integer numberOfCopies;

	/**
	 * 营销活动id列表，优惠券选择用
	 */
	private List<Long> promotionIdList;

	/**
	 * 优惠券id列表
	 */
	private Long couponId;

	/**
	 * 券是否为活动老数据
	 */
	private	Integer isReleaseData;

	/**
	 * 券的总数量
	 */
	private Integer totalNum;

	/**
	 * 券剩余的数量
	 */
	private Integer remainderNum;

	/**
	 * 分享份数新增
	 */
	private Integer shareAddNum;

	/**
	 * 券剩余的数量
	 */
	private Integer remainderAmount;

	/**
	 * 店铺名称
	 */
	private String storeName;

}
