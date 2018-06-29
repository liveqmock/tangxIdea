package com.topaiebiz.merchant.store.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/31 0031 下午 8:09
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
