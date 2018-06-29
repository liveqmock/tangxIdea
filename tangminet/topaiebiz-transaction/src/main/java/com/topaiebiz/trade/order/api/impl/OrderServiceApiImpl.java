package com.topaiebiz.trade.order.api.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.enumdata.PayMethodEnum;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.topaiebiz.trade.api.order.OrderServiceApi;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.dto.order.*;
import com.topaiebiz.trade.dto.order.openapi.APIOrderDetailDTO;
import com.topaiebiz.trade.dto.order.openapi.OrderExpressDTO;
import com.topaiebiz.trade.dto.order.params.OrderQueryParams;
import com.topaiebiz.trade.dto.settlement.SettlementOrderDetailDTO;
import com.topaiebiz.trade.order.dao.OrderCustomsResultDao;
import com.topaiebiz.trade.order.dao.OrderDao;
import com.topaiebiz.trade.order.dao.OrderDetailDao;
import com.topaiebiz.trade.order.service.CustomerOrderServie;
import com.topaiebiz.trade.order.service.StoreOrderService;
import com.topaiebiz.trade.order.util.OrderHelper;
import com.topaiebiz.trade.order.util.OrderPageForApiUtil;
import com.topaiebiz.trade.order.util.OrdersQueryUtil;
import com.topaiebiz.trade.refund.helper.RefundQueryUtil;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderAddressEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderCustomsResultEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description 订单
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/9 13:08
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class OrderServiceApiImpl implements OrderServiceApi {

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private OrdersQueryUtil ordersQueryUtil;

    @Autowired
    private OrderHelper orderHelper;

    @Autowired
    private OrderCustomsResultDao orderCustomsResultDao;

    @Autowired
    private CustomerOrderServie customerOrderServie;

    @Autowired
    private RefundQueryUtil refundQueryUtil;

    @Override
    public boolean orderEvaluated(Long orderId) {
        if (null == orderId) {
            log.error("----------orderDetailEvaluated : the params is null!");
            return false;
        }
        EntityWrapper<OrderEntity> orderDetailWrapper = new EntityWrapper<>();
        orderDetailWrapper.eq("id", orderId);
        orderDetailWrapper.gt("orderState", OrderStatusEnum.PENDING_RECEIVED.getCode());
        orderDetailWrapper.lt("orderState", OrderStatusEnum.ORDER_CLOSE.getCode());
        orderDetailWrapper.eq("commentFlag", Constants.Order.COMMENT_NO);
        orderDetailWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.cleanInit();
        orderEntity.setCommentFlag(Constants.Order.COMMENT_YES);
        orderEntity.setCommentDate(new Date());

        boolean result = orderDao.update(orderEntity, orderDetailWrapper) > 0;
        if (result) {
            log.info("----------order:{} add has been commented", orderId);
        } else {
            log.warn("----------order comment fail!");
        }
        return result;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean orderShip(OrderExpressDTO orderExpressDTO) {
        return storeOrderService.orderDeliveryApi(orderExpressDTO);
    }

    @Override
    public OrderDetailDTO orderDetail(Long orderId) {
        if (null == orderId) {
            return null;
        }
        OrderEntity orderEntity = ordersQueryUtil.queryOrder(orderId);
        return orderHelper.buildOrderDetail(orderEntity);
    }

    @Override
    public Map<Long, GuiderOrderDetailDTO> guiderOrderDetailGroupById(List<Long> orderIds) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return Collections.emptyMap();
        }
        List<OrderEntity> orderEntities = ordersQueryUtil.queryOrders(orderIds);
        if (CollectionUtils.isEmpty(orderEntities)) {
            return Collections.emptyMap();
        }
        // 商品信息
        Map<Long, List<OrderDetailEntity>> orderDetailMap = ordersQueryUtil.queryDetailsByOrderIds(orderIds);
        // 地址信息
        Map<Long, OrderAddressEntity> orderAddressMap = ordersQueryUtil.queryOrderReceiver(orderIds);
        // 支付信息
        Map<Long, OrderPayEntity> orderPayMap = ordersQueryUtil.queryOrderPayMap(orderEntities.stream().map(OrderEntity::getPayId).collect(Collectors.toList()));
        // 退款金额信息
        Map<Long, BigDecimal> refundAmountMap = refundQueryUtil.queryRefundAmountGroupByOrderId(orderIds);

        Map<Long, GuiderOrderDetailDTO> resultMap = new HashMap<>(orderEntities.size());
        for (OrderEntity orderEntity : orderEntities) {
            Long orderId = orderEntity.getId();

            GuiderOrderDetailDTO orderDetailDTO = new GuiderOrderDetailDTO();
            BeanCopyUtil.copy(orderEntity, orderDetailDTO);

            // 支付相关
            String payType = orderEntity.getPayType();
            if (StringUtils.isNotBlank(payType) && !PayMethodEnum.PREDEPOSIT.getName().equals(payType)) {
                OrderPayEntity orderPayEntity = orderPayMap.get(orderEntity.getPayId());
                orderDetailDTO.setThirdPaymentAmount(orderDetailDTO.getPayPrice().subtract(orderDetailDTO.getBalance()).subtract(orderDetailDTO.getCardPrice()).subtract(orderDetailDTO.getScore()));
                // 第三方支付号
                orderDetailDTO.setOuterPaySn(null != orderPayEntity ? orderPayEntity.getOuterPaySn() : null);
            }
            // 查询订单明细
            orderDetailDTO.setOrderGoodsDTOS(orderHelper.buildOrderDetailGoodsDTO(orderDetailMap.get(orderId)));

            // 查询订单收货地址
            orderDetailDTO.setOrderAddressDTO(orderHelper.buildOrderAddressDTO(orderAddressMap.get(orderId)));

            // 退款金额
            orderDetailDTO.setRefundPrice(null == refundAmountMap.get(orderId) ? BigDecimal.ZERO : refundAmountMap.get(orderId));

            resultMap.put(orderId, orderDetailDTO);
        }
        return resultMap;
    }

    @Override
    public PushOrderParamsDTO queryOrdersByPayId(Long payId) {
        if (null == payId) {
            return null;
        }
        List<OrderEntity> orderEntities = ordersQueryUtil.queryOrdersByPayId(payId);
        if (CollectionUtils.isEmpty(orderEntities)) {
            return null;
        }
        PushOrderParamsDTO pushOrderParamsDTO = new PushOrderParamsDTO();
        pushOrderParamsDTO.setPayId(payId);
        Map<Long, Set<Long>> orderIdsMap = new HashMap<>();

        // 如果为海淘订单，则一个支付号只存在一个订单
        OrderEntity orderEntity = orderEntities.get(0);
        if (orderEntity.getHaitao().equals(OrderConstants.HaitaoFlag.YES)) {
            pushOrderParamsDTO.setHaiTao(true);
            Set<Long> orderIdSet = new HashSet<>();
            orderIdSet.add(orderEntity.getId());
            orderIdsMap.put(orderEntity.getStoreId(), orderIdSet);
        } else {
            pushOrderParamsDTO.setHaiTao(false);
            for (OrderEntity entity : orderEntities) {
                Long storeId = entity.getStoreId();
                Set<Long> set = orderIdsMap.get(storeId);
                if (CollectionUtils.isEmpty(set)) {
                    set = new HashSet<>();
                }
                set.add(entity.getId());
                orderIdsMap.put(storeId, set);
            }
        }
        pushOrderParamsDTO.setOrderIds(orderIdsMap);
        return pushOrderParamsDTO;
    }

    @Override
    public OrderAddressDTO queryOrderAddressById(Long orderId) {
        return ordersQueryUtil.queryOrderAddress(orderId);
    }

    @Override
    public OrderPayDTO queryOrderPayInfoById(Long payId) {
        return orderHelper.buildPayInfo(payId);
    }

    @Override
    public boolean saveOrderReportCustomsResult(OrderCustomsResultDTO orderCustomsResultDTO) {
        if (null == orderCustomsResultDTO) {
            return false;
        }
        log.warn(">>>>>>>>>>save order report customs result, params:{}", JSON.toJSONString(orderCustomsResultDTO));
        OrderCustomsResultEntity orderCustomsResultEntity = new OrderCustomsResultEntity();
        BeanCopyUtil.copy(orderCustomsResultDTO, orderCustomsResultEntity);
        return orderCustomsResultDao.insert(orderCustomsResultEntity) > 0;
    }

    @Override
    public List<OrderDTO> queryFinishedOrders(Long storeId, Date start, Date end, Long startOrderId, Integer pageSize) {
        EntityWrapper<OrderEntity> cond = new EntityWrapper<>();
        cond.eq("storeId", storeId);
        cond.eq("orderState", OrderStatusEnum.ORDER_COMPLETION.getCode());
        cond.ge("completeTime", start);
        cond.lt("completeTime", end);
        cond.gt("id", startOrderId);
        cond.orderBy("id", true);
        RowBounds page = new RowBounds(0, pageSize);
        List<OrderEntity> orderEntities = orderDao.selectPage(page, cond);
        if (CollectionUtils.isEmpty(orderEntities)) {
            return Collections.emptyList();
        }
        return PageDataUtil.copyList(orderEntities, OrderDTO.class);
    }

    @Override
    public Map<Long, List<SettlementOrderDetailDTO>> querySettlementOrderDetails(List<Long> orderIds) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return Collections.emptyMap();
        }
        EntityWrapper<OrderDetailEntity> cond = new EntityWrapper<>();
        cond.in("orderId", orderIds);
        List<OrderDetailEntity> details = orderDetailDao.selectList(cond);
        if (CollectionUtils.isEmpty(details)) {
            return Collections.emptyMap();
        }

        List<SettlementOrderDetailDTO> detailDTOS = BeanCopyUtil.copyList(details, SettlementOrderDetailDTO.class);

        //根据订单ID分组
        Map<Long, List<SettlementOrderDetailDTO>> detailMap = new HashMap<>();
        for (SettlementOrderDetailDTO orderDetailDTO : detailDTOS) {
            List<SettlementOrderDetailDTO> orderDetails = detailMap.get(orderDetailDTO.getOrderId());
            if (orderDetails == null) {
                orderDetails = new ArrayList<>();
                detailMap.put(orderDetailDTO.getOrderId(), orderDetails);
            }
            orderDetails.add(orderDetailDTO);
        }
        return detailMap;
    }

    @Override
    public PageInfo<APIOrderDetailDTO> queryOrderPageOpenApi(OrderQueryParams orderQueryParams) {
        log.info(">>>>>>>>>>query order'page params:{}", JSON.toJSONString(orderQueryParams));
        PageInfo<APIOrderDetailDTO> resultPageInfo = new PageInfo<>();
        // 1：查询订单
        PageInfo<OrderDTO> pageInfo = ordersQueryUtil.queryOrdersFromAPI(orderQueryParams);

        List<OrderDTO> orderDTOS = pageInfo.getRecords();
        if (CollectionUtils.isEmpty(orderDTOS)) {
            return resultPageInfo;
        }
        List<Long> orderIds = new ArrayList<>(orderDTOS.size());
        List<Long> payIds = new ArrayList<>(orderDTOS.size());
        for (OrderDTO orderDTO : orderDTOS) {
            orderIds.add(orderDTO.getId());
            if (null != orderDTO.getPayTime()) {
                payIds.add(orderDTO.getPayId());
            }
        }

        // 2:查询订单得商品明细
        Map<Long, List<OrderGoodsDTO>> orderDetailsMap = ordersQueryUtil.queryOrderGoodsInfoFromAPI(orderIds);

        // 3：查询订单得收货信息
        Map<Long, OrderAddressDTO> orderAddressMap = ordersQueryUtil.queryOrderAddressFromAPI(orderIds);

        // 4：查询订单得支付信息
        Map<Long, OrderPayDTO> orderPayMap = ordersQueryUtil.queryOrderPayInfoFromAPI(payIds);

        // 5：拼装结果
        resultPageInfo.setRecords(OrderPageForApiUtil.buildOrderPage(orderDTOS, orderDetailsMap, orderAddressMap, orderPayMap));
        resultPageInfo.setCurrentPageSize(pageInfo.getCurrentPageSize());
        resultPageInfo.setPageNo(pageInfo.getPageNo());
        resultPageInfo.setPageSize(pageInfo.getPageSize());
        resultPageInfo.setTotalCount(pageInfo.getTotalCount());
        resultPageInfo.setTotalPage(pageInfo.getTotalPage());
        return resultPageInfo;
    }

    @Override
    public Integer queryMemberValidOrderCounts(Long memberId) {
        if (null == memberId) {
            return null;
        }
        return customerOrderServie.queryMemberVaildOrderCounts(memberId);
    }
}
