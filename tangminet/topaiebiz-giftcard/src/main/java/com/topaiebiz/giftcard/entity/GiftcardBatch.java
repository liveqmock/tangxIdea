package com.topaiebiz.giftcard.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 发行的礼卡信息
 */
@TableName("t_giftcard_batch")
@Data
public class GiftcardBatch implements Serializable{

	/**
	 * id
	 * 表主键
	 */
	private Long id;

	/**
	 * batch_no
	 * 批次号
	 */
	private String batchNo;

	/**
	 * card_name
	 * 卡片名称
	 */
	private String cardName;

	/**
	 * subtitle
	 * 副标题
	 */
	private String subtitle;

	/**
	 * medium
	 * 0实体卡 4电子卡
	 */
	private Integer medium;

	/**
	 * label_id
	 * 标签id：参考t_giftcard_label
	 */
	private Long labelId;

	/**
	 * 标签名称
	 */
	@TableField(exist = false)
	private String labelName;

	/**
	 * prefix
	 * 卡号前缀
	 */
	private String prefix;

	/**
	 * face_value
	 * 面值
	 */
	private BigDecimal faceValue;

	/**
	 * sale_price
	 * 售价
	 */
	private BigDecimal salePrice;

	/**
	 * apply_scope
	 * 适用范围：1 全部平台 2部分店铺可用 3 部分店铺不可用
	 */
	private Integer applyScope;

	/**
	 * store_ids
	 * 店铺id
	 */
	private String storeIds;

	/**
	 * card_attr
	 * 卡属性
	 */
	private Integer cardAttr;

	/**
	 * discount_amount
	 * 贴现总额
	 */
	private BigDecimal discountAmount;

	/**
	 * platform_discount
	 * 平台贴现
	 */
	private BigDecimal platformDiscount;

	/**
	 * store_discount
	 * 店铺贴现
	 */
	private BigDecimal storeDiscount;

	/**
	 * given_flag
	 * 是否可转赠：0否 1是
	 */
	private Integer givenFlag;

	/**
	 * issue_num
	 * 发行数量
	 */
	private Integer issueNum;

	/**
	 * out_num
	 * 电子卡售出数量，实体卡生产数量
	 */
	private Integer outNum;

	/**
	 * 下单数量
	 */
	private Integer orderQty;

	/**
	 * valid_days
	 * 有效天数
	 */
	private Integer validDays;

	/**
	 * limit_num
	 * 限购数量：-1不限制
	 */
	private Integer limitNum;

	/**
	 * cover
	 * 封面图片url
	 */
	private String cover;

	/**
	 * spec
	 * 备注描述
	 */
	private String spec;

	/**
	 * issue_status
	 * 状态：0-待审核，1-审核通过（未上架/未生产），2-未通过，3-已上架/未入库，4-已入库
	 */
	private Integer issueStatus;

	/**
	 * priority
	 * 从1-10结算优先级逐级提升，即优先级高的美礼卡先结算
	 */
	private Integer priority;

	/**
	 * del_flag
	 * 删除标记:0未删除 1删除
	 */
	private Integer delFlag;

	/**
	 * 备注
	 */
	private String remark;
	/**
	 * creator
	 * 创建者
	 */
	private String creator;

	/**
	 * created_time
	 * 创建时间
	 */
	private Date createdTime;

	/**
	 * modifier
	 * 修改者
	 */
	private String modifier;

	/**
	 * modified_time
	 * 修改时间
	 */
	private Date modifiedTime;
	/**
	 * 卡号起始
	 */
	private Long noStart;
	/**
	 * 卡号结束
	 */
	private Long noEnd;

	@TableField(exist = false)
	private List<Long> goodsIds;
}
