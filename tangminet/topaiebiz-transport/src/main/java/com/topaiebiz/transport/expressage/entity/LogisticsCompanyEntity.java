package com.topaiebiz.transport.expressage.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseEntity;
import lombok.Data;

/**
 * Description 物流公司（快递） 实体类
 * 
 * Author Aaron.Xue
 * 
 * Date 2017年10月24日 下午4:25:10
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_exp_logistics_company")
@Data
public class LogisticsCompanyEntity extends BaseEntity<Long> {

	/**
	 * 序列版本号。
	 */
	private static final long serialVersionUID = -911912012861371032L;

	/**快递公司编码。*/
	private String company;

	/**快递公司名称。*/
	private String comName;

	/**状态 1 禁用 0 启用。*/
	private Integer status;

	/**公司图片*/
	private String image;

	/**说明。*/
	private String description;

	/**最后修改时间。取值为系统的当前时间。*/
	private Date lastModifiedTime;

}
