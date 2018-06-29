package com.topaiebiz.openapi.mq.listener;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.common.msg.core.MessageListener;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.topaiebiz.openapi.service.OpenApiOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ward on 2018-03-01.
 */
@Slf4j
@Component
public class OrderPayListener implements MessageListener {

    @Autowired
    private OpenApiOrderService openApiOrderService;

    @Override
    public Set<MessageTypeEnum> getTargetMessageTypes() {
        Set<MessageTypeEnum> msgTypes = new HashSet<>();
        msgTypes.add(MessageTypeEnum.ORDER_PAY);
        return msgTypes;
    }

    @Override
    public void onMessage(MessageDTO msg) {
        log.warn(" receive order pay message :" + JSON.toJSONString(msg));
        Long payId = (Long) msg.getParams().get("payId");
        if (null != payId){
            openApiOrderService.saveOrderCreateMessage(payId);
        }
    }
}
