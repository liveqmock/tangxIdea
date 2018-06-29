package com.topaiebiz.trade.order.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.dto.order.OrderGoodsDTO;
import com.topaiebiz.trade.order.core.cancel.OrderCancelChain;
import com.topaiebiz.trade.order.dao.OrderDao;
import com.topaiebiz.trade.order.dao.OrderDetailDao;
import com.topaiebiz.trade.order.dao.OrderPayDao;
import com.topaiebiz.trade.order.dto.customer.CmOrderDetailDTO;
import com.topaiebiz.trade.order.dto.customer.CmOrderPageDTO;
import com.topaiebiz.trade.order.dto.customer.EvaluateGoodDTO;
import com.topaiebiz.trade.order.dto.page.OrderPageParamDto;
import com.topaiebiz.trade.order.exception.OrderExceptionEnum;
import com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.service.CustomerOrderServie;
import com.topaiebiz.trade.order.util.OrderHelper;
import com.topaiebiz.trade.order.util.OrdersQueryUtil;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Description 用户订单service
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/20 13:42
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Service
public class CustomerOrderServiceImpl implements CustomerOrderServie {

    @Autowired
    private OrderPayDao orderPayDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderHelper orderHelper;

    @Autowired
    private OrdersQueryUtil ordersQueryUtil;

    @Autowired
    private OrderCancelChain orderCancelChain;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private DistLockSservice distLockSservice;

    @Override
    public PageInfo<CmOrderPageDTO> queryCustomerOrders(OrderPageParamDto paramDto) {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        Page<CmOrderPageDTO> pageDTOPage = PageDataUtil.buildPageParam(paramDto.getPagePO());
        // 1：查询订单
        List<OrderEntity> orderEntities = ordersQueryUtil.queryCustomerOrders(pageDTOPage, paramDto, memberId);
        if (CollectionUtils.isEmpty(orderEntities)) {
            return null;
        }

        List<Long> orderIds = new ArrayList<>(orderEntities.size());
        List<CmOrderPageDTO> cmOrderPageDTOS = new ArrayList<>(orderEntities.size());
        orderEntities.forEach(orderEntity -> {
            CmOrderPageDTO cmOrderPageDTO = new CmOrderPageDTO();
            BeanCopyUtil.copy(orderEntity, cmOrderPageDTO);
            cmOrderPageDTOS.add(cmOrderPageDTO);
            orderIds.add(orderEntity.getId());
        });

        // 2：查询订单明细
        Map<Long, List<OrderDetailEntity>> orderDetails = ordersQueryUtil.queryDetailsByOrderIds(orderIds);
        if (MapUtils.isNotEmpty(orderDetails)) {
            cmOrderPageDTOS.forEach(cmOrderPageDTO -> cmOrderPageDTO.setCmOrderDetailDTOS(orderHelper.buildCustomerOrderDetail(orderDetails.get(cmOrderPageDTO.getId()))));
        }
        pageDTOPage.setRecords(cmOrderPageDTOS);
        return PageDataUtil.copyPageInfo(pageDTOPage);
    }

    @Override
    public CmOrderDetailDTO queryCustomerOrderDetail(Long orderId) {
        OrderEntity orderEntity = ordersQueryUtil.queryCustomerOrder(orderId, MemberContext.getCurrentMemberToken().getMemberId());
        // 检查
        orderEntity = orderHelper.orderCheck(orderEntity);

        CmOrderDetailDTO cmOrderDetailDTO = new CmOrderDetailDTO();
        BeanCopyUtil.copy(orderEntity, cmOrderDetailDTO);

        // 查询支付信息
        cmOrderDetailDTO.setOrderPayDTO(orderHelper.buildCustomerPayInfo(orderEntity));

        // 查询订单明细
        List<OrderGoodsDTO> orderGoodsDTOS = orderHelper.buildOrderDetailGoodsDTO(orderId);
        BigDecimal discount = BigDecimal.ZERO;
        for (OrderGoodsDTO orderGoodsDTO : orderGoodsDTOS) {
            discount = discount.add(orderGoodsDTO.getDiscount());
            orderGoodsDTO.setPayPrice(orderGoodsDTO.getGoodsPrice().subtract(orderGoodsDTO.getDiscount().divide(new BigDecimal(orderGoodsDTO.getGoodsNum()), 2)));
        }
        // 商品总价
        cmOrderDetailDTO.setGoodsTotal(orderEntity.getGoodsTotal().subtract(discount));
        // 店铺优惠总价
        cmOrderDetailDTO.setStoreDiscount(orderEntity.getStoreDiscount().add(orderEntity.getStoreCouponDiscount()));
        cmOrderDetailDTO.setOrderGoodsDTOS(orderGoodsDTOS);

        // 查询订单收货地址
        cmOrderDetailDTO.setOrderAddressDTO(ordersQueryUtil.queryOrderAddress(orderId));

        // 查询订单发票信息
        cmOrderDetailDTO.setOrderInvoiceDTO(ordersQueryUtil.queryOrderInvoice(orderId));
        return cmOrderDetailDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(Long payId) {
        LockResult memberLock = null;
        try {
            OrderPayEntity orderPayEntity = orderPayDao.selectById(payId);
            if (null == orderPayEntity) {
                throw new GlobalException(OrderExceptionEnum.ORDER_CANT_BE_FOUND);
            }
            memberLock = distLockSservice.tryLock(Constants.LockOperatons.TRADE_ORDER_PAY_, payId);
            if (!memberLock.isSuccess()) {
                throw new GlobalException(OrderSubmitExceptionEnum.ORDER_CANCEL_ERROR);
            }
            if (!orderPayEntity.getPayState().equals(OrderConstants.PayStatus.UNPAY)) {
                log.error("----------order pay state is unpay ,cant be canceled");
                throw new GlobalException(OrderSubmitExceptionEnum.ORDER_CANCEL_ERROR);
            }
            MemberTokenDto token = MemberContext.getCurrentMemberToken();

            BuyerBO buyerBO = new BuyerBO();
            buyerBO.setMemberId(token.getMemberId());
            buyerBO.setMemberName(token.getUserName());
            buyerBO.setMobile(token.getTelephone());
            return orderCancelChain.cancel(buyerBO, payId);
        } finally {
            distLockSservice.unlock(memberLock);
        }
    }

    @Override
    public Boolean deleteOrder(Long orderId) {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        OrderEntity orderEntity = ordersQueryUtil.queryCustomerOrder(orderId, memberId);

        Integer orderState = orderEntity.getOrderState();
        if (!OrderStatusEnum.ORDER_CANCELLATION.getCode().equals(orderState) && !OrderStatusEnum.ORDER_COMPLETION.getCode().equals(orderState) && !OrderStatusEnum.ORDER_CLOSE.getCode().equals(orderState)) {
            log.error("----------operation is not allowed，member:{} operate the order:{} to deleteOrder", MemberContext.getCurrentMemberToken().getMemberId(), orderId);
            throw new GlobalException(OrderExceptionEnum.OPERATION_IS_NOT_ALLOWED);
        }
        OrderEntity orderUpdate = new OrderEntity();
        orderUpdate.cleanInit();
        orderUpdate.setId(orderId);
        orderUpdate.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        orderUpdate.setLastModifierId(memberId);
        orderUpdate.setLastModifiedTime(new Date());
        return orderDao.updateById(orderUpdate) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmReceipt(Long orderId) {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        OrderEntity orderEntity = ordersQueryUtil.queryCustomerOrder(orderId, memberId);

        if (orderEntity.getLockState().equals(Constants.OrderLockFlag.LOCK_YES)) {
            log.error("----------member:{} ,order:{} is locked, cant do confirmReceipt operation!", memberId, orderId);
            throw new GlobalException(OrderExceptionEnum.OPERATION_IS_NOT_ALLOWED);
        }
        if (!orderEntity.getOrderState().equals(OrderStatusEnum.PENDING_RECEIVED.getCode())) {
            log.error("----------operation is not allowed，member:{} operate the order:{} to confirmReceipt", memberId, orderId);
            throw new GlobalException(OrderExceptionEnum.OPERATION_IS_NOT_ALLOWED);
        }
        boolean changes;
        Date currentDate = new Date();
        OrderEntity orderUpdate = new OrderEntity();
        orderUpdate.cleanInit();
        orderUpdate.setId(orderId);
        orderUpdate.setOrderState(OrderStatusEnum.HAVE_BEEN_RECEIVED.getCode());
        orderUpdate.setReceiveTime(currentDate);
        orderUpdate.setLastModifiedTime(currentDate);
        orderUpdate.setLastModifierId(memberId);
        changes = orderDao.updateById(orderUpdate) > 0;
        if (changes) {
            EntityWrapper<OrderDetailEntity> updateWrapper = new EntityWrapper<>();
            updateWrapper.eq("orderId", orderId);
            updateWrapper.eq("orderState", OrderStatusEnum.PENDING_RECEIVED.getCode());

            OrderDetailEntity updateEntity = new OrderDetailEntity();
            updateEntity.cleanInit();
            updateEntity.setOrderState(OrderStatusEnum.HAVE_BEEN_RECEIVED.getCode());
            updateEntity.setReceiveTime(currentDate);
            updateEntity.setLastModifierId(memberId);
            updateEntity.setLastModifiedTime(currentDate);
            orderDetailDao.update(updateEntity, updateWrapper);
        }
        return changes;
    }

    @Override
    public Boolean extendShip(Long orderId) {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        OrderEntity orderEntity = ordersQueryUtil.queryCustomerOrder(orderId, memberId);

        if (orderEntity.getLockState().equals(Constants.OrderLockFlag.LOCK_YES)) {
            log.error("----------member:{} ,order:{} is locked, cant do extendShip operation!", memberId, orderId);
            throw new GlobalException(OrderExceptionEnum.OPERATION_IS_NOT_ALLOWED);
        }
        // 判断订单状态是否允许延长收货
        if (!orderEntity.getOrderState().equals(OrderStatusEnum.PENDING_RECEIVED.getCode())) {
            log.error("----------extendShip operation failed, The current operation of the order is not allowed!");
            return false;
        }
        Integer extendShip = orderEntity.getExtendShip();
        if (null != extendShip && extendShip.equals(Constants.Order.EXTEND_SHIP_YES)) {
            log.error("----------operation failed, order:{} has beed extend ship", orderId);
            return false;
        }
        OrderEntity orderUpdate = new OrderEntity();
        orderUpdate.cleanInit();
        orderUpdate.setId(orderId);
        orderUpdate.setExtendShip(Constants.Order.EXTEND_SHIP_YES);
        orderUpdate.setLastModifiedTime(new Date());
        orderUpdate.setLastModifierId(memberId);
        return orderDao.updateById(orderUpdate) > 0;
    }

    @Override
    public List<EvaluateGoodDTO> evaluateOrder(Long orderId) {
        OrderEntity orderEntity = ordersQueryUtil.queryCustomerOrder(orderId, MemberContext.getCurrentMemberToken().getMemberId());
        if (orderEntity.getCommentFlag().equals(Constants.Order.COMMENT_YES)) {
            return Collections.emptyList();
        }
        List<OrderDetailEntity> orderDetailEntities = ordersQueryUtil.queryDetailsByOrderId(orderId);
        List<EvaluateGoodDTO> evaluateGoodDTOS = new ArrayList<>(orderDetailEntities.size());
        for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
            if (!orderDetailEntity.getRefundState().equals(OrderConstants.OrderRefundStatus.REFUND)) {
                EvaluateGoodDTO evaluateGoodDTO = new EvaluateGoodDTO();
                BeanCopyUtil.copy(orderDetailEntity, evaluateGoodDTO);
                evaluateGoodDTO.setOrderDetailId(orderDetailEntity.getId());
                evaluateGoodDTOS.add(evaluateGoodDTO);
            }
        }
        return evaluateGoodDTOS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Integer queryMemberVaildOrderCounts(Long memberId) {
        return orderDao.selectCount(Condition.create().setSqlSelect("count(id)").eq("memberId", memberId).gt("orderState", OrderStatusEnum.UNPAY.getCode()));
    }
}
