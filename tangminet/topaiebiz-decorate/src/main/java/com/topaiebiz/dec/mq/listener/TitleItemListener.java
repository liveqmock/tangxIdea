package com.topaiebiz.dec.mq.listener;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.common.msg.core.MessageListener;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.topaiebiz.dec.service.TitleGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class TitleItemListener implements MessageListener {

    @Autowired
    private TitleGoodsService titleGoodsService;

    @Override
    public Set<MessageTypeEnum> getTargetMessageTypes() {
        Set<MessageTypeEnum> msgTypes = new HashSet<>();
        msgTypes.add(MessageTypeEnum.MODIFY_TITLE_ITEM);
        return msgTypes;
    }

    @Override
    public void onMessage(MessageDTO msg) {
        log.info(" receive message :" + JSON.toJSONString(msg));
        Long titleId = (Long) msg.getParams().get("titleId");
        titleGoodsService.refreshCache(titleId);
    }
}
