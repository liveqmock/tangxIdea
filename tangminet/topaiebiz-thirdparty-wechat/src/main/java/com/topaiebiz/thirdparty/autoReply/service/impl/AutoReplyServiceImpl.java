package com.topaiebiz.thirdparty.autoReply.service.impl;

import com.topaiebiz.basic.api.ConfigApi;
import com.topaiebiz.thirdparty.autoReply.dto.XmlDTO;
import com.topaiebiz.thirdparty.autoReply.service.AutoReplyService;
import com.topaiebiz.thirdparty.constants.AutoReplyMessage;
import com.topaiebiz.thirdparty.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;

/**
 * Created by Joe on 2018/5/3.
 */
@Slf4j
@Service
public class AutoReplyServiceImpl implements AutoReplyService {

    private static final String weChatautoReply = "weChatAutoReply";
    private String autoReplyMessgae = "Hi，欢迎关注妈妈购——千万个中国辣妈的共同之选！";

    @Autowired
    private ConfigApi configApi;

    @Override
    public String autoReplyzzMessage(XmlDTO xmlDTO) throws IOException, DocumentException {
        String message = "success";
        String fromUserName = xmlDTO.getFromUserName();//消息来源用户标识
        String toUserName = xmlDTO.getToUserName();//消息目的用户标识
        String msgType = xmlDTO.getMsgType();//消息类型
        String content = xmlDTO.getContent();//消息内容
        String event = xmlDTO.getEvent();//事件类型
        String config = configApi.getConfig(weChatautoReply);
        if (!StringUtils.isBlank(config)) {
            autoReplyMessgae = config;
        }
        autoReplyMessgae = HtmlUtils.htmlUnescape(autoReplyMessgae);
        // 判断消息类型
        if (msgType.equals(AutoReplyMessage.MessageType.EVENT)) {
            // 判断事件类型
            if (event.equals(AutoReplyMessage.MessageType.SUBSCRIBE)) {
                // 关注事件
                message = MessageUtil.textMsg(toUserName, xmlDTO.getFromUserName(), autoReplyMessgae);
            }
        }

        return message;

    }


}
