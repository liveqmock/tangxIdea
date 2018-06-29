package com.topaiebiz.trade.order.core.order.handler;

import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import lombok.Data;
import org.apache.commons.collections4.map.LinkedMap;

import java.util.Map;

/***
 * 下单上下文参数
 * @author yfeng
 * @date 2018-01-09 10:08
 */
@Data
public class OrderSubmitContext {
    /**
     * 店铺订单
     */
    private Map<Long, StoreOrderBO> storeOrderMap = new LinkedMap<>();

    /**
     * 平台优惠活动
     */
    private PromotionDTO platformPromotion;

    private boolean hasHaitaoOrder;
}