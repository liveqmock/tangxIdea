package com.topaiebiz.giftcard.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.giftcard.service.GiftcardRefundLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


/**
 * @description: 处理老系统退款
 * @author: Jeff Chen
 * @date: created in 上午9:55 2018/5/7
 */
@RestController
@RequestMapping("/giftcard/log")
public class GiftcardRefundLogController extends AbstractController {

    @Autowired
    private GiftcardRefundLogService giftcardRefundLogService;

    @RequestMapping("/refund/{date}")
    public ResponseInfo refundByCard(@PathVariable Long date) {

        return new ResponseInfo(giftcardRefundLogService.refundByCard(new Date(date)));

    }
}
