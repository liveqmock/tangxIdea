package com.topaiebiz.transaction.order.merchant.service.impl;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.order.dao.OrderInvoiceDao;
import com.topaiebiz.transaction.order.merchant.dao.StoreOrderDao;
import com.topaiebiz.transaction.order.merchant.dto.OrderInvoiceDto;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderInvoiceEntity;
import com.topaiebiz.transaction.order.merchant.exception.StoreOrderExceptionEnum;
import com.topaiebiz.transaction.order.merchant.service.OrderInvoiceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description 订单发票的接口实现
 * <p>
 * <p>
 * Author zhushuyong
 * <p>
 * Date 2017年9月4日 下午3:34:21
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
@Transactional
public class OrderInvoiceServiceImpl implements OrderInvoiceService {

    @Autowired
    private OrderInvoiceDao orderInvoiceDao;

    @Autowired
    private StoreOrderDao storeOrderDao;

    @Override
    public OrderInvoiceDto queryOrderInvoice(Long orderId) {
        OrderEntity orderEntity = storeOrderDao.selectById(orderId);
        if (orderEntity == null) {
            throw new GlobalException(StoreOrderExceptionEnum.STOREORDER_ID_NOT_EXIST);
        }
        OrderInvoiceEntity entity = orderInvoiceDao.selectOrderInvoice(orderId);
        OrderInvoiceDto dto = new OrderInvoiceDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

}
