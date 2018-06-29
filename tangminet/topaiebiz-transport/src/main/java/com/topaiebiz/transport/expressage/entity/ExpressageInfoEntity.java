package com.topaiebiz.transport.expressage.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseEntity;
import lombok.Data;

/**
 * Description 快递信息实体类 
 * 
 * Author Aaron.Xue 
 *    
 * Date 2017年10月17日 下午9:04:43 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_exp_expressage_info")
@Data
public class ExpressageInfoEntity extends BaseEntity<Long> {

	/**
	 * 序列版本号
	 */
	private static final long serialVersionUID = 4654424670107119088L;

	/**快递单号。*/
	private String nu;

	/**快递公司 编码。*/
	private String com;

	/**快递单当前签收状态，包括0在途中、1已揽收、2疑难、3已签收、4退签、5同城派送中、6退回等状态*/
	private String state;

	/**是否签收。0为未签收。*/
	private String ischeck;

	/**数据信息。以键值对拼接。时间：信息，时间：信息。*/
	private String data;

	/**最后修改时间。*/
	private Date lastModifiedTime;

}
