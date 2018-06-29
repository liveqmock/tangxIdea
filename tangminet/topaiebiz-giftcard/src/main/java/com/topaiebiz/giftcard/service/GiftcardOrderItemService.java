package com.topaiebiz.giftcard.service;

import com.baomidou.mybatisplus.service.IService;
import com.topaiebiz.giftcard.entity.GiftcardOrderItem;
import com.topaiebiz.giftcard.vo.OrderSuccVO;

/**
 * @description: 订单子项服务
 * @author: Jeff Chen
 * @date: created in 下午8:44 2018/1/25
 */
public interface GiftcardOrderItemService extends IService<GiftcardOrderItem> {

    /**
     * 查询支付成功后的订单信息
     * @param orderId
     * @return
     */
    OrderSuccVO getPaidOrderById(Long orderId,Long memberId);
}
