package com.topaiebiz.merchant.enter.dto;

import com.topaiebiz.goods.dto.category.backend.BackendCategoryStatusDTO;
import com.topaiebiz.merchant.enter.entity.MerchantAuditDetailEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 入住审核dto
 *
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/22 0022 下午 12:43
 */
@Data
public class ExamineStateDto implements Serializable {

    private Long id;

    /**
     * 商家id
     */
    private Long merchantId;

    /**
     * 审核状态
     */
    private Integer state;

    /**
     * 审核时间
     */
    private Date examineTime;

    /**
     * 审核人
     */
    private String examineAuditor;

    /**
     * 选择商品的第三极类目
     */
    private Long[] ids;
    private List<BackendCategoryStatusDTO> backendCategoryStatusDTOS;

    /**
     * 类目状态
     */
    private Integer categoryStatus;

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

}
