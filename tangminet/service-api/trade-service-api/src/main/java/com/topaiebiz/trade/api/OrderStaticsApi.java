package com.topaiebiz.trade.api;

import com.topaiebiz.trade.dto.statics.OrderStatusCountDTO;
import com.topaiebiz.trade.dto.statics.OrderVolumeDTO;
import com.topaiebiz.trade.dto.statics.PromotionStaticsDTO;

import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-06 11:15
 */
public interface OrderStaticsApi {

    /**
     * 统计单个营销活动的数据
     *
     * @param promotionIds 营销活动ID
     * @return
     */
    Map<Long, PromotionStaticsDTO> promotionStatics(List<Long> promotionIds);

    /**
     * 统计个人的全部交易量
     * @param memberId 会员id
     * @return
     */
    OrderVolumeDTO queryOrderStatics(Long memberId);

    /**
     * 统计个人在某家店铺的交易量
     * @param memberId 会员id
     * @param storeId 店铺id
     * @return
     */
    OrderVolumeDTO queryStoreOrderStatics(Long memberId, Long storeId);

    /**
     * 查询会员按照订单状态分组的统计数据
     * @param memberId 会员id
     * @return
     */
    OrderStatusCountDTO queryOrderStatusCount(Long memberId);
}