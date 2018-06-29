package com.topaiebiz.merchant.enter.dto;

import java.io.Serializable;
import java.util.List;

import com.topaiebiz.merchant.enter.entity.MerchantAuditDetailEntity;
import lombok.Data;

/**
 * Description: 商家入驻审核记录Dto
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年10月13日 上午9:21:08
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerchantauditLogDto implements Serializable {

    /**
     * 全局唯一标识符
     */
    private Long id;

    /**
     * 商家信息
     */
    private Long merchantId;

    /**
     * 审核结果
     */
    private Integer auditResult;

    /**
     * 不通过原因
     */
    private String noPassReason;

    /**
     * 不通过字段
     */
    private String noPassField;

    /**
     * 审核记录Id
     */
    private Long auditLogId;

    private List<MerchantAuditDetailEntity> detailList;

    /**
     * 审核状态
     */
    private Integer state;

}
