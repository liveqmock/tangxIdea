package com.topaiebiz.trade.order.core.pay.bo;

import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-17 22:01
 */
@Data
public class PayParamContext {

    /**
     * 支付订单信息
     */
    private OrderPayEntity orderPayEntity;


    /**
     * 店铺支付分摊结果
     */
    private List<StorePayBO> storePayDetails;

    /**
     * 清理分配结果
     */
    public void cleanDispatch() {
        if (CollectionUtils.isEmpty(storePayDetails)) {
            return;
        }
        for (StorePayBO storePayBO : storePayDetails) {
            storePayBO.cleanDispatch();
        }
    }

    /**
     * 使用站内支付支付了整个订单
     */
    private boolean pkgFull;
}