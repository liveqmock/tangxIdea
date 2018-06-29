package com.topaiebiz.trade.api.refund;

import com.topaiebiz.trade.dto.refund.RefundDTO;
import com.topaiebiz.trade.dto.refund.RefundDetailDTO;

import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-03-26 10:59
 */
public interface RefundServiceApi {

    /**
     * 更具正向订单ID集合查询已经完成退款订单列表
     *
     * @return
     */
    List<RefundDTO> queryFinishedRefundOrders(List<Long> storeOrderIds);

    /**
     * 根据退款ID查询退款详情
     *
     * @param refundIds
     * @return
     */
    Map<Long, List<RefundDetailDTO>> querySKURefundDetails(List<Long> refundIds);
}