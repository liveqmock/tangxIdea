package com.topaiebiz.trade.order.util;

import com.topaiebiz.trade.dto.order.OrderAddressDTO;
import com.topaiebiz.trade.dto.order.OrderDTO;
import com.topaiebiz.trade.dto.order.OrderGoodsDTO;
import com.topaiebiz.trade.dto.order.OrderPayDTO;
import com.topaiebiz.trade.dto.order.openapi.APIOrderDetailDTO;
import com.topaiebiz.trade.dto.order.openapi.APIOrderSkuDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description open api 订单分页-- 参数转换帮助类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/5/7 20:39
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public class OrderPageForApiUtil {

    public static List<APIOrderDetailDTO> buildOrderPage(List<OrderDTO> orderDTOS, Map<Long, List<OrderGoodsDTO>> orderDetailMap, Map<Long, OrderAddressDTO> orderAddressMap, Map<Long, OrderPayDTO> orderPayMap) {
        List<APIOrderDetailDTO> apiOrderDetailDTOS = new ArrayList<>(orderDTOS.size());
        for (OrderDTO orderDTO : orderDTOS) {
            Long orderId = orderDTO.getId();
            APIOrderDetailDTO apiOrderDetailDTO = new APIOrderDetailDTO();
            buildAPIOrderDetailDTO(apiOrderDetailDTO, orderDTO, orderDetailMap.get(orderId), orderAddressMap.get(orderId), orderPayMap.get(orderId));
            apiOrderDetailDTOS.add(apiOrderDetailDTO);
        }
        return apiOrderDetailDTOS;
    }

    /**
     * Description: 拼装订单详情
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/27
     *
     * @param:
     **/
    private static void buildAPIOrderDetailDTO(APIOrderDetailDTO apiOrderDetailDTO, OrderDTO orderDTO, List<OrderGoodsDTO> orderGoodsDTOS, OrderAddressDTO orderAddressDTO, OrderPayDTO orderPayDTO) {
        // 订单信息
        apiOrderDetailDTO.setOrderId(orderDTO.getId());
        apiOrderDetailDTO.setMemberName(orderDTO.getMemberName());
        apiOrderDetailDTO.setMemberTelephone(orderDTO.getMemberTelephone());
        apiOrderDetailDTO.setStoreId(orderDTO.getStoreId());
        apiOrderDetailDTO.setStoreName(orderDTO.getStoreName());
        apiOrderDetailDTO.setPayId(orderDTO.getPayId());
        apiOrderDetailDTO.setPayType(orderDTO.getPayType());
        apiOrderDetailDTO.setOrderTime(orderDTO.getOrderTime());
        apiOrderDetailDTO.setPayTime(orderDTO.getPayTime());
        apiOrderDetailDTO.setShipmentTime(orderDTO.getShipmentTime());
        apiOrderDetailDTO.setReceiveTime(orderDTO.getReceiveTime());
        apiOrderDetailDTO.setCompleteTime(orderDTO.getCompleteTime());
        apiOrderDetailDTO.setOrderState(orderDTO.getOrderState());
        apiOrderDetailDTO.setInvoiceState(orderDTO.getInvoiceState());
        apiOrderDetailDTO.setLockState(orderDTO.getLockState());
        apiOrderDetailDTO.setExtendShip(orderDTO.getExtendShip());
        apiOrderDetailDTO.setStoreDiscount(orderDTO.getStoreDiscount());
        apiOrderDetailDTO.setStoreCouponDiscount(orderDTO.getStoreCouponDiscount());
        apiOrderDetailDTO.setPlatformDiscount(orderDTO.getPlatformDiscount());
        apiOrderDetailDTO.setDiscountTotal(orderDTO.getDiscountTotal());
        apiOrderDetailDTO.setGoodsTotalAmount(orderDTO.getGoodsTotal());
        apiOrderDetailDTO.setFreightTotalAmount(orderDTO.getFreightTotal());
        apiOrderDetailDTO.setActualFreightAmount(orderDTO.getActualFreight());
        apiOrderDetailDTO.setPayAmount(orderDTO.getPayPrice());
        apiOrderDetailDTO.setScoreNum(orderDTO.getScoreNum());
        apiOrderDetailDTO.setScoreAmount(orderDTO.getScore());
        apiOrderDetailDTO.setBalance(orderDTO.getBalance());
        apiOrderDetailDTO.setCardAmount(orderDTO.getCardPrice());
        apiOrderDetailDTO.setThirdPaymentAmount(getThirdPaymentAmount(orderDTO));
        if (null != orderPayDTO) {
            apiOrderDetailDTO.setOuterPaySn(orderPayDTO.getOuterPaySn());
        }
        apiOrderDetailDTO.setHaitao(orderDTO.getHaitao());
        apiOrderDetailDTO.setMemo(orderDTO.getMemo());

        // 订单明细
        List<APIOrderSkuDTO> apiOrderSkuDTOS;
        if (CollectionUtils.isEmpty(orderGoodsDTOS)) {
            apiOrderSkuDTOS = Collections.emptyList();
        } else {
            apiOrderSkuDTOS = new ArrayList<>(orderGoodsDTOS.size());
            for (OrderGoodsDTO orderGoodsDTO : orderGoodsDTOS) {
                apiOrderSkuDTOS.add(new APIOrderSkuDTO(orderGoodsDTO));
            }
        }
        apiOrderDetailDTO.setOrderSkus(apiOrderSkuDTOS);

        // 订单收货信息
        if (null != orderAddressDTO) {
            apiOrderDetailDTO.setReceiverName(orderAddressDTO.getName());
            apiOrderDetailDTO.setReceiverProvince(orderAddressDTO.getProvince());
            apiOrderDetailDTO.setReceiverCity(orderAddressDTO.getCity());
            apiOrderDetailDTO.setReceiverCounty(orderAddressDTO.getCounty());
            apiOrderDetailDTO.setReceiverTelephone(orderAddressDTO.getTelephone());
            apiOrderDetailDTO.setReceiverDetailAddress(orderAddressDTO.getDetailAddress());
            apiOrderDetailDTO.setBuyerIdCard(orderAddressDTO.getMemberIdCard());
            apiOrderDetailDTO.setBuyerName(orderAddressDTO.getBuyerName());
        }

        // 物流信息
        if (CollectionUtils.isNotEmpty(orderGoodsDTOS)) {
            apiOrderDetailDTO.setExpressComName(orderGoodsDTOS.get(0).getExpressComName());
            apiOrderDetailDTO.setExpressNo(orderGoodsDTOS.get(0).getExpressNo());
        }
    }

    private static BigDecimal getThirdPaymentAmount(OrderDTO orderDTO) {
        BigDecimal thirdPaymentAmount = orderDTO.getPayPrice();

        if (null != orderDTO.getCardPrice()) {
            thirdPaymentAmount = thirdPaymentAmount.subtract(orderDTO.getCardPrice());
        }
        if (null != orderDTO.getBalance()) {
            thirdPaymentAmount = thirdPaymentAmount.subtract(orderDTO.getBalance());
        }
        if (null != orderDTO.getScore()) {
            thirdPaymentAmount = thirdPaymentAmount.subtract(orderDTO.getScore());
        }
        return thirdPaymentAmount;
    }

}
