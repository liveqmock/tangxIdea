package com.topaiebiz.decorate.mq.listener;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.common.msg.core.MessageListener;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.decorate.exception.DecorateExcepionEnum;
import com.topaiebiz.decorate.service.ContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class ActivityListener implements MessageListener {

    @Autowired
    private ContentService contentService;

    @Override
    public Set<MessageTypeEnum> getTargetMessageTypes() {
        Set<MessageTypeEnum> type = new HashSet<>();
        type.add(MessageTypeEnum.ACTIVE_ITEM);
        return type;
    }

    @Override
    public void onMessage(MessageDTO msg) {
        if (null == msg) {
            throw new GlobalException(DecorateExcepionEnum.ACTIVITY_ITEM_NOT_NULL);
        }
        log.info(" receive message :" + JSON.toJSONString(msg));
        contentService.activeItem((List<Long>) msg.getParams().get("itemId"));
    }
}
