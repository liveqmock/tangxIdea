package com.topaiebiz.trade.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.enumdata.PayMethodEnum;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.ExportUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.aop.JedisOperation;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.util.SecurityContextUtils;
import com.topaiebiz.system.util.SystemUserType;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.dto.order.OrderAddressDTO;
import com.topaiebiz.trade.dto.order.OrderDeliveryDTO;
import com.topaiebiz.trade.dto.order.openapi.OrderExpressDTO;
import com.topaiebiz.trade.order.dao.OrderDao;
import com.topaiebiz.trade.order.dao.OrderDetailDao;
import com.topaiebiz.trade.order.dao.OrderPayDao;
import com.topaiebiz.trade.order.dao.OrderRemarkDao;
import com.topaiebiz.trade.order.dto.store.*;
import com.topaiebiz.trade.order.dto.store.export.GoodExpressDTO;
import com.topaiebiz.trade.order.dto.store.export.OrderExportDTO;
import com.topaiebiz.trade.order.dto.store.export.OrderRefundPriceDTO;
import com.topaiebiz.trade.order.dto.store.statistics.MemberOrderCountDTO;
import com.topaiebiz.trade.order.dto.store.statistics.OrderStatisticsDTO;
import com.topaiebiz.trade.order.dto.store.statistics.TodayStatisticsDTO;
import com.topaiebiz.trade.order.exception.OrderExceptionEnum;
import com.topaiebiz.trade.order.exception.PaymentExceptionEnum;
import com.topaiebiz.trade.order.facade.ExpressageServiceFacade;
import com.topaiebiz.trade.order.service.StoreOrderService;
import com.topaiebiz.trade.order.util.OrderHelper;
import com.topaiebiz.trade.order.util.OrdersQueryUtil;
import com.topaiebiz.trade.refund.dao.RefundOrderDao;
import com.topaiebiz.trade.refund.enumdata.RefundOrderStateEnum;
import com.topaiebiz.trade.refund.helper.RefundOrderHelper;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderAddressEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderRemarkEntity;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import com.topaiebiz.transport.dto.LogisticsDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description 商家订单service 实现类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/20 13:28
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Service
public class StoreOrderServiceImpl implements StoreOrderService {

    @Autowired
    private OrderPayDao orderPayDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderHelper orderHelper;

    @Autowired
    private OrdersQueryUtil ordersQueryUtil;

    @Autowired
    private RefundOrderDao refundOrderDao;

    @Autowired
    private DistLockSservice distLockSservice;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ExpressageServiceFacade expressageServiceFacade;

    @Autowired
    private RefundOrderHelper refundOrderHelper;

    @Autowired
    private OrderRemarkDao orderRemarkDao;

    @Override
    public PageInfo<StoreOrderPageDTO> queryStoreOrders(StoreOrderPageParamsDTO pageParamsDTO) {
        //1：实例化 分页对象 和 查询结果实体类
        Page<OrderEntity> orderEntityPage = PageDataUtil.buildPageParam(pageParamsDTO.getPagePO());
        PageInfo<StoreOrderPageDTO> pageInfo = PageDataUtil.copyPageInfo(orderEntityPage, StoreOrderPageDTO.class);

        //2: 校验登录对象
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        if (null == currentUserDto || !SystemUserType.MERCHANT.equals(currentUserDto.getType())) {
            log.warn(">>>>>>>>>query store order page fail, no authority!");
            return pageInfo;
        }
        pageParamsDTO.setStoreId(currentUserDto.getStoreId());

        //3：查询订单
        List<OrderEntity> orderEntities = ordersQueryUtil.queryStoreOrders(orderEntityPage, pageParamsDTO);
        if (CollectionUtils.isEmpty(orderEntities)) {
            return pageInfo;
        }

        List<Long> orderIds = orderEntities.stream().map(OrderEntity::getId).collect(Collectors.toList());
        //4：查询订单明细
        Map<Long, List<OrderDetailEntity>> orderDetailMap = ordersQueryUtil.queryDetailsByOrderIds(orderIds);
        if (MapUtils.isEmpty(orderDetailMap)) {
            log.error(">>>>>>>>>>>>query order details fail!, params:{}", JSON.toJSONString(orderIds));
            return pageInfo;
        }

        //5：查询收货人信息
        Map<Long, OrderAddressEntity> orderReceiverDTOMap = ordersQueryUtil.queryOrderReceiver(orderIds);
        if (MapUtils.isEmpty(orderReceiverDTOMap)) {
            log.error(">>>>>>>>>>>>query order address info fail!, params:{}", JSON.toJSONString(orderIds));
            return pageInfo;
        }

        //6：拼装查询结果
        List<StoreOrderPageDTO> storeOrderPageDTOS = new ArrayList<>(orderEntities.size());
        orderEntities.forEach(orderEntity -> {
            StoreOrderPageDTO storeOrderPageDTO = new StoreOrderPageDTO();
            BeanCopyUtil.copy(orderEntity, storeOrderPageDTO);
            // 订单明细
            storeOrderPageDTO.setOrderPageDetailDTOS(orderHelper.buildOrderDetailInPage(orderDetailMap.get(orderEntity.getId())));
            // 订单收货信息
            orderHelper.setReceiverInfo(storeOrderPageDTO, orderReceiverDTOMap.get(orderEntity.getId()));
            // 最新的商家备注
            storeOrderPageDTO.setLatestRemarks(orderHelper.getLatestRemarks(orderEntity.getId()));
            storeOrderPageDTOS.add(storeOrderPageDTO);
        });
        pageInfo = PageDataUtil.setPageInfo(pageInfo, orderEntityPage);
        pageInfo.setRecords(storeOrderPageDTOS);
        return pageInfo;
    }

    @Override
    public StoreOrderDetailDTO queryOrderDetail(Long orderId) {

        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        StoreOrderDetailDTO storeOrderDetailDTO = new StoreOrderDetailDTO();
        //1：订单数据
        OrderEntity orderEntity = ordersQueryUtil.queryStoreOrder(orderId, currentUserDto.getStoreId());

        //2：订单明细数据
        List<OrderDetailEntity> orderDetailEntities = ordersQueryUtil.queryDetailsByOrderId(orderId);

        //3：订单地址数据
        OrderAddressEntity orderAddressEntity = ordersQueryUtil.queryOrderAddressEntity(orderId);

        //4：订单支付数据
        if (OrderStatusEnum.UNPAY.getCode() < orderEntity.getOrderState()) {
            OrderPayEntity orderPayEntity = ordersQueryUtil.queryOrderPay(orderEntity.getPayId());
            storeOrderDetailDTO.setOuterPaySn(orderPayEntity.getOuterPaySn());
        }

        //5：拼装数据
        BeanCopyUtil.copy(orderEntity, storeOrderDetailDTO);
        // 订单关闭/取消时间
        if (OrderStatusEnum.ORDER_CLOSE.getCode().equals(orderEntity.getOrderState()) || OrderStatusEnum.ORDER_CANCELLATION.getCode().equals(orderEntity.getOrderState())) {
            storeOrderDetailDTO.setCloseTime(orderEntity.getLastModifiedTime());
        }
        // 计算第三方支付
        storeOrderDetailDTO.resetThirdPaymentAmount();
        // 拼装用户和商家备注列表
        storeOrderDetailDTO.setRemarkDTOS(ordersQueryUtil.queryOrderRemarks(orderEntity));
        // 获取物流信息
        OrderDetailEntity orderDetailEntity = orderDetailEntities.get(0);
        storeOrderDetailDTO.setExpressComId(orderDetailEntity.getExpressComId());
        storeOrderDetailDTO.setExpressComName(orderDetailEntity.getExpressComName());
        storeOrderDetailDTO.setExpressNo(orderDetailEntity.getExpressNo());
        storeOrderDetailDTO.setDeliveryTime(orderDetailEntity.getShipmentTime());

        //订单明细
        Long goodsTotalNum = 0L;
        BigDecimal goodsTotalPrice = BigDecimal.ZERO;
        List<StoreOrderGoodsDTO> storeOrderGoodsDTOS = new ArrayList<>();
        for (OrderDetailEntity detailEntity : orderDetailEntities) {
            goodsTotalNum = goodsTotalNum + detailEntity.getGoodsNum();
            goodsTotalPrice = goodsTotalPrice.add(detailEntity.getTotalPrice());
            StoreOrderGoodsDTO storeOrderGoodsDTO = new StoreOrderGoodsDTO();
            BeanCopyUtil.copy(detailEntity, storeOrderGoodsDTO);
            storeOrderGoodsDTOS.add(storeOrderGoodsDTO);
        }
        storeOrderDetailDTO.setStoreOrderGoodsDTOS(storeOrderGoodsDTOS);
        storeOrderDetailDTO.setGoodsTotalNum(goodsTotalNum);
        storeOrderDetailDTO.setGoodsTotalPrice(goodsTotalPrice);

        //收货人信息
        storeOrderDetailDTO.setReceiverName(orderAddressEntity.getName());
        storeOrderDetailDTO.setAddress(StringUtils.join(orderAddressEntity.getProvince(), ".", orderAddressEntity.getCity(), ".", orderAddressEntity.getCounty(), ".", orderAddressEntity.getAddress()));
        storeOrderDetailDTO.setReceiverTelephone(orderAddressEntity.getTelephone());
        storeOrderDetailDTO.setReceiverTime(orderEntity.getReceiveTime());

        //海淘订单，实名制信息
        if (OrderConstants.HaitaoFlag.YES.equals(orderEntity.getHaitao())) {
            storeOrderDetailDTO.setBuyerName(orderAddressEntity.getBuyerName());
            storeOrderDetailDTO.setIdNum(orderAddressEntity.getIdNum());
        }
        return storeOrderDetailDTO;
    }

    @Override
    public List<ShipGoodsDTO> queryShipGoods(Long orderId) {
        List<ShipGoodsDTO> shipGoodsDTOS = new ArrayList<>();
        List<OrderDetailEntity> orderDetailEntities = ordersQueryUtil.queryDetailsByOrderState(orderId, null, OrderStatusEnum.PENDING_DELIVERY.getCode());
        orderDetailEntities.forEach(orderDetailEntity -> {
            ShipGoodsDTO shipGoodsDTO = new ShipGoodsDTO();
            shipGoodsDTO.setGoodName(orderDetailEntity.getName());
            shipGoodsDTO.setOrderDetailId(orderDetailEntity.getId());
            shipGoodsDTOS.add(shipGoodsDTO);
        });
        return shipGoodsDTOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean shipOrder(OrderDeliveryDTO orderDeliveryDTO) {
        orderDeliveryDTO.setMemberId(SecurityContextUtils.getCurrentUserDto().getId());
        orderDeliveryDTO.setStoreId(SecurityContextUtils.getCurrentUserDto().getStoreId());
        return orderDelivery(orderDeliveryDTO, false);
    }

    @Override
    public Boolean orderDeliveryApi(OrderExpressDTO orderExpressDTO) {
        // 2：参数校验
        if (StringUtils.isBlank(orderExpressDTO.getExpressNo())) {
            throw new GlobalException(OrderExceptionEnum.EXPRESS_NO_CANT_BE_NULL);
        }
        if (StringUtils.isBlank(orderExpressDTO.getExpressCompanyCode())) {
            throw new GlobalException(OrderExceptionEnum.EXPRESS_COMPANY_CODE_CANT_BE_NULL);
        }
        if (null == orderExpressDTO.getMmgOrderId()) {
            throw new GlobalException(OrderExceptionEnum.ORDER_ID_CANT_BE_NUL);
        }

        OrderDeliveryDTO orderDeliveryDTO = new OrderDeliveryDTO();
        LogisticsDto logisticsDto = expressageServiceFacade.getLogistics(orderExpressDTO.getExpressCompanyCode());
        if (null == logisticsDto) {
            throw new GlobalException(OrderExceptionEnum.EXPRESS_COMPANY_CANT_BE_FOUND);
        }

        orderDeliveryDTO.setOrderId(orderExpressDTO.getMmgOrderId());
        orderDeliveryDTO.setStoreId(orderExpressDTO.getStoreId());
        orderDeliveryDTO.setExpressComCode(logisticsDto.getExpressCompanyCode());
        orderDeliveryDTO.setExpressComName(logisticsDto.getComName());
        orderDeliveryDTO.setExpressComId(logisticsDto.getId());
        orderDeliveryDTO.setExpressNo(orderExpressDTO.getExpressNo());
        orderDeliveryDTO.setMemberId(Constants.Order.OPEN_API_USER_ID);
        return orderDelivery(orderDeliveryDTO, true);
    }

    private Boolean orderDelivery(OrderDeliveryDTO orderDeliveryDTO, Boolean isReqFromOpenAPI) {
        LockResult updateOrderLock = null;
        try {
            updateOrderLock = distLockSservice.tryLock(Constants.LockOperatons.ORDER_OPERATION_LOCK, orderDeliveryDTO.getOrderId());
            if (!updateOrderLock.isSuccess()) {
                throw new GlobalException(PaymentExceptionEnum.PAY_FAIL_ERROR);
            }
            log.info(">>>>>>>>>>order ship params:{}", JSON.toJSONString(orderDeliveryDTO));
            Long orderId = orderDeliveryDTO.getOrderId();
            Long memberId = orderDeliveryDTO.getMemberId();
            Date currentDate = new Date();

            OrderEntity orderEntity = ordersQueryUtil.queryStoreOrder(orderDeliveryDTO.getOrderId(), orderDeliveryDTO.getStoreId());
            if (orderEntity.getLockState().equals(Constants.OrderLockFlag.LOCK_YES)) {
                log.error("----------order:{} has been locked", orderId);
                throw new GlobalException(OrderExceptionEnum.ORDER_HAS_BEEN_LOCKED);
            }
            if (!orderEntity.getOrderState().equals(OrderStatusEnum.PENDING_DELIVERY.getCode())) {
                log.error("----------operation is not allowed，operate the order:{} to shipOrder", orderId);
                throw new GlobalException(OrderExceptionEnum.OPERATION_IS_NOT_ALLOWED);
            }

            LogisticsDto logisticsDto = expressageServiceFacade.getLogistics(orderDeliveryDTO.getExpressComId());
            if (null == logisticsDto) {
                throw new GlobalException(OrderExceptionEnum.EXPRESS_COMPANY_CANT_BE_FOUND);
            }

            EntityWrapper<OrderDetailEntity> updateWrapper = new EntityWrapper<>();
            updateWrapper.eq("orderId", orderDeliveryDTO.getOrderId());
            updateWrapper.eq("orderState", OrderStatusEnum.PENDING_DELIVERY.getCode());

            OrderDetailEntity updateEntity = new OrderDetailEntity();
            updateEntity.cleanInit();
            updateEntity.setExpressComId(orderDeliveryDTO.getExpressComId());
            updateEntity.setExpressComName(logisticsDto.getComName());
            updateEntity.setExpressNo(orderDeliveryDTO.getExpressNo());
            updateEntity.setOrderState(OrderStatusEnum.PENDING_RECEIVED.getCode());
            updateEntity.setShipmentTime(currentDate);
            updateEntity.setLastModifierId(memberId);
            updateEntity.setLastModifiedTime(currentDate);
            if (isReqFromOpenAPI) {
                updateEntity.setRefundState(OrderConstants.OrderRefundStatus.NO_REFUND);
            }
            // 批量修改
            if (orderDetailDao.update(updateEntity, updateWrapper) > 0) {
                OrderEntity update = new OrderEntity();
                update.cleanInit();
                update.setId(orderEntity.getId());
                update.setOrderState(OrderStatusEnum.PENDING_RECEIVED.getCode());
                update.setShipmentTime(currentDate);
                update.setLastModifiedTime(currentDate);
                update.setLastModifierId(memberId);
                if (isReqFromOpenAPI) {
                    update.setRefundState(OrderConstants.OrderRefundStatus.NO_REFUND);
                }
                if (orderDao.updateById(update) > 0) {
                    // 发送消息到快递100
                    expressageServiceFacade.sendExpress(logisticsDto.getId(), orderDeliveryDTO.getExpressNo());
                    log.info(">>>>>>>>>>orderId:{} push ship request to kuaidi-100 success", orderId);

                    if (isReqFromOpenAPI && OrderConstants.OrderRefundStatus.REFUNDING.equals(orderEntity.getRefundState())) {
                        refundOrderHelper.closeRefundWhenDelivery(orderId, memberId);
                    }
                    return true;
                }
                log.error(">>>>>>>>>>order:{} ship fail: no order data has been modified!", orderId);
                throw new GlobalException(OrderExceptionEnum.ORDER_SHIP_FAIL);
            }
            log.error(">>>>>>>>>>order:{} ship fail: no orderDetail data has been modified!", orderId);
            throw new GlobalException(OrderExceptionEnum.ORDER_SHIP_FAIL);
        } finally {
            distLockSservice.unlock(updateOrderLock);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateFreight(Long orderId, BigDecimal freight) {
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        if (null == currentUserDto) {
            return false;
        }
        Date currentDate = new Date();
        OrderEntity orderEntity = ordersQueryUtil.queryOrder(orderId);

        LockResult updateOrderLock = null;
        try {
            updateOrderLock = distLockSservice.tryLock(Constants.LockOperatons.TRADE_ORDER_PAY_, orderId);
            if (!updateOrderLock.isSuccess()) {
                throw new GlobalException(PaymentExceptionEnum.PAY_FAIL_ERROR);
            }

            if (!orderEntity.getOrderState().equals(OrderStatusEnum.UNPAY.getCode())) {
                throw new GlobalException(OrderExceptionEnum.ORDER_CANT_UPDATE_FREIGHT);
            }
            if (freight == null || freight.compareTo(BigDecimal.ZERO) < 0) {
                log.error("----------update order freight fail, the freight is illegal!");
                return false;
            }

            // 修改支付价格
            BigDecimal payPrice = orderEntity.getPayPrice().subtract(orderEntity.getActualFreight()).add(freight);

            // 更新订单表
            EntityWrapper<OrderEntity> orderEntityWrapper = new EntityWrapper<>();
            orderEntityWrapper.eq("id", orderId);
            orderEntityWrapper.eq("orderState", OrderStatusEnum.UNPAY.getCode());
            orderEntityWrapper.eq("storeId", currentUserDto.getStoreId());
            orderEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            OrderEntity orderUpdate = new OrderEntity();
            orderUpdate.cleanInit();
            orderUpdate.setActualFreight(freight);
            orderUpdate.setPayPrice(payPrice);
            orderUpdate.setLastModifierId(currentUserDto.getId());
            orderUpdate.setLastModifiedTime(currentDate);

            // 更新订单支付表
            EntityWrapper<OrderPayEntity> orderPayEntityWrapper = new EntityWrapper<>();
            orderPayEntityWrapper.eq("id", orderEntity.getPayId());
            orderPayEntityWrapper.eq("payState", OrderConstants.PayStatus.UNPAY);
            OrderPayEntity orderPayUpdate = new OrderPayEntity();
            orderPayUpdate.cleanInit();
            orderPayUpdate.setPayPrice(payPrice);
            orderPayUpdate.setLastModifierId(currentUserDto.getId());
            orderPayUpdate.setLastModifiedTime(currentDate);

            boolean result = orderDao.update(orderUpdate, orderEntityWrapper) > 0 && orderPayDao.update(orderPayUpdate, orderPayEntityWrapper) > 0;
            if (!result) {
                throw new GlobalException(OrderExceptionEnum.ORDER_CANT_UPDATE_FREIGHT);
            }
            return result;
        } finally {
            distLockSservice.unlock(updateOrderLock);
        }
    }

    @Override
    public OrderAddressDTO queryOrderAddress(Long orderId) {
        return ordersQueryUtil.queryOrderAddress(orderId);
    }

    @Override
    public OrderStatisticsDTO storeIndex() {
//        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
//        if (null == currentUserDto || null == currentUserDto.getStoreId()){
//            log.error("----------no login!");
//            return null;
//        }

        Long storeId = 961048929257181186L;
        int index = 1;
        Page<MemberOrderCountDTO> page = new Page<>(index, 200);

        List<Long> memberIds = new ArrayList<>();
        // 下单人数
        Integer orderCustomersNum;
        // 下单金额
        BigDecimal orderTotalAmount = BigDecimal.ZERO;
        // 首次进入店铺下单会员人数
        Integer newCustomersNum = 0;


        /*
        今日订单数量
         */
        do {
            page.setCurrent(index);
            // 今日订单数
            List<MemberOrderCountDTO> memberOrderCountDTOS = orderDao.queryTodayOrderGroupMember(page, storeId);
            if (CollectionUtils.isEmpty(memberOrderCountDTOS)) {
                break;
            }
            for (MemberOrderCountDTO memberOrderCountDTO : memberOrderCountDTOS) {
                orderTotalAmount = orderTotalAmount.add(memberOrderCountDTO.getOrderTotalAmount());
                memberIds.add(memberOrderCountDTO.getMemberId());
            }
            index++;
        } while (true);

        orderCustomersNum = memberIds.size();

        // 根据今日下单会员ID集合， 批量查询判断新会员的数量
//        EntityWrapper<OrderEntity> orderWrapper = new EntityWrapper<>();
//        orderWrapper.setSqlSelect("count(memberId)");
//        orderWrapper.in("memberId", memberIds);
//        orderWrapper.lt("orderTime", LocalDate.now());
//        orderWrapper.groupBy("memberId");
//        newCustomersNum = memberIds.size() - orderDao.selectCount(orderWrapper);

        TodayStatisticsDTO todayStatisticsDTO = new TodayStatisticsDTO(orderCustomersNum, orderTotalAmount, newCustomersNum);


        return null;
    }

    private List<OrderExportDTO> orderExport(StoreOrderPageParamsDTO paramDto) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PagePO pagePO = new PagePO();
        pagePO.setPageSize(paramDto.getExportSize());
        pagePO.setPageNo(paramDto.getExportIndex());
        Page<OrderEntity> pageParam = PageDataUtil.buildPageParam(pagePO);
        List<OrderEntity> orderEntities = ordersQueryUtil.queryStoreOrders(pageParam, paramDto);
        if (CollectionUtils.isEmpty(orderEntities)) {
            return Collections.emptyList();
        }

        // 所有订单ID集合
        List<Long> orderIds = new ArrayList<>(orderEntities.size());
        // 已发货订单ID 集合
        List<Long> expressOrderIds = new ArrayList<>(orderEntities.size());

        orderEntities.forEach(orderEntity -> {
            orderIds.add(orderEntity.getId());
            if (orderEntity.getOrderState() > OrderStatusEnum.PENDING_DELIVERY.getCode()) {
                expressOrderIds.add(orderEntity.getId());
            }
        });

        Map<Long, GoodExpressDTO> goodExpressDTOMap = new HashMap<>(expressOrderIds.size());

        // 已发货订单的物流信息集合
        if (CollectionUtils.isNotEmpty(expressOrderIds)) {
            List<GoodExpressDTO> goodExpressDTOS = orderDetailDao.getExpressInfoByOrderIds(expressOrderIds);
            goodExpressDTOS.forEach(goodExpressDTO -> {
                goodExpressDTOMap.put(goodExpressDTO.getOrderId(), goodExpressDTO);
            });
        }

        Map<Long, BigDecimal> refundPriceMap = new HashMap<>();
        // 各个订单的退款总额
        List<OrderRefundPriceDTO> orderRefundPriceDTOS = refundOrderDao.getRefundPriceByOrderId(orderIds, RefundOrderStateEnum.REFUNDED.getCode());
        if (CollectionUtils.isNotEmpty(orderRefundPriceDTOS)) {
            orderRefundPriceDTOS.forEach(obj -> {
                refundPriceMap.put(obj.getOrderId(), obj.getRefundPrice());
            });
        }

        // 收货地址
        Map<Long, OrderAddressEntity> orderAddressEntityMap = ordersQueryUtil.queryOrderReceiver(orderIds);

        // 订单详情
        Map<Long, List<OrderDetailEntity>> orderDetailEntityMap = ordersQueryUtil.queryDetailsByOrderIds(orderIds);

        List<OrderExportDTO> orderExportDTOS = new ArrayList<>(expressOrderIds.size());
        for (OrderEntity orderEntity : orderEntities) {
            Long orderId = orderEntity.getId();
            Integer orderState = orderEntity.getOrderState();
            OrderExportDTO orderExportDTO = new OrderExportDTO();
            orderExportDTO.setOrderId(orderId);
            orderExportDTO.setOrderTime(simpleDateFormat.format(orderEntity.getOrderTime()));
            orderExportDTO.setOrderPrice(orderEntity.getPayPrice());
            orderExportDTO.setOrderState(OrderStatusEnum.getDescByCode(orderState));

            // 已支付
            if (orderState > OrderStatusEnum.UNPAY.getCode()) {
                orderExportDTO.setPayId(orderEntity.getPayId());
                orderExportDTO.setPayMethod(PayMethodEnum.getDescByName(orderEntity.getPayType()));
                if (orderEntity.getPayTime() != null) {
                    orderExportDTO.setPayTime(simpleDateFormat.format(orderEntity.getPayTime()));
                }
                orderExportDTO.setCardPrice(orderEntity.getCardPrice());
                orderExportDTO.setBalance(orderEntity.getBalance());
            }
            if (orderState > OrderStatusEnum.PENDING_DELIVERY.getCode()) {
                GoodExpressDTO goodExpressDTOS = goodExpressDTOMap.get(orderId);
                if (null != goodExpressDTOS) {
                    orderExportDTO.setExpressNo(goodExpressDTOS.getExpressNo());
                    orderExportDTO.setExpressCompanyName(goodExpressDTOS.getExpressCompanyName());
                }
            }

            if (MapUtils.isNotEmpty(refundPriceMap)) {
                BigDecimal refundPrice = refundPriceMap.get(orderId);
                if (null != refundPrice) {
                    orderExportDTO.setRefundPrice(refundPrice);
                }
            }

            if (OrderStatusEnum.ORDER_COMPLETION.getCode().equals(orderState)) {
                orderExportDTO.setOrderCompleteTime(simpleDateFormat.format(orderEntity.getLastModifiedTime()));
            }

            // 查询收货地址
            OrderAddressEntity orderAddressEntity = orderAddressEntityMap.get(orderId);
            if (null != orderAddressEntity) {
                orderExportDTO.setReceiverName(orderAddressEntity.getName());
                orderExportDTO.setReceiverCardNo(orderAddressEntity.getIdNum());
                orderExportDTO.setReceiverPhone(orderAddressEntity.getTelephone());
                orderExportDTO.setProvince(orderAddressEntity.getProvince());
                orderExportDTO.setCity(orderAddressEntity.getCity());
                orderExportDTO.setCounty(orderAddressEntity.getCounty());
                orderExportDTO.setDetailAddress(orderAddressEntity.getAddress());
            }

            orderExportDTO.setIsComment(orderEntity.getCommentFlag() == null || Constants.Order.COMMENT_NO.equals(orderEntity.getCommentFlag()) ? "未评价" : "已评价");
            orderExportDTO.setStoreId(orderEntity.getStoreId());
            orderExportDTO.setStoreName(orderEntity.getStoreName());
            orderExportDTO.setMemberId(orderEntity.getMemberId());
            orderExportDTO.setMemberName(orderEntity.getMemberName());

            // 订单详情
            List<OrderDetailEntity> orderDetailEntities = orderDetailEntityMap.get(orderId);
            if (CollectionUtils.isEmpty(orderDetailEntities)) {
                continue;
            }
            for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
                OrderExportDTO entity = new OrderExportDTO();
                BeanCopyUtil.copy(orderExportDTO, entity);
                entity.setFieldValue(orderDetailEntity.getFieldValue());
                entity.setBarCode(orderDetailEntity.getBarCode());
                entity.setItemCode(orderDetailEntity.getItemCode());
                entity.setItemId(orderDetailEntity.getItemId());
                entity.setName(orderDetailEntity.getName());
                entity.setGoodNum(orderDetailEntity.getGoodsNum().intValue());
                orderExportDTOS.add(entity);
            }
        }
        return orderExportDTOS;
    }

    @Override
    @JedisOperation
    public String buildOrderExportParas(StoreOrderPageParamsDTO paramDto) {
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        Long storeId = currentUserDto.getStoreId();
        paramDto.setStoreId(storeId);
        String key = StringUtils.join(Constants.ExportKey.STORE_ORDER_EXPORT_KEY, storeId);
        String value = JSON.toJSONString(paramDto);
        if (redisCache.set(key, value, Constants.ExportKey.keyTime.intValue())) {
            return storeId.toString();
        }
        return null;
    }

    @Override
    @JedisOperation
    public void downloadExportData(HttpServletResponse response, String storeId) {
        LockResult storeLock = null;
        try {
            String key = StringUtils.join(Constants.ExportKey.STORE_ORDER_EXPORT_KEY, storeId);
            String params = redisCache.get(key);
            if (StringUtils.isBlank(params)) {
                return;
            }

            storeLock = distLockSservice.tryLock(Constants.LockOperatons.STORE_ORDER_EXPORT_LOCK, storeId);
            if (!storeLock.isSuccess()) {
                log.error(">>>>>>>>>>storeId:{} frequently call export order data!", storeId);
                throw new GlobalException(OrderExceptionEnum.EXPORT_ORDER_FAIL);
            }
            List<OrderExportDTO> orderExportDTOS = this.orderExport(JSON.parseObject(params, StoreOrderPageParamsDTO.class));
            if (CollectionUtils.isEmpty(orderExportDTOS)) {
                return;
            }

            String excelHeadColumn = ExportUtil.buildExcelHeadColumn("订单号,下单时间,订单金额,订单状态,支付单号,支付方式," +
                    "支付时间,美礼卡支付金额,余额支付金额,物流公司,物流单号,退款金额,订单完成时间,是否评价,店铺ID,店铺名称,用户ID,用户名称,收货人姓名," +
                    "收货人身份证号,收货人电话,省,市,区,详细地址,商品名称,商品ID,商品属性,商品条形码,商品编码,商品数量");
            String excelBodyColumn;
            try {
                excelBodyColumn = ExportUtil.buildExcelBodyColumn(orderExportDTOS, OrderExportDTO.class);
                ExportUtil.setRespProperties("导出", response);
                ExportUtil.doExport(excelHeadColumn, excelBodyColumn, response.getOutputStream());
            } catch (Exception e) {
                log.error(StringUtils.join(">>>>>>>>>>导出订单失败：", e.getMessage()), e);
                throw new GlobalException(OrderExceptionEnum.EXPORT_ORDER_FAIL);
            } finally {
                redisCache.delete(key);
            }
        } catch (Throwable t) {
            log.error("----------downloadExportData 异常!");
            throw t;
        } finally {
            distLockSservice.unlock(storeLock);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateExpress(OrderDeliveryDTO orderDeliveryDTO) {
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        Long storeId = currentUserDto.getStoreId();
        Long orderId = orderDeliveryDTO.getOrderId();

        LogisticsDto logisticsDto = expressageServiceFacade.getLogistics(orderDeliveryDTO.getExpressComId());
        if (null == logisticsDto) {
            throw new GlobalException(OrderExceptionEnum.EXPRESS_COMPANY_CANT_BE_FOUND);
        }

        OrderEntity orderEntity = ordersQueryUtil.queryStoreOrder(orderId, storeId);
        if (!orderEntity.getOrderState().equals(OrderStatusEnum.PENDING_RECEIVED.getCode())) {
            throw new GlobalException(OrderExceptionEnum.OPERATION_IS_NOT_ALLOWED);
        }
        List<OrderDetailEntity> orderDetailEntities = ordersQueryUtil.queryDetailsByOrderId(orderId);
        List<Long> detailIds = orderDetailEntities.stream().map(OrderDetailEntity::getId).collect(Collectors.toList());

        EntityWrapper<OrderDetailEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.in("id", detailIds);
        entityEntityWrapper.eq("orderState", OrderStatusEnum.PENDING_RECEIVED.getCode());

        OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
        orderDetailEntity.cleanInit();
        orderDetailEntity.setExpressComId(logisticsDto.getId());
        orderDetailEntity.setExpressComName(logisticsDto.getComName());
        orderDetailEntity.setExpressNo(orderDeliveryDTO.getExpressNo());
        if (orderDetailDao.update(orderDetailEntity, entityEntityWrapper) == detailIds.size()) {
            return true;
        }
        throw new GlobalException(OrderExceptionEnum.UPDATE_ORDER_EXPRESS_FAIL);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addRemark(Long orderId, String remarks) {
        if (null == orderId || StringUtils.isBlank(remarks)) {
            log.error(">>>>>>>>>> add remarks fail, params is illegal!");
            return false;
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        Long storeId = currentUserDto.getStoreId();
        LockResult storeLock = null;
        try {
            storeLock = distLockSservice.tryLock(Constants.LockOperatons.ORDER_OPERATION_LOCK, orderId);
            if (!storeLock.isSuccess()) {
                log.error(">>>>>>>>>>storeId:{} frequently call export order data!", orderId);
                throw new GlobalException(OrderExceptionEnum.ORDER_REPEAT_OPERATION);
            }
            // 订单是否存在
            ordersQueryUtil.queryStoreOrder(orderId, storeId);

            OrderRemarkEntity remarkEntity = new OrderRemarkEntity();
            remarkEntity.setCreateTime(new Date());
            remarkEntity.setMemberId(currentUserDto.getId());
            remarkEntity.setMemberName(StringUtils.join("商家(", currentUserDto.getUsername(), ")"));
            remarkEntity.setOrderId(orderId);
            remarkEntity.setRemark(remarks);

            return orderRemarkDao.insert(remarkEntity) > 0;
        } catch (Exception e) {
            log.error(">>>>>>>>>> add remark into order fail! err:{}", e);
        } finally {
            distLockSservice.unlock(storeLock);
        }
        return false;
    }

}
