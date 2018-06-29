package com.topaiebiz.transport.dto;

import lombok.Data;

/**
 * 发送快递接受参数
 */
@Data
public class ExpressageParamDto {

    //快递公司编号
    private Long id;

    //快递单号
    private String number;

}
