package com.topaiebiz.settlement.service;

import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.settlement.entity.StoreSettlementRefundOrderEntity;

import java.util.Date;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-03-24 13:05
 */
public interface SettlementJobService {
    boolean createSettlement(StoreInfoDetailDTO storeDTO);

    boolean createSettlement(StoreInfoDetailDTO storeDTO, Date startTime, Date endTime);

    /**
     * 获取需要订正退款订单佣金的结算单号集合
     *
     * @return
     */
    List<Long> getSettleIds();

    /**
     * 重置退款订单佣金的单号
     *
     * @param ids 结算单号集合
     * @return
     */
    Integer putRefundCommission(List<Long> ids);

    /**
     * 获取需要订正商品详情的订单号集合
     *
     * @return
     */
    List<Long> getOrderIds();

    /**
     * 重置结算订单的商品详情
     *
     * @param orderIds 订单号集合
     * @return
     */
    Integer putOrderGoodDetail(List<Long> orderIds);

    /**
     * 获取需要订正商品详情的售后订单集合
     *
     * @return
     */
    List<StoreSettlementRefundOrderEntity> getRefundIds();

    /**
     * 重置结算售后订单的商品详情
     *
     * @param refundList 售后订单集合
     * @return
     */
    Integer putRefundGoodDetail(List<StoreSettlementRefundOrderEntity> refundList);

    /**
     * 获取店铺的下个结算时间
     *
     * @param store 店铺信息
     * @param curEnd 上次结算时间
     * @return
     */
    Date getNextSettlementDate(StoreInfoDetailDTO store, Date curEnd);
}