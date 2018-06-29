package com.topaiebiz.merchant.dto.store;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description: 商家信息表Dto
 * 
 * Author : Anthony
 * 
 * Date :2017年9月27日 下午1:43:48
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerchantInfoDTO implements Serializable {

	/**
	 * 全局唯一标识符
	 * */
	private Long id;

	/**
	 * 公司名称
	 * */
	@NotNull(message = "{validation.merchantInfo.name}")
	private String name;

	/**
	 * 连锁店、直营店等暂定
	 * */
	private Integer merchantType;

	/**
	 * 上级商户
	 * */
	private Long parentMerchant;

	/**
	 * 入驻状态。1申请，2审核通过 3 审核不通过 4待付款 5已完成
	 * */
	private Integer state;

	/**
	 * 商家联系人姓名
	 * */
	@NotNull(message = "{validation.merchantInfo.contactName}")
	private String contactName;

	/** 联系人手机号 */
	@NotNull(message = "{validation.merchantInfo.contactTele}")
	@Length(min = 11, max = 11)
	private String contactTele;

	/** 店铺的积分。和后期奖惩有关系 */
	private Long integral;

	/** 商家等级积分 */
	@NotNull(message = "{validation.merchantInfo.gradeIntegral}")
	private Long gradeIntegral;

	/** 商家等级 */
	@NotNull(message = "{validation.merchantInfo.merchantGradeId}")
	private Long merchantGradeId;

	/** 所属商家 */
	private Long merchantId;

	/** 银行开户名 */
	private String accountName;

	/** 公司银行帐号 */
	private String account;

	/** 开户银行支行名称 */
	private String bankName;

	/** 支行银联号 */
	private String bankNum;

	/** 开户行所在区域 */
	private Long accountDistrictId;

	/** 是否为结算账号 */
	private Integer isSettle;

	/** 开户银行许可证电子版 */
	private String electronicImage;

	/** 预留电话 */
	private String telephone;

	/** 公司详细地址 */
	private String address;

	/** 员工总数 */
	private Long staffNo;

	/** 注册资金，单位万元 */
	private String capital;

	/** 联系人身份证号 */
	private String idCard;

	/** 联系人身份证号电子版 */
	private String idCardImage;

	/** 电子邮箱 */
	private String email;

	/** 营业执照号 */
	private String licenseNo;

	/** 营业执照号所在地 */
	private String licenseLocation;

	/** 营业执照有效期起始 */
	private Date licenseBegin;

	/** 营业执照有效期结束 */
	private Date licenseEnd;

	/** 法定经营范围 */
	private String manageScope;

	/** 营业执照电子版 */
	private String licenseImage;

	/** 组织机构代码 */
	private String organCode;

	/** 一般纳税人证明 */
	private String taxpayerImage;

	/** 税务登记证号 */
	private String taxRegistNo;

	/** 纳税人识别号 */
	private String taxpayerNo;

	/** 税务登记证号电子版 */
	private String taxpayerNoImage;

	/** 需要支付的金额 */
	private double PaymentPrice;

	/** 支付凭证图片 */
	private String payImage;

	/** 支付时间 */
	private Date payTime;

	/** 店铺模板 */
	private Long templateId;

	/** 实体店位置 */
	private String storeAddress;

	/** 门店电话 */
	private String storeTele;

	/** 商家介绍 */
	private String description;

	/** 门店照片多张 */
	private String images;

	/** 门店数量 */
	private Long storeNumber;

	/** 备注 */
	private String memo;

	/**
	 * 入驻时间
	 */
	private Date createdTime;
}
