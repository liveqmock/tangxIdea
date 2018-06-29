package com.topaiebiz.guider.mq.listener;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.common.msg.core.MessageListener;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.topaiebiz.guider.service.AchievementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ward on 2018-06-01.
 */
@Slf4j
@Component
public class GuiderOrderListener implements MessageListener {

    @Autowired
    private AchievementService achievementService;

    @Override
    public Set<MessageTypeEnum> getTargetMessageTypes() {
        Set<MessageTypeEnum> type = new HashSet<>();
        type.add(MessageTypeEnum.ORDER_CLOSE);
        type.add(MessageTypeEnum.ORDER_COMPLETE);
        return type;
    }

    @Override
    public void onMessage(MessageDTO msg) {
        log.info(" receive message :" + JSON.toJSONString(msg));
        MessageTypeEnum type = msg.getType();
        Long memberId = msg.getMemberId();
        Long orderId = (Long) msg.getParams().get("orderId");
        if (MessageTypeEnum.ORDER_CLOSE.equals(type)) {

        }

        if (MessageTypeEnum.ORDER_COMPLETE.equals(type)) {

        }
    }
}
