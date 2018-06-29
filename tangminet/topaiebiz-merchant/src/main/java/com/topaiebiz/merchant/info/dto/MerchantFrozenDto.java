package com.topaiebiz.merchant.info.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 商家店铺冻结/解冻dto
 *
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/2/2 0002 下午 4:05
 */
@Data
public class MerchantFrozenDto implements Serializable {
    /**
     * 全局唯一标识符
     */
    private Long id;

    /**
     * 变更状态  冻结为2   通过为0，变更为1
     */
    private Integer changeState;

    /**
     * 商家id
     */
    private Long merchantId;

}
