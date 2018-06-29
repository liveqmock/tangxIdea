package com.topaiebiz.merchant.enter.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Description: 商家审核记录回显
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年11月5日 下午9:00:06
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerchantAuditDto implements Serializable {

    /**
     * 全局唯一标识符
     */
    private Long id;

    /**
     * 商家信息
     */
    private Long merchantId;

    /**
     * 不通过原因
     */
    private String noPassReason;

    /**
     * 审核记录Id
     */
    private Long auditLogId;

    /**
     * 不通过字段
     */
    private String noPassField;

    /**
     * 商家入驻状态
     */
    private Integer state;
}
