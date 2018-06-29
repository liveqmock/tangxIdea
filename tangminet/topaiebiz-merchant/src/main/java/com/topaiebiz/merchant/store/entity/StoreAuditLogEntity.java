package com.topaiebiz.merchant.store.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/31 0031 下午 8:07
 */
@TableName("t_mer_store_audit_log")
@Data
public class StoreAuditLogEntity  extends BaseBizEntity<Long> {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = -1101244490260477378L;

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
}
