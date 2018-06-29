package com.topaiebiz.openapi.mq.listener;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.common.msg.core.MessageListener;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.topaiebiz.openapi.service.OpenApiOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ward on 2018-03-01.
 */
@Component
@Slf4j
public class OrderRefundListener implements MessageListener{

    @Autowired
    private OpenApiOrderService openApiOrderService;

    @Override
    public Set<MessageTypeEnum> getTargetMessageTypes() {
        Set<MessageTypeEnum> msgTypes = new HashSet<>();
        msgTypes.add(MessageTypeEnum.ORDER_REFUND);
        return msgTypes;
    }

    @Override
    public void onMessage(MessageDTO msg) {
        log.info(" receive order refund message :" + JSON.toJSONString(msg));
        String storeId_orderId = (String)msg.getParams().get("storeId_orderId");
        if (StringUtils.isNotBlank(storeId_orderId)){
            try{
                String[] strArray = storeId_orderId.split("_");
                openApiOrderService.saveOrderRefundMessage(Long.parseLong(strArray[0]), Long.parseLong(strArray[1]));
            }catch (Exception e){
                log.error(StringUtils.join("----------save order refund message fail：", e.getMessage()), e);
            }
        }
    }
}
