package com.topaiebiz.merchant.dto.template;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * zxp
 */
@Data
public class FreightTemplateDetailDTO implements Serializable {

    /** 全局主键id */
    private Long id;

    /** 关联的运费模板ID */
    private Long freightId;

    /** 配送方式 */
    private Integer type;

    /** 配送区域集合 */
    private List<Long> supportCityIds;

    /** 首次价格 */
    private BigDecimal firstPrice;

    /** 首次件数 */
    private BigDecimal firstNum;

    /** 续件价格 */
    private BigDecimal addPrice;

    /** 续件件数 */
    private BigDecimal addNum;

    /** 是否为默认运费 */
    private Integer isDefault;

    private String nameListStr;

    public boolean isDefaultFreight(){
        return 0 == isDefault;
    }
}
