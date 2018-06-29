package com.topaiebiz.trade.order.controller;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.common.ServletRequestUtil;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.trade.order.core.order.context.BuyerContext;
import com.topaiebiz.trade.order.dto.ordersubmit.*;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.ordersubmit.GoodsPromoitonQueryPO;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestStore;
import com.topaiebiz.trade.order.po.ordersubmit.PageInitPO;
import com.topaiebiz.trade.order.service.OrderSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/***
 * 下单相关接口
 * @author yfeng
 * @date 2018-01-08 14:16
 */
@Slf4j
@MemberLogin
@RestController
@RequestMapping(value = "/trade/order", method = RequestMethod.POST)
public class OrderSubmitController {

    @Autowired
    private OrderSubmitService orderSubmitService;

    private void saveBuyerToContext() {
        MemberTokenDto token = MemberContext.getCurrentMemberToken();

        BuyerBO buyerBO = new BuyerBO();
        buyerBO.setMemberId(token.getMemberId());
        buyerBO.setMemberName(token.getUserName());
        buyerBO.setMobile(token.getTelephone());
        BuyerContext.set(buyerBO);
    }

    @RequestMapping("/pageInit")
    public ResponseInfo pageInit(@RequestBody PageInitPO pageInitPO) {
        saveBuyerToContext();
        GoodsSplitDTO splitDTO = orderSubmitService.loadInitPage(BuyerContext.get(), pageInitPO);
        return new ResponseInfo(splitDTO);
    }

    @Deprecated
    @RequestMapping("/goodsPromotions")
    public ResponseInfo goodsPromotions(@RequestBody GoodsPromoitonQueryPO queryPO) {
        saveBuyerToContext();
        List<PromotionInfoDTO> promotions = orderSubmitService.loadGoodsPromotions(BuyerContext.get(), queryPO.getGoodsId(), queryPO.getGoodsNum());
        return new ResponseInfo(promotions);
    }

    @RequestMapping("/storePromotions")
    public ResponseInfo storePromotions(@RequestBody OrderRequestStore storeRequest) {
        saveBuyerToContext();
        List<PromotionInfoDTO> promotions = orderSubmitService.storePromotions(BuyerContext.get(), storeRequest);
        return new ResponseInfo(promotions);
    }

    @RequestMapping("/storeCoupons")
    public ResponseInfo storeCoupons(@RequestBody OrderRequestStore storeRequest) {
        saveBuyerToContext();
        PromotionListDTO promotions = orderSubmitService.loadStoreCoupons(BuyerContext.get(), storeRequest);
        return new ResponseInfo(promotions);
    }

    @RequestMapping("/freightPromotions")
    public ResponseInfo freightPromotions(@RequestBody OrderRequestStore storeRequest) {
        saveBuyerToContext();
        List<PromotionInfoDTO> promotions = orderSubmitService.loadFreightPromotions(BuyerContext.get(), storeRequest);
        PromotionInfoDTO promotionInfoDTO = CollectionUtils.isEmpty(promotions) ? null : promotions.get(0);
        return new ResponseInfo(promotionInfoDTO);
    }

    @RequestMapping("/storeOrderAmount")
    public ResponseInfo storeOrderAmount(@RequestBody OrderRequestStore storeRequest) {
        saveBuyerToContext();
        OrderAmountDTO orderAmountDTO = orderSubmitService.caculateOrderAmount(BuyerContext.get(), storeRequest);
        return new ResponseInfo(orderAmountDTO);
    }

    @RequestMapping("/platformPromotions")
    public ResponseInfo platformPromotions(@RequestBody OrderRequest orderRequest) {
        saveBuyerToContext();
        PromotionListDTO promotions = orderSubmitService.loadPlatformPromotions(BuyerContext.get(), orderRequest);
        return new ResponseInfo(promotions);
    }

    @RequestMapping("/orderSummary")
    public ResponseInfo orderSummary(@RequestBody OrderRequest orderRequest) {
        saveBuyerToContext();
        OrderSummaryDTO summaryDTO = orderSubmitService.getOrderSummary(BuyerContext.get(), orderRequest);
        return new ResponseInfo(summaryDTO);
    }

    @RequestMapping(value = "/submitOrder")
    public ResponseInfo submitOrder(HttpServletRequest request, @RequestBody OrderRequest orderRequest) {
        saveBuyerToContext();
        BuyerBO buyerBO = BuyerContext.get();
        log.info("buyer {} submit order: {}", JSON.toJSONString(buyerBO), JSON.toJSONString(orderRequest));

        orderRequest.setUserAgent(request.getHeader("User-Agent"));
        orderRequest.setIp(ServletRequestUtil.getIpAddress(request));

        OrderResult orderResult = orderSubmitService.submitOrder(BuyerContext.get(), orderRequest);
        return new ResponseInfo(orderResult);
    }
}