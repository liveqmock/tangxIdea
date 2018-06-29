package com.topaiebiz.openapi.service.impl;

import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.enumdata.PayMethodEnum;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.openapi.dao.OpenApiOrderPayDao;
import com.topaiebiz.openapi.dao.OpenApiStoreResourceDao;
import com.topaiebiz.openapi.entity.OpenApiOrderPayEntity;
import com.topaiebiz.openapi.entity.OpenApiStoreResourceEntity;
import com.topaiebiz.openapi.exception.OpenApiExceptionEnum;
import com.topaiebiz.openapi.service.OrderService;
import com.topaiebiz.pay.api.PaymentApi;
import com.topaiebiz.pay.dto.ReportCustomsDTO;
import com.topaiebiz.trade.api.order.OrderServiceApi;
import com.topaiebiz.trade.dto.order.OrderAddressDTO;
import com.topaiebiz.trade.dto.order.OrderDetailDTO;
import com.topaiebiz.trade.dto.order.OrderPayDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ward on 2018-03-01.
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderServiceApi orderServiceApi;

    @Autowired
    private OpenApiOrderPayDao openApiOrderPayDao;

    @Autowired
    private OpenApiStoreResourceDao openApiStoreResourceDao;

    @Autowired
    private PaymentApi paymentApi;


    /**
     * 报关是否成功
     */
    private final static Integer PUSH_PAYMENT_YES = 1;
    private final static Integer PUSH_PAYMENT_NO = 0;

    @Override
    public OrderDetailDTO queryOrderDetail(Long orderId) {
        return orderServiceApi.orderDetail(orderId);
    }

    private OpenApiOrderPayEntity getOpenapiOrderPay(Long orderId) {
        OpenApiOrderPayEntity param = new OpenApiOrderPayEntity();
        param.cleanInit();
        param.setOrderId(orderId);
        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        OpenApiOrderPayEntity openApiOrderPayEntity;
        try {
            openApiOrderPayEntity = openApiOrderPayDao.selectOne(param);
        } catch (Exception e) {
            log.error("getOpenapiOrderPay 订单ID不唯一err={}", e.getMessage());
            throw new GlobalException(OpenApiExceptionEnum.ORDER_ID_NO_UNIQUE);
        }
        if (null == openApiOrderPayEntity) {
            throw new GlobalException(OpenApiExceptionEnum.ORDER_PAY_NULL);
        }
        return openApiOrderPayEntity;
    }

    private OpenApiStoreResourceEntity getOpenapiStoreResource(Long storeId) {
        if (null == storeId || storeId < 1) {
            throw new GlobalException(OpenApiExceptionEnum.STORE_RESOURCE_IS_NOT_FOUND);
        }
        OpenApiStoreResourceEntity condition = new OpenApiStoreResourceEntity();
        condition.cleanInit();
        condition.setStoreId(storeId);
        condition.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        OpenApiStoreResourceEntity openApiStoreResourceEntity = openApiStoreResourceDao.selectOne(condition);
        if (null == openApiStoreResourceEntity) {
            throw new GlobalException(OpenApiExceptionEnum.STORE_RESOURCE_IS_NOT_FOUND);
        }
        return openApiStoreResourceEntity;
    }

    private void updatePushPaymentStatus(Long orderPayId, Integer pushPaymentStatus) {
        OpenApiOrderPayEntity updateEntity = new OpenApiOrderPayEntity();
        updateEntity.cleanInit();
        updateEntity.setId(orderPayId);
        updateEntity.setPushPaymentStatus(pushPaymentStatus);
        updateEntity.setPushPaymentRes("");
        openApiOrderPayDao.updateById(updateEntity);
    }


    @Override
    public Boolean pushPaymentToCustom(Long orderId) {
        //1.查询是否可以推送
        OpenApiOrderPayEntity openApiOrderPayEntity = getOpenapiOrderPay(orderId);
        if (PUSH_PAYMENT_YES.equals(openApiOrderPayEntity.getPushPaymentStatus())) {
            throw new GlobalException(OpenApiExceptionEnum.ORDER_PAY_HAD_PUSH_PAYMENT);
        }

        OpenApiStoreResourceEntity storeResourceEntity = getOpenapiStoreResource(openApiOrderPayEntity.getStoreId());
        if (null == storeResourceEntity.getNeedPushCustom()
                || !Constants.OpenApiPush.NEED_EXPORT_CUSTOMS.equals(storeResourceEntity.getNeedPushCustom())) {
            throw new GlobalException(OpenApiExceptionEnum.ORDER_PAY_CANT_PUSH_PAYMENT);
        }

        try {
            OrderAddressDTO orderAddressDTO = orderServiceApi.queryOrderAddressById(orderId);
            OrderPayDTO orderPayDTO = orderServiceApi.queryOrderPayInfoById(openApiOrderPayEntity.getPayId());
            ReportCustomsDTO reportCustomsDTO = new ReportCustomsDTO();
            reportCustomsDTO.setOrderId(orderId);
            if (orderPayDTO.getPayType().equals(PayMethodEnum.ALIPAY.getName())) {
                //支付宝
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                String reportId = StringUtils.join(simpleDateFormat.format(new Date()), orderPayDTO.getId());
                //报关ID
                reportCustomsDTO.setReportId(reportId);
                log.warn("pushPaymentToCustom: 支付宝报关ID  ReportId={}", reportId);
                reportCustomsDTO.setPayMethodEnum(PayMethodEnum.ALIPAY);
                reportCustomsDTO.setBuyerIdNo(orderAddressDTO.getMemberIdCard());
                reportCustomsDTO.setBuyerName(orderAddressDTO.getName());
                reportCustomsDTO.setAmount(orderPayDTO.getThirdPaymentAmount().toPlainString());
                reportCustomsDTO.setThirdTradeNo(orderPayDTO.getOuterPaySn());
            } else if (orderPayDTO.getPayType().equals(PayMethodEnum.WX_JSAPI.getName())) {
                //微信
                reportCustomsDTO.setPayMethodEnum(PayMethodEnum.WX_JSAPI);
                reportCustomsDTO.setOutTradeNo(StringUtils.join(orderId, "_", Constants.Order.ORDER_TYPE_GOOD));
                reportCustomsDTO.setTransactionId(orderPayDTO.getOuterPaySn());
            } else {
                log.error("pushPaymentToCustom:>>>>>>>>>报关订单无第三方支付！报关失败！orderId={}", orderId);
                throw new GlobalException(OpenApiExceptionEnum.NOT_SUPPORT_NOW);
            }
            paymentApi.reportCustoms(reportCustomsDTO);
        } catch (GlobalException e) {
            updatePushPaymentStatus(openApiOrderPayEntity.getId(), PUSH_PAYMENT_NO);
            log.error("pushPaymentToCustom 执行异常err={}", e.getMessage());
            throw e;
        }
        updatePushPaymentStatus(openApiOrderPayEntity.getId(), PUSH_PAYMENT_YES);
        return true;
    }


}
