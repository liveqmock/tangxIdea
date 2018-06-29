package com.topaiebiz.transport.dto;

import lombok.Data;

/**
 * 物流公司
 */
@Data
public class LogisticsDto {

    //主键ID
    private Long id;

    //快递公司名称
    private String comName;

    /**
     * 物流公司 拼音 CODE
     */
    private String expressCompanyCode;

}
