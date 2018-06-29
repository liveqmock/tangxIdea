package com.topaiebiz.merchant.enter.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description: 商家入驻审核详情Dto
 * 
 * Author : Anthony
 * 
 * Date :2017年10月12日 下午3:09:16
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerchantAuditDetailDto  implements Serializable {

	/** 全局唯一标识符 */
	private Long id;

	/** 审核记录Id */
	private Long auditLogId;

	/** 商家信息 */
	@NotNull(message = "{validation.MerchantAuditDetail.merchantId}")
	private Long merchantId;

	/** 不通过字段 */
	private String noPassField;

	/** 不通过原因 */
	private String noPassReason;

}
