package com.topaiebiz.trade.order.api.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.enumdata.PayMethodEnum;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.api.order.OrderPayServiceApi;
import com.topaiebiz.trade.dto.order.OrderPayDTO;
import com.topaiebiz.trade.dto.order.PayInfoDTO;
import com.topaiebiz.trade.order.dao.OrderPayDao;
import com.topaiebiz.trade.order.exception.OrderExceptionEnum;
import com.topaiebiz.trade.order.service.OrderPayService;
import com.topaiebiz.trade.order.util.PayOrderHelper;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description 订单对外接口实现类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/17 11:51
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Service
public class OrderPayServiceApiImpl implements OrderPayServiceApi {

    @Autowired
    private OrderPayService orderPayService;

    @Autowired
    private OrderPayDao payDao;
    @Autowired
    private PayOrderHelper payOrderHelper;

    @Autowired
    private MessageSender messageSender;

    @Override
    public PayInfoDTO queryUnpayOrder(Long memberId, Long payId) {
        OrderPayEntity cond = new OrderPayEntity();
        cond.cleanInit();
        cond.setMemberId(memberId);
        cond.setId(payId);

        OrderPayEntity orderPayEntity = payDao.selectOne(cond);
        if (null == orderPayEntity) {
            throw new GlobalException(OrderExceptionEnum.ORDER_CANT_BE_FOUND);
        }
        BigDecimal payPrice = payOrderHelper.needPay(orderPayEntity);

        PayInfoDTO orderPayDTO = new PayInfoDTO();
        orderPayDTO.setPayId(orderPayEntity.getId());
        orderPayDTO.setPayPrice(payPrice);
        orderPayDTO.setPayState(orderPayEntity.getPayState());
        return orderPayDTO;
    }

    @Override
    public Boolean payNotify(Long payOrderId, PayMethodEnum payMethod, BigDecimal amount, String outTradeNo) {
        return orderPayService.payNotify(payOrderId, payMethod.getName(), amount, outTradeNo);
    }

    @Override
    public Map<Long, OrderPayDTO> queryPayInfos(List<Long> payIds) {
        if (CollectionUtils.isEmpty(payIds)) {
            return Collections.emptyMap();
        }
        EntityWrapper<OrderPayEntity> cond = new EntityWrapper<>();
        cond.in("id", payIds);
        List<OrderPayEntity> payEntities = payDao.selectList(cond);
        if (CollectionUtils.isEmpty(payEntities)) {
            return Collections.emptyMap();
        }
        List<OrderPayDTO> payDTOS = PageDataUtil.copyList(payEntities, OrderPayDTO.class);
        Map<Long, OrderPayDTO> map = payDTOS.stream().collect(Collectors.toMap(OrderPayDTO::getId, item -> item));
        return map;
    }

    @Override
    public OrderPayDTO getPayInfo(Long payId) {
        OrderPayEntity entity = payDao.selectById(payId);
        if (entity == null) {
            return null;
        }

        OrderPayDTO orderPay = new OrderPayDTO();
        BeanCopyUtil.copy(entity, orderPay);
        return orderPay;
    }
}
