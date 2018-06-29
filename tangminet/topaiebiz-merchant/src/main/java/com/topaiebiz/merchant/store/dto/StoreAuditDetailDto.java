package com.topaiebiz.merchant.store.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/31 0031 下午 8:08
 */
@Data
public class StoreAuditDetailDto implements Serializable {

    /**
     * 全局唯一标识符
     */
    private Long id;

    /**
     * 审核记录Id
     */
    private Long auditLogId;

    /**
     * 商家信息
     */
    private Long merchantId;

    /**
     * 不通过字段
     */
    private String noPassField;

    /**
     * 不通过原因
     */
    private String noPassReason;
}
