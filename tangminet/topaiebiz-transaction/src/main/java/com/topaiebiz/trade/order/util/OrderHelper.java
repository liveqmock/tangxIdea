package com.topaiebiz.trade.order.util;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.enumdata.PayMethodEnum;
import com.nebulapaas.common.BeanCopyUtil;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.dto.order.OrderAddressDTO;
import com.topaiebiz.trade.dto.order.OrderDetailDTO;
import com.topaiebiz.trade.dto.order.OrderGoodsDTO;
import com.topaiebiz.trade.dto.order.OrderPayDTO;
import com.topaiebiz.trade.order.core.check.AbstractOrderChecker;
import com.topaiebiz.trade.order.core.check.CompleteChecker;
import com.topaiebiz.trade.order.core.check.PayChecker;
import com.topaiebiz.trade.order.core.check.ReceiveChecker;
import com.topaiebiz.trade.order.dao.OrderDao;
import com.topaiebiz.trade.order.dao.OrderRemarkDao;
import com.topaiebiz.trade.order.dto.common.OrderPageDetailDTO;
import com.topaiebiz.trade.order.dto.customer.CmOrderPageDetailDTO;
import com.topaiebiz.trade.order.dto.platform.PlatformOrderPageDTO;
import com.topaiebiz.trade.order.dto.store.StoreOrderPageDTO;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderAddressEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderRemarkEntity;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/***
 * @author yfeng
 * @date 2018-01-09 16:27
 */
@Slf4j
@Component
public class OrderHelper {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrdersQueryUtil ordersQueryUtil;

    @Autowired
    private ApplicationContext springContext;

    @Autowired
    private OrderRemarkDao orderRemarkDao;

    /**
     * Description: 申请售后，锁住订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/1
     *
     * @param:
     **/
    public void lockOrder(Long orderId) {
        EntityWrapper<OrderEntity> orderWrapper = new EntityWrapper<>();
        orderWrapper.eq("id", orderId);
        orderWrapper.eq("lockState", Constants.OrderLockFlag.LOCK_NO);
        orderWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

        OrderEntity updateEntity = new OrderEntity();
        updateEntity.cleanInit();
        updateEntity.setLockState(Constants.OrderLockFlag.LOCK_YES);
        updateEntity.setLastModifiedTime(new Date());
        if (orderDao.update(updateEntity, orderWrapper) > 0) {
            log.info("----------The order:{} is locked ", orderId);
        }
    }

    /**
     * Description: 售后结束，解锁
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/1
     *
     * @param:
     **/
    public void unLockOrder(Long orderId) {
        EntityWrapper<OrderEntity> orderWrapper = new EntityWrapper<>();
        orderWrapper.eq("id", orderId);
        orderWrapper.eq("lockState", Constants.OrderLockFlag.LOCK_YES);
        orderWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

        OrderEntity updateEntity = new OrderEntity();
        updateEntity.cleanInit();
        updateEntity.setLockState(Constants.OrderLockFlag.LOCK_NO);
        updateEntity.setLastModifiedTime(new Date());
        if (orderDao.update(updateEntity, orderWrapper) > 0) {
            log.info("----------The order:{} is unlocked ", orderId);
        }
    }


    /**
     * Description: 检查判断是否需要修改订单状态
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/21
     *
     * @param:
     **/
    public OrderEntity orderCheck(OrderEntity orderEntity) {
        // 若订单被lock， 不处理
        if (orderEntity.getLockState().equals(Constants.OrderLockFlag.LOCK_YES)) {
            return orderEntity;
        }

        AbstractOrderChecker executer;
        Integer orderState = orderEntity.getOrderState();
        OrderStatusEnum orderStatusEnum = OrderStatusEnum.getByCode(orderState);
        switch (orderStatusEnum) {
            // 未支付，判断是否需要超时取消
            case UNPAY:
                executer = springContext.getBean(PayChecker.class);
                break;
            //待收货，判断是否需要自动收货
            case PENDING_RECEIVED:
                executer = springContext.getBean(ReceiveChecker.class);
                break;
            //已收货，判断是否需要自动完成
            case HAVE_BEEN_RECEIVED:
                executer = springContext.getBean(CompleteChecker.class);
                break;
            default:
                return orderEntity;
        }
        // 判断是否需要修改订单状态，修改之后，重新再查询一次
        if (executer.check(orderEntity)) {
            return ordersQueryUtil.queryOrder(orderEntity.getId());
        }
        return orderEntity;
    }

    // 订单详情页
    public OrderDetailDTO buildOrderDetail(OrderEntity orderEntity) {
        Long orderId = orderEntity.getId();
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        BeanCopyUtil.copy(orderEntity, orderDetailDTO);

        // 查询支付信息
        OrderPayDTO orderPayDTO = this.buildPayInfo(orderEntity.getPayId());
        orderDetailDTO.setOrderPayDTO(orderPayDTO);

        // 支付相关
        String payType = orderEntity.getPayType();
        if (StringUtils.isNotBlank(payType) && !PayMethodEnum.PREDEPOSIT.getName().equals(payType)) {
            orderDetailDTO.setThirdPaymentAmount(orderDetailDTO.getPayPrice().subtract(orderDetailDTO.getBalance()).subtract(orderDetailDTO.getCardPrice()).subtract(orderDetailDTO.getScore()));
            orderDetailDTO.setOuterPaySn(orderPayDTO.getOuterPaySn());
        }

        // 查询订单明细
        orderDetailDTO.setOrderGoodsDTOS(this.buildOrderDetailGoodsDTO(orderId));

        // 查询订单收货地址
        orderDetailDTO.setOrderAddressDTO(ordersQueryUtil.queryOrderAddress(orderId, OrderConstants.HaitaoFlag.YES.equals(orderEntity.getHaitao())));

        // 查询订单发票信息
        orderDetailDTO.setOrderInvoiceDTO(ordersQueryUtil.queryOrderInvoice(orderId));
        return orderDetailDTO;
    }

    // 订单列表 中的订单详情
    public List<OrderPageDetailDTO> buildOrderDetailInPage(List<OrderDetailEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        List<OrderPageDetailDTO> pfOrderDetailDTOS = new ArrayList<>();
        entities.forEach(orderDetailEntity -> {
            OrderPageDetailDTO pfOrderDetailDTO = new OrderPageDetailDTO();
            BeanCopyUtil.copy(orderDetailEntity, pfOrderDetailDTO);
            pfOrderDetailDTOS.add(pfOrderDetailDTO);
        });
        return pfOrderDetailDTOS;
    }

    public void buildPfOrderAddress(List<Long> orderIds, List<PlatformOrderPageDTO> pfOrderPageDTOS) {
        Map<Long, OrderAddressEntity> orderReceiverDTOMap = ordersQueryUtil.queryOrderReceiver(orderIds);
        pfOrderPageDTOS.forEach(pfOrderPageDTO -> {
            OrderAddressEntity orderAddressEntity = orderReceiverDTOMap.get(pfOrderPageDTO.getId());
            if (null != orderAddressEntity) {
                pfOrderPageDTO.setReceiverName(orderAddressEntity.getName());
                pfOrderPageDTO.setTelephone(orderAddressEntity.getTelephone());
            }
        });
    }

    public void setReceiverInfo(StoreOrderPageDTO storeOrderPageDTO, OrderAddressEntity orderAddressEntity) {
        if (null == orderAddressEntity) {
            return;
        }
        storeOrderPageDTO.setReceiverName(orderAddressEntity.getName());
        storeOrderPageDTO.setAddress(StringUtils.join(orderAddressEntity.getProvince(), ".", orderAddressEntity.getCity(), ".", orderAddressEntity.getCounty(), ".", orderAddressEntity.getAddress()));
        storeOrderPageDTO.setMemberIdCard(orderAddressEntity.getIdNum());
        storeOrderPageDTO.setBuyerName(orderAddressEntity.getBuyerName());
        storeOrderPageDTO.setTelephone(orderAddressEntity.getTelephone());
    }

    public void buildStoreOrderAddress(List<Long> orderIds, List<StoreOrderPageDTO> storeOrderPageDTOS) {
        Map<Long, OrderAddressEntity> orderReceiverDTOMap = ordersQueryUtil.queryOrderReceiver(orderIds);
        storeOrderPageDTOS.forEach(storeOrderPageDTO -> {
            OrderAddressEntity orderAddressEntity = orderReceiverDTOMap.get(storeOrderPageDTO.getId());
            if (null != orderAddressEntity) {
                storeOrderPageDTO.setReceiverName(orderAddressEntity.getName());
                storeOrderPageDTO.setAddress(StringUtils.join(orderAddressEntity.getProvince(), ".", orderAddressEntity.getCity(), ".", orderAddressEntity.getCounty(), ".", orderAddressEntity.getAddress()));
                storeOrderPageDTO.setMemberIdCard(orderAddressEntity.getIdNum());
                storeOrderPageDTO.setBuyerName(orderAddressEntity.getBuyerName());
                storeOrderPageDTO.setTelephone(orderAddressEntity.getTelephone());
            }
        });
    }

    public List<CmOrderPageDetailDTO> buildCustomerOrderDetail(List<OrderDetailEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        List<CmOrderPageDetailDTO> cmOrderDetailDTOS = new ArrayList<>();
        entities.forEach(orderDetailEntity -> {
            CmOrderPageDetailDTO cmOrderDetailDTO = new CmOrderPageDetailDTO();
            BeanCopyUtil.copy(orderDetailEntity, cmOrderDetailDTO);
            cmOrderDetailDTO.setGoodsPrice(orderDetailEntity.getGoodsPrice().subtract(orderDetailEntity.getDiscount().divide(new BigDecimal(orderDetailEntity.getGoodsNum()), 2)));
            cmOrderDetailDTOS.add(cmOrderDetailDTO);
        });
        return cmOrderDetailDTOS;
    }

    public List<OrderGoodsDTO> buildOrderDetailGoodsDTO(Long orderId) {
        List<OrderDetailEntity> orderDetailEntities = ordersQueryUtil.queryDetailsByOrderId(orderId);
        return buildOrderDetailGoodsDTO(orderDetailEntities);
    }

    public List<OrderGoodsDTO> buildOrderDetailGoodsDTO(List<OrderDetailEntity> orderDetailEntities) {
        if (CollectionUtils.isEmpty(orderDetailEntities)) {
            return Collections.emptyList();
        }
        List<OrderGoodsDTO> orderGoodsDTOS = new ArrayList<>(orderDetailEntities.size());
        orderDetailEntities.forEach(orderDetailEntity -> {
            OrderGoodsDTO orderGoodsDTO = new OrderGoodsDTO();
            BeanCopyUtil.copy(orderDetailEntity, orderGoodsDTO);
            orderGoodsDTOS.add(orderGoodsDTO);
        });
        return orderGoodsDTOS;
    }

    public OrderPayDTO buildPayInfo(Long payId) {
        OrderPayEntity orderPayEntity = ordersQueryUtil.queryOrderPay(payId);
        OrderPayDTO orderPayDTO = new OrderPayDTO();
        BeanCopyUtil.copy(orderPayEntity, orderPayDTO);
        orderPayDTO.setThirdPaymentAmount(orderPayDTO.getPayPrice().subtract(orderPayDTO.getBalance()).subtract(orderPayDTO.getCardPrice()).subtract(orderPayDTO.getScorePrice()));
        return orderPayDTO;
    }

    public OrderPayDTO buildCustomerPayInfo(OrderEntity orderEntity) {
        OrderPayEntity orderPayEntity = ordersQueryUtil.queryOrderPay(orderEntity.getPayId());
        OrderPayDTO orderPayDTO = new OrderPayDTO();
        orderPayDTO.setId(orderPayEntity.getId());
        orderPayDTO.setPayPrice(orderEntity.getPayPrice());
        orderPayDTO.setPayState(orderPayEntity.getPayState());
        orderPayDTO.setPayType(orderPayEntity.getPayType());
        orderPayDTO.setPayTime(orderPayEntity.getPayTime());
        orderPayDTO.setOuterPaySn(orderPayEntity.getOuterPaySn());
        orderPayDTO.setCardPrice(orderEntity.getCardPrice());
        orderPayDTO.setScoreNum(orderEntity.getScoreNum());
        orderPayDTO.setScorePrice(orderEntity.getScore());
        orderPayDTO.setBalance(orderEntity.getBalance());
        orderPayDTO.setThirdPaymentAmount(orderEntity.getPayPrice().subtract(orderEntity.getBalance()).subtract(orderEntity.getCardPrice()).subtract(orderEntity.getScore()));
        return orderPayDTO;
    }

    public String getLatestRemarks(Long orderId) {
        if (null == orderId) {
            return null;
        }
        EntityWrapper<OrderRemarkEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("orderId", orderId);
        wrapper.orderBy("createTime", false);
        wrapper.last("limit 1");

        List<OrderRemarkEntity> remarkEntities = orderRemarkDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(remarkEntities)) {
            return null;
        }
        OrderRemarkEntity remarkEntity = remarkEntities.get(0);
        return remarkEntity.getRemark();
    }

    public OrderAddressDTO buildOrderAddressDTO(OrderAddressEntity orderAddressEntity) {
        if (null == orderAddressEntity) {
            return null;
        }
        OrderAddressDTO orderAddressDTO = new OrderAddressDTO();
        orderAddressDTO.setMemberIdCard(orderAddressEntity.getIdNum());
        BeanCopyUtil.copy(orderAddressEntity, orderAddressDTO);
        orderAddressDTO.setAddress(StringUtils.join(orderAddressEntity.getProvince(), ".", orderAddressEntity.getCity(), ".", orderAddressEntity.getCounty(), ".", orderAddressEntity.getAddress()));
        orderAddressDTO.setDetailAddress(orderAddressDTO.getAddress());
        return orderAddressDTO;
    }
}