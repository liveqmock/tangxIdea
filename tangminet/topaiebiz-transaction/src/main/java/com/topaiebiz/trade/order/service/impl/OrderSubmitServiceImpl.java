package com.topaiebiz.trade.order.service.impl;

import com.google.common.collect.Lists;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.trade.order.core.config.OrderSubmitConfig;
import com.topaiebiz.trade.order.core.order.action.OrderSubmitActionChain;
import com.topaiebiz.trade.order.core.order.aop.ContextOperation;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderGoodsBO;
import com.topaiebiz.trade.order.core.order.context.OrderSummaryContext;
import com.topaiebiz.trade.order.core.order.context.PromotionDiscountContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandlerChain;
import com.topaiebiz.trade.order.core.order.handler.pageinit.OrderRequestHandler;
import com.topaiebiz.trade.order.core.order.promotion.compare.PromotionComparator;
import com.topaiebiz.trade.order.core.order.promotion.pattern.*;
import com.topaiebiz.trade.order.core.order.util.GoodsSplitDTOUtil;
import com.topaiebiz.trade.order.core.order.util.OrderSummaryUtil;
import com.topaiebiz.trade.order.dto.ordersubmit.*;
import com.topaiebiz.trade.order.facade.GoodsSkuServiceFacade;
import com.topaiebiz.trade.order.facade.PromotionServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestStore;
import com.topaiebiz.trade.order.po.ordersubmit.PageInitPO;
import com.topaiebiz.trade.order.service.OrderSubmitService;
import com.topaiebiz.transaction.order.merchant.exception.StoreOrderExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.ORDER_SUBMIT_DUPLICATE;

/***
 * @author yfeng
 * @date 2018-01-09 11:04
 */
@Service
@Slf4j
public class OrderSubmitServiceImpl implements OrderSubmitService {

    @Resource(name = OrderSubmitConfig.OrderHandlerChain.PAGE_INIT)
    private OrderSubmitHandlerChain pageInitChain;

    @Resource(name = OrderSubmitConfig.OrderHandlerChain.STORE_PROMOTION)
    private OrderSubmitHandlerChain storePromotionChain;

    @Resource(name = OrderSubmitConfig.OrderHandlerChain.STORE_COUPON)
    private OrderSubmitHandlerChain storeCouponChain;

    @Resource(name = OrderSubmitConfig.OrderHandlerChain.FREIGHT_PROMOTION)
    private OrderSubmitHandlerChain freightPromotionHandlerChain;

    @Resource(name = OrderSubmitConfig.OrderHandlerChain.PLATFORM_PROMOTION)
    private OrderSubmitHandlerChain platformPromotionHandlerChain;

    @Resource(name = OrderSubmitConfig.OrderHandlerChain.ORDER_SUMMARY)
    private OrderSubmitHandlerChain orderSummaryHandlerChain;

    @Resource(name = OrderSubmitConfig.OrderHandlerChain.ORDER_SUBMIT)
    private OrderSubmitHandlerChain orderSubmitHandlerChain;

    @Resource(name = OrderSubmitConfig.OrderHandlerChain.ORDER_AMOUNT)
    private OrderSubmitHandlerChain orderAmountHandlerChain;

    @Autowired
    private OrderRequestHandler cartInitHelper;

    @Autowired
    private PromotionServiceFacade promotionServiceFacade;

    @Autowired
    private GoodsPromotionPattern goodsPromotionPattern;

    @Autowired
    private StorePromotionPattern storePromotionPattern;

    @Autowired
    private StoreCouponPattern storeCouponPattern;

    @Autowired
    private FreightPromotionPattern freightPromotionPattern;

    @Autowired
    private PlatformPromotionPattern platformPromotionPattern;

    @Autowired
    private GoodsSkuServiceFacade goodsSkuServiceFacade;

    @Autowired
    private DistLockSservice distLockSservice;

    @Autowired
    private OrderSubmitActionChain orderSubmitActionChain;

    @Override
    @ContextOperation
    public GoodsSplitDTO loadInitPage(BuyerBO buyerBO, PageInitPO pageInitPO) {
        if (!pageInitPO.cartMode() && !pageInitPO.validGoodsMode()) {
            throw new GlobalException(StoreOrderExceptionEnum.PARAM_NOT_VALID);
        }

        //step 1 : 构建订单请求
        OrderRequest orderRequest = cartInitHelper.loadOrderRequest(buyerBO.getMemberId(), pageInitPO);

        //step 2 : 执行页面初始化链条
        OrderSubmitContext orderContext = pageInitChain.process(orderRequest);

        //step 3 : 构建页面返回值
        return GoodsSplitDTOUtil.buildResult(orderContext);
    }

    @Override
    @ContextOperation
    public List<PromotionInfoDTO> loadGoodsPromotions(BuyerBO buyerBO, Long goodsId, Long goodsNum) {
        //step 1 : 查询商品信息
        GoodsSkuDTO sku = goodsSkuServiceFacade.getGoodsSku(goodsId);
        if (sku == null) {
            log.warn("goods {} dose not exist", goodsId);
            return Lists.newArrayList();
        }
        if (goodsNum > sku.getStockNumber()) {
            log.warn("goods {} with num:{} exceeded the storage {}", goodsId, goodsNum, sku.getStockNumber());
            return Lists.newArrayList();
        }
        StoreOrderGoodsBO orderGoodsBO = new StoreOrderGoodsBO();
        orderGoodsBO.setGoods(sku);
        orderGoodsBO.setGoodsNum(goodsNum);
        orderGoodsBO.updatePrice();

        //step 2 : 查询商品所有营销活动
        List<PromotionDTO> promotionDTOS = promotionServiceFacade.querySkuPromotions(goodsId);
        if (CollectionUtils.isEmpty(promotionDTOS)) {
            return Lists.newArrayList();
        }

        //step 3 : 匹配当前商品可用的单品优惠活动
        List<PromotionDTO> matchPromotions = new ArrayList<>();
        for (PromotionDTO promotionDTO : promotionDTOS) {
            if (goodsPromotionPattern.match(orderGoodsBO, promotionDTO)) {
                matchPromotions.add(promotionDTO);
            }
        }
        if (CollectionUtils.isEmpty(matchPromotions)) {
            return Lists.newArrayList();
        }

        //step 4 : 组装数据
        List<PromotionInfoDTO> promotionInfoDTOS = new ArrayList<>();
        for (PromotionDTO promotionDTO : matchPromotions) {
            PromotionInfoDTO promotionInfoDTO = GoodsSplitDTOUtil.buildPromotionInfo(promotionDTO);
            promotionInfoDTOS.add(promotionInfoDTO);
        }

        //step 5 : 优惠活动排序
        Collections.sort(promotionDTOS, PromotionComparator.getInstance());

        return promotionInfoDTOS;
    }

    @Override
    @ContextOperation
    public List<PromotionInfoDTO> storePromotions(BuyerBO buyerBO, OrderRequestStore storeRequest) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.getOrders().add(storeRequest);
        OrderSubmitContext orderContext = storePromotionChain.process(orderRequest);
        StoreOrderBO orderRequestStore = new ArrayList<>(orderContext.getStoreOrderMap().values()).get(0);

        List<PromotionDTO> promotionDTOS = promotionServiceFacade.queryStorePromotions(storeRequest.getStoreId());
        if (CollectionUtils.isEmpty(promotionDTOS)) {
            return Collections.emptyList();
        }

        //step 3 : 匹配当前请求可用的单品优惠活动
        List<PromotionDTO> matchPromotions = new ArrayList<>();
        for (PromotionDTO promotionDTO : promotionDTOS) {
            if (storePromotionPattern.match(orderRequestStore, promotionDTO)) {
                BigDecimal promotionDiscount = PromotionDiscountContext.get().get(promotionDTO.getId());
                promotionDTO.setDiscountValue(promotionDiscount);
                matchPromotions.add(promotionDTO);
            }
        }
        //step 4 : 优惠活动排序
        Collections.sort(matchPromotions, PromotionComparator.getInstance());

        //step 5 : 组装数据
        List<PromotionInfoDTO> promotionInfoDTOS = new ArrayList<>();
        for (PromotionDTO promotionDTO : matchPromotions) {
            PromotionInfoDTO promotionInfoDTO = GoodsSplitDTOUtil.buildPromotionInfo(promotionDTO);
            promotionInfoDTOS.add(promotionInfoDTO);
        }
        return promotionInfoDTOS;
    }

    @Override
    @ContextOperation
    public PromotionListDTO loadStoreCoupons(BuyerBO buyerBO, OrderRequestStore storeRequest) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.getOrders().add(storeRequest);
        OrderSubmitContext orderContext = storeCouponChain.process(orderRequest);
        StoreOrderBO orderRequestStore = new ArrayList<>(orderContext.getStoreOrderMap().values()).get(0);

        List<PromotionDTO> promotionDTOS = promotionServiceFacade.queryStoreCoupons(buyerBO.getMemberId(), storeRequest.getStoreId());
        if (CollectionUtils.isEmpty(promotionDTOS)) {
            return new PromotionListDTO();
        }


        //step 3 : 匹配当前请求可用的单品优惠活动
        List<PromotionDTO> matchPromotions = new ArrayList<>();
        List<PromotionDTO> unMatchPromotions = new ArrayList<>();
        for (PromotionDTO promotionDTO : promotionDTOS) {
            if (storeCouponPattern.match(orderRequestStore, promotionDTO)) {
                BigDecimal promotionDiscount = PromotionDiscountContext.get().get(promotionDTO.getId());
                promotionDTO.setDiscountValue(promotionDiscount);
                matchPromotions.add(promotionDTO);
            } else {
                unMatchPromotions.add(promotionDTO);
            }
        }

        return buildResult(matchPromotions, unMatchPromotions);
    }

    private PromotionListDTO buildResult(List<PromotionDTO> matchPromotions, List<PromotionDTO> unMatchPromotions) {
        PromotionListDTO result = new PromotionListDTO();
        Collections.sort(matchPromotions, PromotionComparator.getInstance());

        List<PromotionInfoDTO> matchPromotionsDTOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(matchPromotions)) {
            for (PromotionDTO promotionDTO : matchPromotions) {
                PromotionInfoDTO promotionInfoDTO = GoodsSplitDTOUtil.buildPromotionInfo(promotionDTO);
                matchPromotionsDTOS.add(promotionInfoDTO);
            }
        }

        List<PromotionInfoDTO> unMatchPromotionsDTOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(unMatchPromotions)) {
            for (PromotionDTO promotionDTO : unMatchPromotions) {
                PromotionInfoDTO promotionInfoDTO = GoodsSplitDTOUtil.buildPromotionInfo(promotionDTO);
                unMatchPromotionsDTOS.add(promotionInfoDTO);
            }
        }

        result.setAvailableList(matchPromotionsDTOS);
        result.setUnavailableList(unMatchPromotionsDTOS);
        return result;
    }

    @Override
    @ContextOperation
    public List<PromotionInfoDTO> loadFreightPromotions(BuyerBO buyerBO, OrderRequestStore storeRequest) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.getOrders().add(storeRequest);
        OrderSubmitContext orderContext = freightPromotionHandlerChain.process(orderRequest);
        StoreOrderBO orderRequestStore = new ArrayList<>(orderContext.getStoreOrderMap().values()).get(0);

        List<PromotionDTO> promotionDTOS = promotionServiceFacade.queryFreightPromotions(orderRequestStore.getStore().getId());
        if (CollectionUtils.isEmpty(promotionDTOS)) {
            return Lists.newArrayList();
        }

        //step 3 : 匹配当前请求可用的包邮优惠活动
        List<PromotionDTO> matchPromotions = new ArrayList<>();
        for (PromotionDTO promotionDTO : promotionDTOS) {
            if (freightPromotionPattern.match(orderRequestStore, promotionDTO)) {
                matchPromotions.add(promotionDTO);
            }
        }
        if (CollectionUtils.isEmpty(matchPromotions)) {
            return Lists.newArrayList();
        }

        //step 4 : 组装数据
        List<PromotionInfoDTO> promotionInfoDTOS = new ArrayList<>();
        for (PromotionDTO promotionDTO : matchPromotions) {
            PromotionInfoDTO promotionInfoDTO = GoodsSplitDTOUtil.buildPromotionInfo(promotionDTO);
            promotionInfoDTOS.add(promotionInfoDTO);
        }

        //step 5 : 优惠活动排序
        Collections.sort(promotionDTOS, PromotionComparator.getInstance());

        return promotionInfoDTOS;
    }

    @Override
    @ContextOperation
    public OrderAmountDTO caculateOrderAmount(BuyerBO buyerBO, OrderRequestStore storeRequest) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.getOrders().add(storeRequest);
        OrderSubmitContext orderContext = orderAmountHandlerChain.process(orderRequest);
        StoreOrderBO orderRequestStore = new ArrayList<>(orderContext.getStoreOrderMap().values()).get(0);
        OrderAmountDTO orderAmountDTO = new OrderAmountDTO();
        orderAmountDTO.setFreightAmount(orderRequestStore.getGoodsFreight().subtract(orderRequestStore.getFreightDiscount()));
        orderAmountDTO.setPayPrice(orderRequestStore.getPayPrice());
        return orderAmountDTO;
    }

    @Override
    @ContextOperation
    public PromotionListDTO loadPlatformPromotions(BuyerBO buyerBO, OrderRequest orderRequest) {
        //step 1 : 计算单品优惠、店铺优惠、包邮优惠数据
        OrderSubmitContext orderContext = platformPromotionHandlerChain.process(orderRequest);

        //step 2 : 查询可用平台优惠券
        List<PromotionDTO> promotionDTOS = promotionServiceFacade.queryPlatformPromotions(buyerBO.getMemberId());
        if (CollectionUtils.isEmpty(promotionDTOS)) {
            return new PromotionListDTO();
        }

        List<PromotionDTO> matchPromotions = new ArrayList<>();
        List<PromotionDTO> unMatchPromotions = new ArrayList<>();
        for (PromotionDTO promotionDTO : promotionDTOS) {
            if (platformPromotionPattern.match(orderContext, promotionDTO)) {
                matchPromotions.add(promotionDTO);
            } else {
                unMatchPromotions.add(promotionDTO);
            }
        }
        return buildResult(matchPromotions, unMatchPromotions);
    }

    @Override
    @ContextOperation
    public OrderSummaryDTO getOrderSummary(BuyerBO buyerBO, OrderRequest orderRequest) {
        OrderSubmitContext orderContext = orderSummaryHandlerChain.process(orderRequest);
        return OrderSummaryUtil.buildSummary(orderContext);
    }

    @Override
    @ContextOperation
    @Transactional(rollbackFor = Exception.class)
    public OrderResult submitOrder(BuyerBO buyerBO, OrderRequest orderRequest) {

        LockResult memberLock = null;
        try {
            memberLock = distLockSservice.tryLock("trade-order-submit-", buyerBO.getMemberId());
            if (!memberLock.isSuccess()) {
                throw new GlobalException(ORDER_SUBMIT_DUPLICATE);
            }

            //使用责任链计算所有的商品、优惠等等信息
            OrderSubmitContext orderContext = orderSubmitHandlerChain.process(orderRequest);
            OrderSummaryDTO orderSummary = OrderSummaryUtil.buildSummary(orderContext);
            OrderSummaryContext.set(orderSummary);

            //提交经过校验的信息
            Long payId = orderSubmitActionChain.submitOrder(orderContext, orderRequest);
            return OrderResult.buildResult(payId);
        } finally {
            distLockSservice.unlock(memberLock);
        }
    }
}