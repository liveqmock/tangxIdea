package com.topaiebiz.goods.sku.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by hecaifeng on 2018/2/5.
 */
@Data
public class IntegralRatioDto {

    /** 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。 */
    @NotNull(message = "{validation.integralRatioDto.id}")
    private Long id;

    /** 积分比例。小数形式。*/
    @NotNull(message = "{validation.integralRatioDto.integralRatio}")
    private BigDecimal integralRatio;
}
