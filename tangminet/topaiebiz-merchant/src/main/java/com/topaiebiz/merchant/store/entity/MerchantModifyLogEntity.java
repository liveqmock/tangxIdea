package com.topaiebiz.merchant.store.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/4/9 0009 下午 12:02
 */
@TableName("t_mer_merchant_modify_log")
@Data
public class MerchantModifyLogEntity extends BaseBizEntity<Long> {

    /**
     * 商家id
     */
    private Long merchantId;
    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 审核结果
     */
    private Integer auditResult;

    /**
     * 审核人
     */
    private String examineAuditor;

    /**
     * 审核时间
     */
    private Date examineTime;

    /**
     * 修改状态；0表示数据可用，1数据已作废
     */
    private Integer status;

}
