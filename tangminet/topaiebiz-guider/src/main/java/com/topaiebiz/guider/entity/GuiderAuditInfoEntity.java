package com.topaiebiz.guider.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * Created by admin on 2018/5/30.
 * 记录导购基本信息
 */
@Data
@TableName("t_guider_audit_info")
public class GuiderAuditInfoEntity extends BaseBizEntity<Long> {


    /**
     * 导购id
     */
    private Long guiderId;

    /**
     * 审核当时数据的备份
     */
    private String originData;

    /**
     * 审核原因
     */
    private String auditReason;



}
