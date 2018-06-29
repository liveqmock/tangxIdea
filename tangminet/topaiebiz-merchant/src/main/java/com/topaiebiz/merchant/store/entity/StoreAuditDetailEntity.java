package com.topaiebiz.merchant.store.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/31 0031 下午 8:04
 */
@TableName("t_mer_store_audit_detail")
@Data
public class StoreAuditDetailEntity extends BaseBizEntity<Long> {

    /**
     * 版本序列化
     */
    private static final long serialVersionUID = -4158460879531457123L;

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
