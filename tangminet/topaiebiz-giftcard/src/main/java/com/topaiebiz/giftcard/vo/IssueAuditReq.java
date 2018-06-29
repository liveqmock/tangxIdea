package com.topaiebiz.giftcard.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: 礼卡批次审核请求参数
 * @author: Jeff Chen
 * @date: created in 下午4:24 2018/1/16
 */
@Data
public class IssueAuditReq implements Serializable{

    /**
     * 发行主键
     */
    @NotNull(message = "卡批次id不能为空")
    private Long batchId;
    /**
     * 1-通过审核，2-未通过审核
     */
    @NotNull(message = "审核状态不能为空")
    private Integer issueStatus;

    /**
     * 备注说明
     */
    private String note;
}
