package com.topaiebiz.merchant.store.entity;


import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * @Aurthor:zhaoxupeng
 * @Description:商家修改信息entity
 * @Date 2018/3/30 0030 上午 9:45
 */
@TableName("t_mer_merchant_modify_detail")
@Data
public class MerchantModifyInfoEntity extends BaseBizEntity<Long> {

    /**
     * 再次审核id
     */
    private Long modifyId;

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

}
