package com.topaiebiz.merchant.info.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 商家等级配置
 */
@Data
public class MerchantInfoGradeDto implements Serializable {

    private Long id;

    private Long merchantId;

    private  Long merchantGradeId;

}
