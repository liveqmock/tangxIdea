package com.topaiebiz.giftcard.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.giftcard.entity.GiftcardOrder;
import com.topaiebiz.giftcard.vo.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 礼卡订单服务
 * @author: Jeff Chen
 * @date: created in 下午3:08 2018/1/12
 */
public interface GiftcardOrderService extends IService<GiftcardOrder>{

    /**
     * 分页查询订单
     * @param giftcardOrderReq
     * @return
     */
    PageInfo<GiftcardOrderVO> queryOrders(GiftcardOrderReq giftcardOrderReq);
    /**
     * 订单详情
     * @param orderId
     * @return
     */
    GiftcardOrderDetailVO getByOrderId(Long orderId);

    /**
     * 准备下单
     * @param placeOrderVO
     * @return
     */
    MyOrderVO prepareOrder(PlaceOrderVO placeOrderVO);
    /**
     * 生单
     * @param placeOrderVO
     * @return
     */
    Long placeOrder(PlaceOrderVO placeOrderVO);

    /**
     * 支付后更新订单
     * @param giftcardOrder
     * @return
     */
    Boolean updateOrderAfterPay(GiftcardOrder giftcardOrder);

    /**
     * 会员修改订单：删除，取消等
     * @param giftcardOrder
     * @return
     */
    Boolean updateOrderByMember(GiftcardOrder giftcardOrder);

    /**
     * 查看我的订单详情
     * @param memberId
     * @param orderId
     * @return
     */
    MyOrderVO getMyOrderDetail(Long memberId, Long orderId);

    /**
     * 活动订单金额
     * @param orderId
     * @return
     */
    BigDecimal getOrderAmount(Long orderId);

    /**
     * 按条件更新指定属性
     * @param giftcardOrder
     * @param wrapper
     * @return
     */
    Integer updateBatchByWrapper(GiftcardOrder giftcardOrder, Wrapper wrapper);

    /**
     * 按条件导出订单数据
     * @param giftcardOrderReq
     * @return
     */
    List<GiftcardOrder> queryOrdersForExport(GiftcardOrderReq giftcardOrderReq);
}
