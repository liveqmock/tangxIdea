package com.nebulapaas.common.msg.core;

import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;

import java.util.Set;

/***
 * @author yfeng
 * @date 2018-02-01 20:42
 */
public interface MessageListener {

    /**
     * 监听器感兴趣的消息类型
     *
     * @return
     */
    Set<MessageTypeEnum> getTargetMessageTypes();

    void onMessage(MessageDTO msg);
}