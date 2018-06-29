package com.topaiebiz.trade.order.dto.ordersubmit;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-09 11:18
 */
@Data
public class StoreItemDTO {
    private Long storeId;
    private String storeName;

    private List<StoreGoodsDTO> goodsList = new ArrayList<>();

    private BigDecimal freightPrice = BigDecimal.ZERO;
    private BigDecimal payPrice = BigDecimal.ZERO;
}