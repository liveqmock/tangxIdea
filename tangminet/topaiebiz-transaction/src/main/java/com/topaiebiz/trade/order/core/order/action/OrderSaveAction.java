package com.topaiebiz.trade.order.core.order.action;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.enumdata.PayMethodEnum;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.member.dto.address.MemberAddressDto;
import com.topaiebiz.trade.cart.util.CartHelper;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.dto.order.GoodsPromotionDetailDTO;
import com.topaiebiz.trade.dto.pay.GoodPayDTO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderGoodsBO;
import com.topaiebiz.trade.order.core.order.context.AddressContext;
import com.topaiebiz.trade.order.core.order.context.CartIdContext;
import com.topaiebiz.trade.order.core.order.context.OrderSummaryContext;
import com.topaiebiz.trade.order.core.order.context.PayIdContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.dao.*;
import com.topaiebiz.trade.order.dto.ordersubmit.OrderSummaryDTO;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestInvoice;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestStore;
import com.topaiebiz.trade.order.util.HaiTaoUtil;
import com.topaiebiz.trade.order.util.OrderUtil;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderAddressEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderInvoiceEntity;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

import static com.topaiebiz.trade.constants.OrderConstants.DeliyerType.EXPRESS;
import static com.topaiebiz.trade.constants.OrderConstants.OrderInvoiceStatus.NEED;
import static com.topaiebiz.trade.constants.OrderConstants.OrderInvoiceStatus.NO_NEED;
import static com.topaiebiz.trade.constants.OrderConstants.OrderLockStatus.NO_LOCK;
import static com.topaiebiz.trade.constants.OrderConstants.OrderRefundStatus.NO_REFUND;
import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.ORDER_FAIL_ERROR;

/***
 * @author yfeng
 * @date 2018-01-09 14:34
 */
@Component("orderSaveAction")
@Slf4j
public class OrderSaveAction extends AbstractAction {

    @Autowired
    private OrderPayDao orderPayDao;
    @Autowired
    private OrderAddressDao orderAddressDao;
    @Autowired
    private OrderInvoiceDao orderInvoiceDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderDetailDao orderDetailDao;
    @Autowired
    private CartHelper cartHelper;

    @Override
    public boolean action(BuyerBO buyer, OrderSubmitContext paramContext, OrderRequest orderRequest) {
        try {
            OrderSummaryDTO orderSummary = OrderSummaryContext.get();
            return doAction(orderSummary, buyer, paramContext, orderRequest);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new GlobalException(ORDER_FAIL_ERROR);
        }
    }

    public boolean doAction(OrderSummaryDTO orderSummary, BuyerBO buyer, OrderSubmitContext paramContext, OrderRequest orderRequest) {
        //setp 1 : 保存支付订单信息
        OrderPayEntity orderPay = saveOrderPay(orderSummary, buyer, paramContext);
        log.info("save order pay : {}", JSON.toJSONString(orderPay));
        PayIdContext.set(orderPay.getId());

        //step 2 : 保存每个订单信息
        for (OrderRequestStore storeRequest : orderRequest.getOrders()) {
            StoreOrderBO storeOrderBO = paramContext.getStoreOrderMap().get(storeRequest.getStoreId());

            //step 2.1 : 保存订单
            OrderEntity order = saveStoreOrder(orderSummary, buyer, storeOrderBO, paramContext, orderRequest, storeRequest);

            //step 2.2 : 保存收货地址信息
            MemberAddressDto addressDto = AddressContext.get();
            saveAddress(buyer, order, addressDto, orderRequest);

            //step 2.3 : 保存发票信息
            saveInvoice(buyer, order, orderRequest.getInvoice());

            //step 2.4 : 保存商品
            for (StoreOrderGoodsBO orderGoodsBO : storeOrderBO.getGoodsList()) {
                saveOrderDetail(orderGoodsBO, buyer, order);
            }
        }
        //step 3 : 移除购物车
        cartHelper.removeCarts(buyer.getMemberId(), CartIdContext.get());
        return true;
    }

    private void saveOrderDetail(StoreOrderGoodsBO orderGoodsBO, BuyerBO buyer, OrderEntity order) {
        OrderDetailEntity detail = new OrderDetailEntity();
        GoodsSkuDTO skuDTO = orderGoodsBO.getGoods();
        ItemDTO item = skuDTO.getItem();

        detail.setMemberId(buyer.getMemberId());
        detail.setOrderId(order.getId());
        detail.setOrderState(order.getOrderState());
        detail.setItemId(item.getId());
        detail.setSkuId(skuDTO.getId());
        detail.setSpuId(skuDTO.getSpuId());
        detail.setName(item.getName());
        // 增加商品条形码
        detail.setBarCode(skuDTO.getBarCode());

        BigDecimal brokerageRation = item.getBrokerageRatio() == null ? BigDecimal.ZERO : new BigDecimal(item.getBrokerageRatio());
        detail.setBrokerageRatio(brokerageRation);
        detail.setFieldValue(skuDTO.getSaleFieldValue());
        detail.setGoodsPrice(skuDTO.getPrice());
        detail.setGoodsImage(skuDTO.getSaleImage());
        detail.setGoodsSerial(skuDTO.getArticleNumber());
        detail.setItemCode(item.getItemCode());
        detail.setScoreRate(skuDTO.getScoreRate());
        detail.setGoodsNum(orderGoodsBO.getGoodsNum());
        detail.setTotalPrice(orderGoodsBO.getGoodsAmount());
        BigDecimal taxRate = item.getTaxRate() == null ? BigDecimal.ZERO : item.getTaxRate();
        detail.setTaxRate(taxRate);
        log.info("save skuId : {} taxRate:{}", detail.getSkuId(), taxRate);
        if (orderGoodsBO.getPromotionId() != null) {
            detail.setPromotionId(orderGoodsBO.getPromotionId());
        }
        detail.setDiscount(orderGoodsBO.getGoodsDiscount());

        //优惠详情
        GoodsPromotionDetailDTO proDetail = new GoodsPromotionDetailDTO();
        BeanCopyUtil.copy(orderGoodsBO, proDetail);
        //店铺级别活动优惠幅度（店铺优惠券和店铺的满减）
        proDetail.setStoreDiscount(orderGoodsBO.getStoreDiscount().add(orderGoodsBO.getStoreCouponDiscount()));
        detail.setPromotionDetail(JSON.toJSONString(proDetail));

        detail.setFreight(orderGoodsBO.getFinalFreight());
        detail.setPayPrice(orderGoodsBO.getPayAmount());
        detail.setRefundState(order.getRefundState());

        detail.setPayPrice(orderGoodsBO.getPayAmount());
        detail.setPayDetail(JSON.toJSONString(new GoodPayDTO()));
        orderDetailDao.insert(detail);
    }

    private void saveInvoice(BuyerBO buyer, OrderEntity order, OrderRequestInvoice invoice) {
        if (invoice == null) {
            return;
        }
        OrderInvoiceEntity invoiceEntity = new OrderInvoiceEntity();
        BeanCopyUtil.copy(invoice, invoiceEntity);
        invoiceEntity.setState(OrderConstants.InvoiceStatus.UNDEAL);
        invoiceEntity.setOrderId(order.getId());
        invoiceEntity.setStoreId(order.getStoreId());
        invoiceEntity.setCreatorId(buyer.getMemberId());
        orderInvoiceDao.insert(invoiceEntity);
    }

    private void saveAddress(BuyerBO buyer, OrderEntity order, MemberAddressDto addressDto, OrderRequest orderRequest) {
        OrderAddressEntity addr = new OrderAddressEntity();
        BeanCopyUtil.copy(addressDto, addr);
        addr.setProvince(addressDto.getProvinceName());
        addr.setCity(addressDto.getCityName());
        addr.setCounty(addressDto.getDistrictName());
        // 保存用户地址主键ID
        addr.setMemberAddressId(addressDto.getId());
        addr.setId(null);
        addr.setOrderId(order.getId());
        addr.setCreatorId(buyer.getMemberId());
        addr.setIdNum(orderRequest.getIdNum());
        addr.setBuyerName(orderRequest.getBuyerName());
        orderAddressDao.insert(addr);
    }

    private OrderEntity saveStoreOrder(OrderSummaryDTO orderSummary, BuyerBO buyer, StoreOrderBO storeOrderBO, OrderSubmitContext paramContext, OrderRequest orderRequest, OrderRequestStore storeRequest) {
        Long payId = PayIdContext.get();
        OrderEntity order = new OrderEntity();
        order.setCreatorId(buyer.getMemberId());
        order.setMemberId(buyer.getMemberId());
        order.setMemberName(buyer.getMemberName());
        order.setMemberTelephone(buyer.getMobile());
        order.setStoreId(storeOrderBO.getStore().getId());
        order.setStoreName(storeOrderBO.getStore().getName());
        order.setIp(orderRequest.getIp());
        order.setUserAgent(OrderUtil.orderUserAgent(orderRequest.getUserAgent()));
        order.setPayId(payId);
        order.setOrderTime(new Date());

        if (orderSummary.zeroAmountOrder()) {
            //0元订单直接变成待发货状态
            order.setOrderState(OrderStatusEnum.PENDING_DELIVERY.getCode());
            order.setPayTime(new Date());
            order.setPayType(PayMethodEnum.PREDEPOSIT.getName());
        } else {
            order.setOrderState(OrderStatusEnum.UNPAY.getCode());
        }

        order.setInvoiceState(orderRequest.getInvoice() == null ? NO_NEED : NEED);
        order.setRefundState(NO_REFUND);
        order.setLockState(NO_LOCK);
        order.setDeliveryType(EXPRESS);
        order.setExtendShip(Constants.Order.EXTEND_SHIP_NO);
        order.setCommentFlag(Constants.Order.COMMENT_NO);
        //海淘标记
        Integer haitaoFlag = HaiTaoUtil.isHaitaoStore(storeOrderBO.getStore()) ? OrderConstants.HaitaoFlag.YES : OrderConstants.HaitaoFlag.NO;
        order.setHaitao(haitaoFlag);

        //商品总金额
        order.setGoodsTotal(storeOrderBO.getGoodsAmount());

        //运费信息
        order.setFreightTotal(storeOrderBO.getGoodsFreight());
        if (storeOrderBO.getFreightPromotion() != null) {
            order.setFreightPromotionId(storeOrderBO.getFreightPromotion().getId());
            order.setFreightDiscount(storeOrderBO.getFreightDiscount());
        } else {
            order.setFreightPromotionId(0L);
            order.setFreightDiscount(BigDecimal.ZERO);
        }
        order.setActualFreight(storeOrderBO.getGoodsFreight());

        //店铺优惠:秒杀
        if (storeOrderBO.getStorePromotion() != null) {
            order.setStorePromotionId(storeOrderBO.getStorePromotion().getId());
        } else {
            order.setStorePromotionId(0L);
        }
        order.setStoreDiscount(storeOrderBO.getStoreDiscount());

        //店铺优惠券
        if (storeOrderBO.getStoreCoupon() != null) {
            order.setStoreCouponId(storeOrderBO.getStoreCoupon().getId());
        } else {
            order.setStoreCouponId(0L);
        }
        order.setStoreCouponDiscount(storeOrderBO.getStoreCouponDiscount());

        //平台优惠
        if (paramContext.getPlatformPromotion() != null) {
            order.setPlatformPromotionId(paramContext.getPlatformPromotion().getId());
        } else {
            order.setPlatformPromotionId(0L);
        }
        order.setPlatformDiscount(storeOrderBO.getPlatformDiscount());

        //订单总金额
        order.setCardPrice(BigDecimal.ZERO);
        order.setBalance(BigDecimal.ZERO);
        order.setScore(BigDecimal.ZERO);
        order.setScoreNum(0L);
        order.setDiscountTotal(storeOrderBO.getTotalDiscountAmount());
        order.setOrderTotal(storeOrderBO.getOrderTotal());
        order.setPayPrice(storeOrderBO.getPayPrice());
        order.setMemo(storeRequest.getOrderMessage());
        orderDao.insert(order);

        //回写OrderId
        storeOrderBO.setOrderId(order.getId());
        return order;
    }

    /**
     * 保存支付订单
     */
    private OrderPayEntity saveOrderPay(OrderSummaryDTO orderSummary, BuyerBO buyer, OrderSubmitContext paramContext) {
        OrderPayEntity orderPay = new OrderPayEntity();
        orderPay.setMemberId(buyer.getMemberId());
        orderPay.setPayPrice(orderSummary.getPayAmount());
        orderPay.setBalance(BigDecimal.ZERO);
        orderPay.setCardPrice(BigDecimal.ZERO);
        orderPay.setScorePrice(BigDecimal.ZERO);
        orderPay.setScoreNum(0L);

        if (orderSummary.zeroAmountOrder()) {
            orderPay.setPayState(OrderConstants.PayStatus.SUCCESS);
            orderPay.setPayTime(new Date());
        } else {
            orderPay.setPayState(OrderConstants.PayStatus.UNPAY);
        }
        orderPay.setCreatorId(buyer.getMemberId());
        orderPayDao.insert(orderPay);
        return orderPay;
    }

    @Override
    public void rollback(BuyerBO buyer, OrderSubmitContext paramContext, OrderRequest orderRequest) {
        throw new GlobalException(ORDER_FAIL_ERROR);
    }
}