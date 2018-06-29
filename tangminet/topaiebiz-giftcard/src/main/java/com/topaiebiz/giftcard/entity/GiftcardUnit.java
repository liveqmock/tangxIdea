package com.topaiebiz.giftcard.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 售出或生产的卡实体
 */
@TableName("t_giftcard_unit")
@Data
public class GiftcardUnit implements Serializable{

	/**
	 * id
	 * 实体主键
	 */
	private Long id;

	/**
	 * 卡号
	 */
	private String cardNo;

	/**
	 * 关联发行id
	 */
	private Long batchId;

	/**
	 * batch_no
	 * 批次号
	 */
	private String batchNo;

	/**
	 * owner
	 * 卡所有权的拥有者id：电子卡购买的人，实体卡绑定的人
	 */
	private Long owner;

	/**
	 * binding_member
	 * 绑定卡的用户
	 */
	private Long bindingMember;

	/**
	 * given_status
	 * 转赠状态：0-不可转赠，1-可转赠，2-已转赠
	 */
	private Integer givenStatus;

	/**
	 * card_status
	 * 卡状态:0-未绑定，1-已绑定，2-已激活，3-已用完，4-已过期，5-已冻结
	 */
	private Integer cardStatus;

	/**
	 * 保存card_status变更的上次值，默认-1无上次修改记录
	 */
	private Integer lastStatus;

	/**
	 * password
	 * 卡密
	 */
	private String password;

	/**
	 * label_id
	 * 关联标签id
	 */
	private Long labelId;

	/**
	 * 绑定时间
	 */
	private Date bindingTime;

	/**
	 * 失效时间
	 */
	private Date deadTime;

	/**
	 * 卡片余额
	 */
	private BigDecimal balance;

	/**
	 * 激活时间
	 */
	private Date activeTime;

	/**
	 * creator
	 * 创建人，默认系统生成的
	 */
	private String creator;

	/**
	 * created_time
	 * 创建时间
	 */
	private Date createdTime;

	/**
	 * modifier
	 * 修改人
	 */
	private String modifier;

	/**
	 * modified_time
	 * 修改时间
	 */
	private Date modifiedTime;

	/**
	 * priority
	 * 从1-10结算优先级逐级提升，即优先级高的美礼卡先结算
	 */
	@TableField(exist = false)
	private Integer priority;
	/**
	 * 标签名称
	 */
	@TableField(exist = false)
	private String labelName;
	/**
	 * face_value
	 * 面值
	 */
	@TableField(exist = false)
	private BigDecimal faceValue;

	/**
	 * sale_price
	 * 售价
	 */
	@TableField(exist = false)
	private BigDecimal salePrice;

	/**
	 * 发行数量
	 */
	@TableField(exist = false)
	private Integer issueNum;
	/**
	 * 介质
	 */
	@TableField(exist = false)
	private Integer medium;
	/**
	 * 封面
	 */
	@TableField(exist = false)
	private String cover;
	/**
	 * 副标题
	 */
	@TableField(exist = false)
	private String subtitle;

	@TableField(exist = false)
	private String cardName;
	/**
	 * 有效天数
	 */
	@TableField(exist = false)
	private Integer validDays;

	/**
	 * 卡属性
	 */
	@TableField(exist = false)
	private Integer cardAttr;

	/**
	 * 适用范围
	 */
	@TableField(exist = false)
	private Integer applyScope;

	/**
	 * 店铺ids
	 */
	@TableField(exist = false)
	private String storeIds;

	/**
	 * 店铺贴现比
	 */
	@TableField(exist = false)
	private BigDecimal storeDiscount;

	/**
	 * 平台贴现比
	 */
	@TableField(exist = false)
	private BigDecimal platformDiscount;
}
