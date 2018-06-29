package com.topaiebiz.trade.order.core.cancel;

import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/***
 * @author yfeng
 * @date 2018-01-21 19:03
 */
@Data
public class CancelParamContext {
    private OrderPayEntity payEntity;
    private List<OrderEntity> orders = new ArrayList<>();
    private List<Long> orderIds = new ArrayList<>();

    private Map<Long, List<OrderDetailEntity>> detaiMaps = new HashMap();
    private List<Long> detailIds = new ArrayList<>();
}