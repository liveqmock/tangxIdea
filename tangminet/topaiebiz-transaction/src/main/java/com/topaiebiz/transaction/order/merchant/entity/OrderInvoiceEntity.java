package com.topaiebiz.transaction.order.merchant.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 
 * Description 订单发票信息表，存储订单需要开具的发票信息实体类  
 * 
 * 
 * Author zhushuyong 
 *    
 * Date 2017年9月4日 上午11:09:56 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@TableName("t_tsa_order_invoice")
public class OrderInvoiceEntity extends BaseBizEntity<Long> {

	/** 版本化序列号*/
	@TableField(exist = false)
	private static final long serialVersionUID = -7712525922205332738L;
	
	/** 订单id*/
	private Long orderId;
	
	/** 店铺id*/
	private Long storeId;
	
	/** 发票类型。 1 普通 2电子 3增值税*/
	private Short invoiceType;
    
	/** 发票抬头。*/
	private String title;
    
	/** 发票内容。*/
	private String text;
	
	/** 纳税人识别号。*/
	private String taxpayerNo;
    
	/** 增值税发票专用。1 订单完成后开票*/
	private Short modeType;
    
	/** 增值税发票专用。*/
	private String name;
    
	/** 开票金额。*/
	private BigDecimal sum;
    
	/** 地址电话。*/
	private String addressTel;
	
	/** 开户行及账号。*/
	private String account;
    
	/** 状态。1 已开 2未开*/
	private Integer state;

	/** 电子发票路径。 */
	private String invoiceImage;

	/** 发票代码。 */
	private String invoiceCode;

	/** 发票号码。 */
	private String invoiceNum;

	/**配送地址的主键ID*/
	@TableField(exist=false)
	private Long addressId;
	
	/**礼卡主键ID*/
	@TableField(exist=false)
	private Long cardId;
	
	/**礼卡数量*/
	@TableField(exist=false)
	private Long number;
	
	/**token令牌*/
	@TableField(exist=false)
	private String token;
}