package com.topaiebiz.transaction.order.merchant.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * 
 * Description 订单配送地址的实体类
 * 
 * 
 * Author:zhushuyong
 * 
 * Date 2017年8月30日 下午8:53:07
 * 
 * Copyright:Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@TableName("t_tsa_order_address")
public class OrderAddressEntity extends BaseBizEntity<Long> {

	/** 序列化版本号 */
	@TableField(exist = false)
	private static final long serialVersionUID = 4455910672930257562L;

	/**
	 * 会员ID
	 */
	private Long memberId;

    /**
     * 会员地址表主键ID
     */
	private Long memberAddressId;

	/** 订单编号 */
	private Long orderId;

	/** 收货人姓名 */
	private String name;

	/** 地址区域 */
	private Long districtId;

	/**
	 * 省
	 */
	private String province;

	/**
	 * 市
	 */
	private String city;

	/**
	 * 区
	 */
	private String county;


	/** 收货人地址 */
	private String address;

	/** 收货人邮编 */
	private String zipCode;

	/** 收货人手机号 */
	private String telephone;

	/** 收货人座机号 */
	private String landline;

	/** 紧急联系人，备用电话 */
	private String otherTelephone;

	/**
	 * 身份证号码
	 */
	private String idNum;
	/**
	 * 购买人姓名
	 */
	private String buyerName;
}