package com.topaiebiz.merchant.store.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Aurthor:zhaoxupeng
 * @Description:再次审核详情
 * @Date 2018/4/4 0004 下午 12:46
 */
@Data
public class MerchantModifyDetailDto extends PagePO implements Serializable {


    /**
     * 再次审核状态 0 审核通过，1审核待审核，2审核未通过
     */
    private Integer status;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 修改的值
     */
    private String modifiedValue;

    /**
     * 不通过原因
     */
    private String noPassReason;

    /**
     * 审核人
     */
    private String examineAuditor;

    /**
     * 审核时间
     */
    private Date examineTime;

    /**
     * 再次审核id
     */
    private Long modifyId;

}
