package com.topaiebiz.openapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.enumdata.PayMethodEnum;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.openapi.contants.OpenApiContants;
import com.topaiebiz.openapi.dao.OpenApiOrderPayDao;
import com.topaiebiz.openapi.dao.OpenApiOrderRefundDao;
import com.topaiebiz.openapi.entity.OpenApiOrderPayEntity;
import com.topaiebiz.openapi.entity.OpenApiOrderRefundEntity;
import com.topaiebiz.openapi.entity.OpenApiStoreResourceEntity;
import com.topaiebiz.openapi.exception.OpenApiExceptionEnum;
import com.topaiebiz.openapi.service.OpenApiOrderService;
import com.topaiebiz.openapi.utils.MmgOpenApiUtil;
import com.topaiebiz.openapi.utils.StoreResourceUtil;
import com.topaiebiz.pay.api.PaymentApi;
import com.topaiebiz.pay.dto.ReportCustomsDTO;
import com.topaiebiz.trade.api.order.OrderServiceApi;
import com.topaiebiz.trade.dto.order.OrderAddressDTO;
import com.topaiebiz.trade.dto.order.OrderDetailDTO;
import com.topaiebiz.trade.dto.order.OrderPayDTO;
import com.topaiebiz.trade.dto.order.PushOrderParamsDTO;
import com.topaiebiz.trade.dto.order.openapi.APIOrderDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/2 14:36
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class OpenApiOrderServiceImpl implements OpenApiOrderService {

    @Autowired
    private OpenApiOrderPayDao openApiOrderPayDao;

    @Autowired
    private OpenApiOrderRefundDao openApiOrderRefundDao;

    @Autowired
    private OrderServiceApi orderServiceApi;

    @Autowired
    private StoreResourceUtil storeResourceUtil;

    @Autowired
    private PaymentApi paymentApi;

    @Autowired
    private MmgOpenApiUtil mmgOpenApiUtil;

    @Override
    public void saveOrderCreateMessage(Long payId) {

        PushOrderParamsDTO pushOrderParamsDTO = orderServiceApi.queryOrdersByPayId(payId);
        if (null == pushOrderParamsDTO) {
            log.warn(">>>>>>>>>>saveOrderCreateMessage fail : don't found the orders!");
            return;
        }
        log.warn(">>>>>>>>>>query orderIds and storeIds result:{}", JSON.toJSONString(pushOrderParamsDTO));
        Map<Long, Set<Long>> map = pushOrderParamsDTO.getOrderIds();
        OpenApiStoreResourceEntity openApiStoreResourceEntity = null;

        List<Long> orderIds = new ArrayList<>();
        for (Map.Entry<Long, Set<Long>> entry : map.entrySet()) {
            // 保存条数
            int rows = 0;
            Long storeId = entry.getKey();
            openApiStoreResourceEntity = storeResourceUtil.getByStoreId(storeId);
            // 未配置商家资源，或者没有推单地址， 则不保存。
            if (null == openApiStoreResourceEntity || StringUtils.isBlank(openApiStoreResourceEntity.getOrderCreateUrl())) {
                continue;
            }
            for (Long orderId : entry.getValue()) {
                OpenApiOrderPayEntity entity = new OpenApiOrderPayEntity(payId);
                entity.setStoreId(storeId);
                entity.setOrderId(orderId);
                if (openApiOrderPayDao.insert(entity) > 0) {
                    rows++;
                    orderIds.add(orderId);
                }
            }
            log.warn(">>>>>>>>>>save store:{} has {} order pay's message success!", storeId, rows);


            //下面进行报关
            if (pushOrderParamsDTO.isHaiTao() && Constants.OpenApiPush.NEED_EXPORT_CUSTOMS.equals(openApiStoreResourceEntity.getNeedPushCustom())) {
                // 如果是海淘，则只存在一个支付单号 只包含一个订单号
                try {
                    Long orderId = orderIds.get(0);
                    OrderAddressDTO orderAddressDTO = orderServiceApi.queryOrderAddressById(orderId);
                    OrderPayDTO orderPayDTO = orderServiceApi.queryOrderPayInfoById(payId);

                    ReportCustomsDTO reportCustomsDTO = new ReportCustomsDTO();
                    reportCustomsDTO.setOrderId(orderId);
                    if (orderPayDTO.getPayType().equals(PayMethodEnum.ALIPAY.getName())) {
                        // 报关ID,
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                        String reportId = StringUtils.join(simpleDateFormat.format(new Date()), orderPayDTO.getId());
                        reportCustomsDTO.setReportId(reportId);
                        reportCustomsDTO.setPayMethodEnum(PayMethodEnum.ALIPAY);
                        reportCustomsDTO.setBuyerIdNo(orderAddressDTO.getMemberIdCard());
                        reportCustomsDTO.setBuyerName(orderAddressDTO.getName());
                        reportCustomsDTO.setAmount(orderPayDTO.getThirdPaymentAmount().toPlainString());
                        reportCustomsDTO.setThirdTradeNo(orderPayDTO.getOuterPaySn());
                    } else if (orderPayDTO.getPayType().equals(PayMethodEnum.WX_JSAPI.getName())) {
                        reportCustomsDTO.setPayMethodEnum(PayMethodEnum.WX_JSAPI);
                        reportCustomsDTO.setOutTradeNo(StringUtils.join(orderId, "_", Constants.Order.ORDER_TYPE_GOOD));
                        reportCustomsDTO.setTransactionId(orderPayDTO.getOuterPaySn());
                    } else {
                        log.error(">>>>>>>>>>报关订单无第三方支付！报关失败！");
                        return;
                    }
                    paymentApi.reportCustoms(reportCustomsDTO);
                } catch (Exception e) {
                    log.error(">>>>>>>>>>report order customs fail:", e.getMessage());
                }
            }
        }
    }

    @Override
    public void saveOrderRefundMessage(Long storeId, Long orderId) {
        if (null == orderId || null == storeId) {
            return;
        }
        OpenApiStoreResourceEntity openApiStoreResourceEntity = storeResourceUtil.getByStoreId(storeId);
        if (null != openApiStoreResourceEntity) {
            OpenApiOrderRefundEntity entity = new OpenApiOrderRefundEntity();
            entity.setOrderId(orderId);
            entity.setStoreId(storeId);
            openApiOrderRefundDao.insert(entity);
        }
    }

    @Override
    public void pushOrderDetail() {
        EntityWrapper<OpenApiOrderPayEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.lt("pushCount", OpenApiContants.OrderMessage.MAX_PUSH_COUNT);
        entityWrapper.eq("state", OpenApiContants.OrderMessage.STATE_NO);
        entityWrapper.orderBy("createdTime", true);
        entityWrapper.last("limit 1");

        List<OpenApiOrderPayEntity> openApiOrderPayEntities = openApiOrderPayDao.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(openApiOrderPayEntities)) {
            return;
        }
        OpenApiOrderPayEntity openApiOrderPayEntity = openApiOrderPayEntities.get(0);

        OpenApiOrderPayEntity updateEntity = new OpenApiOrderPayEntity();
        updateEntity.cleanInit();
        updateEntity.setId(openApiOrderPayEntity.getId());
        updateEntity.setPushTime(new Date());

        try {
            // 1：查询订单的商家资源
            OpenApiStoreResourceEntity openApiStoreResourceEntity = storeResourceUtil.getByStoreId(openApiOrderPayEntity.getStoreId());
            if (null == openApiStoreResourceEntity) {
                throw new GlobalException(OpenApiExceptionEnum.STORE_RESOURCE_IS_NOT_FOUND);
            }
            String orderCreateVersion = openApiStoreResourceEntity.getOrderCreateVersion();

            // 2: 拼装推送给管易的订单详情DTO
            String params;
            OrderDetailDTO orderDetailDTO = orderServiceApi.orderDetail(openApiOrderPayEntity.getOrderId());
            if (StringUtils.isNotBlank(orderCreateVersion) && OpenApiContants.OrderCreateVersion.VERSION_TWO.equals(orderCreateVersion)) {
                APIOrderDetailDTO apiOrderDetailDTO = new APIOrderDetailDTO(orderDetailDTO);
                params = JSON.toJSONString(apiOrderDetailDTO, SerializerFeature.WriteMapNullValue);
            } else {
                params = JSON.toJSONString(orderDetailDTO, SerializerFeature.WriteMapNullValue);
            }
            log.info(">>>>>>>>>>pushOrderPayMessage, params:{}", JSON.toJSONString(params));

            String response = mmgOpenApiUtil.postOrderMessage(openApiStoreResourceEntity.getStoreId(), params);
            if (StringUtils.isNotBlank(response)) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                log.warn(">>>>>>>>>> push thirt interface response:{}", jsonObject.toJSONString());
                String code = jsonObject.getString("code");
                if (OpenApiContants.OrderMessage.RESPONSE_SUCCESS_CODE.equals(code)) {
                    updateEntity.setState(OpenApiContants.OrderMessage.STATE_YES);
                } else {
                    updateEntity.setState(OpenApiContants.OrderMessage.STATE_NO);
                    updateEntity.setErrorMessage(jsonObject.getString(OpenApiContants.OrderMessage.RESPONSE_MSG));
                }
            } else {
                updateEntity.setErrorMessage("response is null!");
                updateEntity.setState(OpenApiContants.OrderMessage.STATE_NO);
            }
        } catch (Exception e) {
            log.error(">>>>>>>>>>push order to guanyi fail！error:{]", e);
            updateEntity.setErrorMessage(e.getMessage());
        } finally {
            updateEntity.setPushCount(openApiOrderPayEntity.getPushCount() + 1);
            openApiOrderPayDao.updateById(updateEntity);
        }
    }
}
