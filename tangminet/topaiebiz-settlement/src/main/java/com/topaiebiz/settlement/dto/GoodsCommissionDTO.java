package com.topaiebiz.settlement.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-03-28 10:20
 */
@Data
public class GoodsCommissionDTO {
    private BigDecimal commissionTotal;
    private List<GoodsCommissionDetailDTO> commissionDetail = new ArrayList<>();
    private List<GoodsDetailDTO> goodsDetail = new ArrayList<>();
}