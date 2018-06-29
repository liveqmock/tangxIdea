package com.topaiebiz.decorate.mq.listener;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.common.msg.core.MessageListener;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.topaiebiz.decorate.service.ContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class ItemListener implements MessageListener {

    @Autowired
    private ContentService contentService;

    @Override
    public Set<MessageTypeEnum> getTargetMessageTypes() {
        Set<MessageTypeEnum> type = new HashSet<>();
        type.add(MessageTypeEnum.REMOVE_ITEM);
        type.add(MessageTypeEnum.EDIT_ITEM);
        return type;
    }

    @Override
    public void onMessage(MessageDTO msg) {
        log.info(" receive message :" + JSON.toJSONString(msg));
        MessageTypeEnum type = msg.getType();
        Long itemId = (Long) msg.getParams().get("itemId");
        if (MessageTypeEnum.REMOVE_ITEM.equals(type)) {
            contentService.removeItem(itemId);
        }

        if (MessageTypeEnum.EDIT_ITEM.equals(type)) {
            contentService.editItem(itemId);
        }
    }

}



