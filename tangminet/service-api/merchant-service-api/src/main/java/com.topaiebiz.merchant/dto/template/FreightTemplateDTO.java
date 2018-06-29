package com.topaiebiz.merchant.dto.template;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * zxp
 */
@Data
public class FreightTemplateDTO implements Serializable {

     /** 全局主键id */
    private Long id;

    /** 店铺ID */
    private Long storeId;

    /** 运费名称 */
    private String freightName;

    /** 计价方式。1 件数 2体积 3重量 */
    private Integer pricing;

    /** 是否仅配送特定地区。（1 为是 ，0为否） */
    private Integer onlyThis;

    /**运费模版详情*/
    private List<FreightTemplateDetailDTO> freightTempleteDetailList = new ArrayList<>();

    public boolean hasSaleAreaLimit(){
        return 1 == onlyThis;
    }

}
