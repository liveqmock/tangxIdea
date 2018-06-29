package com.topaiebiz.transport.expressage.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseEntity;
import lombok.Data;

/**
 * Description 快递100订阅日志
 * 
 * Author Aaron.Xue
 * 
 * Date 2017年10月18日 上午10:16:27
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_exp_expressage_subscription_log")
@Data
public class ExpressageSubscriptionLogEntity extends BaseEntity<Long> {

	/**
	 * 序列码
	 */
	private static final long serialVersionUID = -5324739368478807275L;

	/** 快递单号。 */
	private String nu;

	/** 快递公司。 */
	private String com;

	/** 是否成功。1表示成功。 */
	private String isSuccess;

	/** 快递100返回码。 */
	private String returnCode;

	/** 最后修改时间。 */
	private Date lastModifiedTime;

}
