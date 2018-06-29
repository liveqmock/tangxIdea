package com.topaiebiz.trade.order.util;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.dto.order.*;
import com.topaiebiz.trade.dto.order.params.OrderQueryParams;
import com.topaiebiz.trade.order.dao.*;
import com.topaiebiz.trade.order.dto.common.OrderRemarkDTO;
import com.topaiebiz.trade.order.dto.customer.CmOrderPageDTO;
import com.topaiebiz.trade.order.dto.page.OrderPageParamDto;
import com.topaiebiz.trade.order.dto.store.StoreOrderPageParamsDTO;
import com.topaiebiz.trade.order.exception.OrderExceptionEnum;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.*;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description 订单查询工具类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/2 16:19
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class OrdersQueryUtil {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private OrderAddressDao orderAddressDao;

    @Autowired
    private OrderInvoiceDao orderInvoiceDao;

    @Autowired
    private OrderPayDao orderPayDao;

    @Autowired
    private OrderRemarkDao orderRemarkDao;

    @Autowired
    private OrderHelper orderHelper;


    /**
     * Description: 根据PayId 查询 订单IDs
     *
     * @Author: hxpeng
     * createTime: 2018/6/1
     * @param:
     **/
    public List<Long> queryOrderIds(Long payId) {
        if (null == payId) {
            return Collections.emptyList();
        }
        EntityWrapper<OrderEntity> wrapper = new EntityWrapper<>();
        wrapper.setSqlSelect("id");
        wrapper.eq("payId", payId);

        List<OrderEntity> orderEntities = orderDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(orderEntities)) {
            return Collections.emptyList();
        }
        return orderEntities.stream().map(OrderEntity::getId).collect(Collectors.toList());
    }


    public List<OrderRemarkDTO> queryOrderRemarks(OrderEntity orderEntity) {
        Long orderId = orderEntity.getId();
        EntityWrapper<OrderRemarkEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("orderId", orderId);

        List<OrderRemarkEntity> remarkEntities = orderRemarkDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(remarkEntities)) {
            return Collections.emptyList();
        }

        List<OrderRemarkDTO> remarkDTOS = new ArrayList<>(remarkEntities.size());
        remarkDTOS.add(new OrderRemarkDTO(orderEntity.getMemo(), orderEntity.getMemberName(), orderEntity.getOrderTime()));
        remarkEntities.forEach(orderRemarkEntity -> {
            remarkDTOS.add(new OrderRemarkDTO(orderRemarkEntity.getRemark(), orderRemarkEntity.getMemberName(), orderRemarkEntity.getCreateTime()));
        });
        return remarkDTOS;
    }


    /**
     * Description: 订单分页
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/12
     *
     * @param:
     **/
    public List<OrderEntity> queryOrders(Page<OrderEntity> page, OrderPageParamDto paramDto) {
        return orderDao.queryOrders(page, paramDto);
    }

    public List<OrderEntity> queryStoreOrders(Page<OrderEntity> page, StoreOrderPageParamsDTO paramsDTO) {
        try {
            return orderDao.queryStoreOrders(page, paramsDTO);
        } catch (Exception e) {
            log.error(">>>>>>>>>>query order data error!!!!", e);
            return Collections.emptyList();
        }
    }

    /**
     * Description: 批量查询订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/5
     *
     * @param:
     **/
    public List<OrderEntity> queryOrders(List<Long> orderIds) {
        EntityWrapper<OrderEntity> orderWrapper = new EntityWrapper<>();
        orderWrapper.in("id", orderIds);
//        orderWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        return orderDao.selectList(orderWrapper);
    }

    /**
     * Description: 获取用户订单分页
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/12
     *
     * @param:
     **/
    public List<OrderEntity> queryCustomerOrders(Page<CmOrderPageDTO> pageDTOPage, OrderPageParamDto paramDto, Long memberId) {
        EntityWrapper<OrderEntity> orderWrapper = new EntityWrapper<>();
        orderWrapper.eq("memberId", memberId);
        orderWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        Integer orderState = paramDto.getOrderState();
        if (null != orderState) {
            if (orderState.equals(OrderStatusEnum.WAIT_EVALUATION.getCode())) {
                // 待评价    订单关闭 > 待评价 =（已收货，已完成, 未评价） > 待发货
                orderWrapper.gt("orderState", OrderStatusEnum.PENDING_RECEIVED.getCode());
                orderWrapper.lt("orderState", OrderStatusEnum.ORDER_CLOSE.getCode());
                orderWrapper.eq("refundState", OrderConstants.OrderRefundStatus.NO_REFUND);
                orderWrapper.eq("commentFlag", Constants.Order.COMMENT_NO);
            } else {
                orderWrapper.eq("orderState", paramDto.getOrderState());
            }
        }
        orderWrapper.orderBy("orderTime", false);
        return orderDao.selectPage(pageDTOPage, orderWrapper);
    }

    /**
     * Description: 查询单个订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/13
     *
     * @param:
     **/
    public OrderEntity queryOrder(Long orderId) {
        OrderEntity orderEntity = null;
        if (null != orderId) {
            orderEntity = orderDao.selectById(orderId);
        }
        if (null == orderEntity) {
            throw new GlobalException(OrderExceptionEnum.ORDER_CANT_BE_FOUND);
        }
        return orderEntity;
    }

    /**
     * Description: 用户查询某个订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/13
     *
     * @param:
     **/
    public OrderEntity queryCustomerOrder(Long orderId, Long memberId) {
        OrderEntity orderEntity = null;
        if (null != orderId) {
            OrderEntity customerEntity = new OrderEntity();
            customerEntity.cleanInit();
            customerEntity.setMemberId(memberId);
            customerEntity.setId(orderId);
            orderEntity = orderDao.selectOne(customerEntity);
        }
        if (null == orderEntity) {
            throw new GlobalException(OrderExceptionEnum.ORDER_CANT_BE_FOUND);
        }
        return orderEntity;
    }

    /**
     * Description: 商家查询某个订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/25
     *
     * @param:
     **/
    public OrderEntity queryStoreOrder(Long orderId, Long storeId) {
        OrderEntity orderEntity = null;
        if (null != orderId && null != storeId) {
            OrderEntity customerEntity = new OrderEntity();
            customerEntity.cleanInit();
            customerEntity.setStoreId(storeId);
            customerEntity.setId(orderId);
            orderEntity = orderDao.selectOne(customerEntity);
        }
        if (null == orderEntity) {
            throw new GlobalException(OrderExceptionEnum.ORDER_CANT_BE_FOUND);
        }
        return orderEntity;
    }


    /**
     * Description: 查询订单支付信息
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/18
     *
     * @param:
     **/
    public OrderPayEntity queryOrderPay(Long payId) {
        OrderPayEntity orderPayEntity = null;
        if (null != payId) {
            orderPayEntity = orderPayDao.selectById(payId);
        }
        if (null == orderPayEntity) {
            throw new GlobalException(OrderExceptionEnum.ORDER_PAY_INFO_CANT_BE_FOUND);
        }
        return orderPayEntity;
    }


    /**
     * Description: 根据支付ID 查询订单集合
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/16
     *
     * @param:
     **/
    public List<OrderEntity> queryOrdersByPayId(Long payId) {
        if (null == payId) {
            return Collections.emptyList();
        }
        EntityWrapper<OrderEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.eq("payId", payId);
        return orderDao.selectList(entityEntityWrapper);
    }


    /**
     * Description: 订单ID集合 批量查询订单收货人信息
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/12
     *
     * @param:
     **/
    public Map<Long, OrderAddressEntity> queryOrderReceiver(List<Long> orderIds) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return Collections.emptyMap();
        }
        EntityWrapper<OrderAddressEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("orderId", orderIds);

        List<OrderAddressEntity> orderAddressEntities = orderAddressDao.selectList(entityWrapper);
        if (!CollectionUtils.isEmpty(orderAddressEntities)) {
            Map<Long, OrderAddressEntity> map = new HashMap<>(orderIds.size());
            orderAddressEntities.forEach(orderAddressEntity -> map.put(orderAddressEntity.getOrderId(), orderAddressEntity));
            return map;
        }
        return Collections.emptyMap();
    }


    public OrderAddressDTO queryOrderAddress(Long orderId, boolean showIdCard) {
        OrderAddressDTO orderAddressDTO = this.queryOrderAddress(orderId);
        if (!showIdCard) {
            orderAddressDTO.setMemberIdCard(null);
        }
        return orderAddressDTO;
    }

    public OrderAddressEntity queryOrderAddressEntity(Long orderId) {
        OrderAddressEntity condition = new OrderAddressEntity();
        condition.cleanInit();
        condition.setOrderId(orderId);
        OrderAddressEntity orderAddressEntity = orderAddressDao.selectOne(condition);
        if (null == orderAddressEntity) {
            throw new GlobalException(OrderExceptionEnum.ORDER_ADDRES_CANT_BE_FOUND);
        }
        return orderAddressEntity;
    }

    /**
     * Description: 订单详情--查询收货信息
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/13
     *
     * @param:
     **/
    public OrderAddressDTO queryOrderAddress(Long orderId) {
        if (null != orderId) {
            OrderAddressEntity condition = new OrderAddressEntity();
            condition.cleanInit();
            condition.setOrderId(orderId);
            condition.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);

            OrderAddressEntity orderAddressEntity = orderAddressDao.selectOne(condition);
            if (null != orderAddressEntity) {
                return orderHelper.buildOrderAddressDTO(orderAddressEntity);
            }
        }
        throw new GlobalException(OrderExceptionEnum.ORDER_ADDRES_CANT_BE_FOUND);
    }

    /**
     * Description: 订单详情--查询发票信息
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/13
     *
     * @param:
     **/
    public OrderInvoiceDTO queryOrderInvoice(Long orderId) {
        OrderInvoiceEntity condition = new OrderInvoiceEntity();
        condition.cleanInit();
        condition.setOrderId(orderId);
        condition.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        OrderInvoiceEntity orderInvoiceEntity = orderInvoiceDao.selectOne(condition);
        if (null != orderInvoiceEntity) {
            OrderInvoiceDTO orderInvoiceDTO = new OrderInvoiceDTO();
            BeanCopyUtil.copy(orderInvoiceEntity, orderInvoiceDTO);
            return orderInvoiceDTO;
        }
        return null;
    }

    /**
     * Description: 根据订单ID/明细ID集合/状态 查询订单明细
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/13
     *
     * @param:
     **/
    public List<OrderDetailEntity> queryDetailsByOrderState(Long orderId, List<Long> orderDetailIds, Integer orderState) {
        EntityWrapper<OrderDetailEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("orderState", orderState);
        if (null != orderId) {
            entityWrapper.eq("orderId", orderId);
        }
        if (CollectionUtils.isNotEmpty(orderDetailIds)) {
            entityWrapper.in("id", orderDetailIds);
        }
        entityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<OrderDetailEntity> orderDetailEntities = orderDetailDao.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(orderDetailEntities)) {
            throw new GlobalException(OrderExceptionEnum.ORDER_DETAILS_CANT_BE_FOUND);
        }
        return orderDetailEntities;
    }

    /**
     * Description: 根据订单明细ID集合 查询订单明细集合
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/17
     *
     * @param:
     **/
    public List<OrderDetailEntity> queryDetailsByDetailsIds(Collection<Long> detailIds) {
        if (CollectionUtils.isEmpty(detailIds)) {
            throw new GlobalException(OrderExceptionEnum.ORDER_DETAILS_CANT_BE_FOUND);
        }
        EntityWrapper<OrderDetailEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", detailIds);
        entityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<OrderDetailEntity> orderDetailEntities = orderDetailDao.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(orderDetailEntities)) {
            throw new GlobalException(OrderExceptionEnum.ORDER_DETAILS_CANT_BE_FOUND);
        }
        return orderDetailEntities;
    }

    /**
     * Description: 根据订单ID集合 批量查询订单明细
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/13
     *
     * @param:
     **/
    public List<OrderDetailEntity> queryDetailsByOrderId(Long orderId) {
        EntityWrapper<OrderDetailEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("orderId", orderId);
        entityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<OrderDetailEntity> orderDetailEntities = orderDetailDao.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(orderDetailEntities)) {
            throw new GlobalException(OrderExceptionEnum.ORDER_DETAILS_CANT_BE_FOUND);
        }
        return orderDetailEntities;
    }

    /**
     * Description: 根据订单ID集合 批量查询订单明细集合
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/12
     *
     * @param:
     **/
    public Map<Long, List<OrderDetailEntity>> queryDetailsByOrderIds(List<Long> orderIds) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return Collections.emptyMap();
        }
        EntityWrapper<OrderDetailEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("orderId", orderIds);
        entityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

        List<OrderDetailEntity> orderDetailEntities = orderDetailDao.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(orderDetailEntities)) {
            return Collections.emptyMap();
        }
        Map<Long, List<OrderDetailEntity>> map = new HashMap<>(orderIds.size());
        orderDetailEntities.forEach(orderDetailEntity -> {
            Long orderId = orderDetailEntity.getOrderId();
            List<OrderDetailEntity> list = map.get(orderId);
            if (null == list) {
                list = new ArrayList<>();
            }
            list.add(orderDetailEntity);
            map.put(orderId, list);
        });
        return map;
    }


    /**
     * Description: 查询超时未支付的订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/1
     *
     * @param:
     **/
    public List<OrderEntity> queryUnPayOrder() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, -Constants.Order.UNPAY_AUDIT_CANCEL_HOURS);

        EntityWrapper<OrderEntity> orderEntityWrapper = new EntityWrapper<>();
        orderEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        orderEntityWrapper.eq("lockState", Constants.OrderLockFlag.LOCK_NO);
        orderEntityWrapper.eq("orderState", OrderStatusEnum.UNPAY.getCode());
        orderEntityWrapper.lt("orderTime", calendar.getTime());
        orderEntityWrapper.last("limit 100");
        return orderDao.selectList(orderEntityWrapper);
    }

    /**
     * Description: 查询超时未收货的订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/2
     *
     * @param:
     **/
    public List<OrderEntity> queryReceiveOrder() {
        Date currentDate = new Date();
        // 正常收货时间
        Calendar receiveTime = Calendar.getInstance();
        receiveTime.setTime(currentDate);
        receiveTime.add(Calendar.DAY_OF_WEEK, -Constants.Order.SHIP_AUDIT_RECEIVE_DAYS);

        // 延长后的收货时间
        Calendar extendReceiveTime = Calendar.getInstance();
        extendReceiveTime.setTime(currentDate);
        extendReceiveTime.add(Calendar.DAY_OF_WEEK, -(Constants.Order.EXTEND_DAYS + Constants.Order.SHIP_AUDIT_RECEIVE_DAYS));

        EntityWrapper<OrderEntity> orderEntityWrapper = new EntityWrapper<>();
        orderEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        orderEntityWrapper.eq("lockState", Constants.OrderLockFlag.LOCK_NO);
        orderEntityWrapper.eq("orderState", OrderStatusEnum.PENDING_RECEIVED.getCode());
        orderEntityWrapper.andNew("(extendShip = {0} and shipmentTime < {1})", Constants.Order.EXTEND_SHIP_NO, receiveTime.getTime())
                .or("(extendShip = {0} and shipmentTime < {1})", Constants.Order.EXTEND_SHIP_YES, extendReceiveTime.getTime());
        orderEntityWrapper.last("limit 100");
        return orderDao.selectList(orderEntityWrapper);
    }

    /**
     * Description: 查询超时未完成的订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/2
     *
     * @param:
     **/
    public List<OrderEntity> queryCompleteOrder() {
        // 正常收货时间
        Calendar completeTime = Calendar.getInstance();
        completeTime.setTime(new Date());
        completeTime.add(Calendar.DAY_OF_WEEK, -Constants.Order.RECEIVE_AUTO_COMPLETE_DAYS);

        EntityWrapper<OrderEntity> orderEntityWrapper = new EntityWrapper<>();
        orderEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        orderEntityWrapper.eq("lockState", Constants.OrderLockFlag.LOCK_NO);
        orderEntityWrapper.eq("refundState", OrderConstants.OrderRefundStatus.NO_REFUND);
        orderEntityWrapper.eq("orderState", OrderStatusEnum.HAVE_BEEN_RECEIVED.getCode());
        orderEntityWrapper.lt("receiveTime", completeTime.getTime());
        orderEntityWrapper.last("limit 100");
        return orderDao.selectList(orderEntityWrapper);
    }

    public static void main(String[] args) {
        // 正常收货时间
//        Calendar receiveTime = Calendar.getInstance();
//        receiveTime.setTime(new Date());
//        receiveTime.add(Calendar.DAY_OF_WEEK, -Constants.Order.SHIP_AUDIT_RECEIVE_DAYS);
//
//        // 延长后的收货时间
//        Calendar extendReceiveTime = Calendar.getInstance();
//        extendReceiveTime.setTime(new Date());
//        extendReceiveTime.add(Calendar.DAY_OF_WEEK, -Constants.Order.EXTEND_DAYS - Constants.Order.SHIP_AUDIT_RECEIVE_DAYS);
//        System.out.println(receiveTime.getTime());
//        System.out.println("================");
//        System.out.println(extendReceiveTime.getTime());


    }

    /**
     * Description: 订单ID集合 批量 查询 订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/5
     *
     * @param:
     **/
    public Map<Long, OrderEntity> queryOrderMap(List<Long> orderIds) {
        Map<Long, OrderEntity> map = new HashMap<>(orderIds.size());
        List<OrderEntity> orderEntities = this.queryOrders(orderIds);
        for (OrderEntity orderEntity : orderEntities) {
            map.put(orderEntity.getId(), orderEntity);
        }
        return map;
    }


    /**
     * Description: 订单ID集合 查询 各个订单的明细ID集合
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/5
     *
     * @param:
     **/
    public Map<Long, Set<Long>> queryOrderDetailIdsInRefundTask(List<Long> orderIds) {
        Map<Long, List<OrderDetailEntity>> orderDetailMap = this.queryDetailsByOrderIds(orderIds);
        Map<Long, Set<Long>> orderDetailIdsMap = new HashMap<>(orderDetailMap.size());
        for (Map.Entry<Long, List<OrderDetailEntity>> orderDetail : orderDetailMap.entrySet()) {
            OrderDetailEntity orderDetailEntity = orderDetail.getValue().get(0);
            if (null != orderDetailEntity && OrderStatusEnum.PENDING_DELIVERY.getCode().equals(orderDetailEntity.getOrderState())) {
                orderDetailIdsMap.put(orderDetail.getKey(), null);
            } else {
                orderDetailIdsMap.put(orderDetail.getKey(), orderDetail.getValue().stream().map(OrderDetailEntity::getId).collect(Collectors.toSet()));
            }
        }
        return orderDetailIdsMap;
    }

    /**
     * Description: 订单是否有明细在售后中
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/26
     *
     * @param:
     **/
    public boolean hasOrderDetailInRefunding(Long orderId) {
        EntityWrapper<OrderDetailEntity> orderDetailWrapper = new EntityWrapper<>();
        orderDetailWrapper.setSqlSelect("count(id)");
        orderDetailWrapper.eq("orderId", orderId);
        orderDetailWrapper.eq("refundState", OrderConstants.OrderRefundStatus.REFUNDING);
        return orderDetailDao.selectCount(orderDetailWrapper) > 0;
    }

    /**
     * Description: API---订单查询
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/27
     *
     * @param:
     **/
    public PageInfo<OrderDTO> queryOrdersFromAPI(OrderQueryParams params) {
        if (null == params || null == params.getStoreId()) {
            log.error(">>>>>>>>>>api request order page error, params is illegal");
            throw new GlobalException(OrderExceptionEnum.ORDER_QUERY_PARAMS_ILLEGAL);
        }
        EntityWrapper<OrderEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("storeId", params.getStoreId());
        if (null != params.getOrderState()) {
            wrapper.eq("orderState", params.getOrderState());
        }
        if (null != params.getOrderId()) {
            wrapper.eq("id", params.getOrderId());
        }
        if (null != params.getOrderStartTime() && null != params.getOrderEndTime()) {
            wrapper.gt("orderTime", params.getOrderStartTime());
            wrapper.lt("orderTime", params.getOrderEndTime());
        }

        PagePO pagePO = new PagePO();
        if (null != params.getPageNo() && null != params.getPageSize()) {
            pagePO.setPageNo(params.getPageNo());
            pagePO.setPageSize(params.getPageSize());
        }

        Page<OrderEntity> orderEntityPage = PageDataUtil.buildPageParam(pagePO);
        List<OrderEntity> orderEntities = orderDao.selectPage(orderEntityPage, wrapper);

        orderEntityPage.setRecords(orderEntities);
        return PageDataUtil.copyPageInfo(orderEntityPage, OrderDTO.class);
    }

    /**
     * Description: API---查询商品明细
     * key= orderId
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/27
     *
     * @param:
     **/
    @SuppressWarnings("unchecked")
    public Map<Long, List<OrderGoodsDTO>> queryOrderGoodsInfoFromAPI(Collection<Long> orderIds) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return Collections.emptyMap();
        }
        // 1:查询订单商品明细
        Wrapper wrapper = Condition.create().in("orderId", orderIds);
        List<OrderDetailEntity> orderDetailEntities = orderDetailDao.selectList(wrapper);

        if (CollectionUtils.isEmpty(orderDetailEntities)) {
            return Collections.emptyMap();
        }

        // 2：转换订单商品明细类型
        final List<OrderGoodsDTO> orderGoodsDTOS = new ArrayList<>(orderDetailEntities.size());
        orderDetailEntities.forEach(orderDetailEntity -> {
            OrderGoodsDTO orderGoodsDTO = new OrderGoodsDTO();
            BeanCopyUtil.copy(orderDetailEntity, orderGoodsDTO);
            orderGoodsDTOS.add(orderGoodsDTO);
        });

        // 3：分组
        return orderGoodsDTOS.stream().collect(Collectors.groupingBy(OrderGoodsDTO::getOrderId));
    }

    /**
     * Description: API---查询订单地址
     * key= orderId
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/27
     *
     * @param:
     **/
    @SuppressWarnings("unchecked")
    public Map<Long, OrderAddressDTO> queryOrderAddressFromAPI(Collection<Long> orderIds) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return Collections.emptyMap();
        }
        // 1:查询订单商品明细
        Wrapper wrapper = Condition.create().in("orderId", orderIds);
        List<OrderAddressEntity> orderAddressEntities = orderAddressDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(orderAddressEntities)) {
            return Collections.emptyMap();
        }

        // 2：转换订单商品明细类型
        List<OrderAddressDTO> orderAddressDTOS = new ArrayList<>(orderAddressEntities.size());
        orderAddressEntities.forEach(orderAddressEntity -> {
            OrderAddressDTO orderAddressDTO = new OrderAddressDTO();
            BeanCopyUtil.copy(orderAddressEntity, orderAddressDTO);
            orderAddressDTO.setMemberIdCard(orderAddressEntity.getIdNum());
            BeanCopyUtil.copy(orderAddressEntity, orderAddressDTO);
            orderAddressDTO.setAddress(StringUtils.join(orderAddressEntity.getProvince(), ".", orderAddressEntity.getCity(), ".", orderAddressEntity.getCounty(), ".", orderAddressEntity.getAddress()));
            orderAddressDTO.setDetailAddress(orderAddressDTO.getAddress());
            orderAddressDTOS.add(orderAddressDTO);
        });

        // 3：分组
        Map<Long, OrderAddressDTO> map = new HashMap<>();
        orderAddressDTOS.forEach(orderAddressDTO -> map.putIfAbsent(orderAddressDTO.getOrderId(), orderAddressDTO));
        return map;
    }

    /**
     * Description: API---查询订单支付信息
     * key= payId
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/27
     *
     * @param:
     **/
    @SuppressWarnings("unchecked")
    public Map<Long, OrderPayDTO> queryOrderPayInfoFromAPI(Collection<Long> payIds) {
        if (CollectionUtils.isEmpty(payIds)) {
            return Collections.emptyMap();
        }
        // 1:查询订单商品明细
        Wrapper wrapper = Condition.create().in("id", payIds);
        List<OrderPayEntity> orderPayEntities = orderPayDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(orderPayEntities)) {
            return Collections.emptyMap();
        }

        // 2：转换订单商品明细类型
        List<OrderPayDTO> orderPayDTOS = new ArrayList<>(orderPayEntities.size());
        orderPayEntities.forEach(orderPayEntity -> {
            OrderPayDTO orderPayDTO = new OrderPayDTO();
            BeanCopyUtil.copy(orderPayEntity, orderPayDTO);
            orderPayDTOS.add(orderPayDTO);
        });

        // 3：分组
        Map<Long, OrderPayDTO> map = new HashMap<>();
        orderPayDTOS.forEach(orderPayDTO -> map.putIfAbsent(orderPayDTO.getId(), orderPayDTO));
        return map;
    }

    /**
     * Description: 批量查询订单支付信息
     *
     * @Author: hxpeng
     * createTime: 2018/6/1
     * @param:
     **/
    public Map<Long, OrderPayEntity> queryOrderPayMap(Collection<Long> payIds) {
        if (CollectionUtils.isEmpty(payIds)) {
            return Collections.emptyMap();
        }
        // 1:查询订单商品明细
        Wrapper wrapper = Condition.create().in("id", payIds);
        List<OrderPayEntity> orderPayEntities = orderPayDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(orderPayEntities)) {
            return Collections.emptyMap();
        }
        Map<Long, OrderPayEntity> map = new HashMap<>();
        orderPayEntities.forEach(orderPayEntity -> map.putIfAbsent(orderPayEntity.getId(), orderPayEntity));
        return map;
    }

}
