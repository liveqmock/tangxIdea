package com.topaiebiz.giftcard.service;

import com.baomidou.mybatisplus.service.IService;
import com.topaiebiz.giftcard.entity.GiftcardRefundLog;

import java.util.Date;

/**
 * @description: 处理老系统退款
 * @author: Jeff Chen
 * @date: created in 上午9:05 2018/5/7
 */
public interface GiftcardRefundLogService extends IService<GiftcardRefundLog>{

    /**
     * 按卡退款
     * @return
     */
    String refundByCard(Date date);
}
