package com.topaiebiz.thirdparty.autoReply.controller;

import com.topaiebiz.thirdparty.autoReply.dto.XmlDTO;
import com.topaiebiz.thirdparty.autoReply.service.AutoReplyService;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 微信自动回复
 * Created by Joe on 2018/5/3.
 */
@RestController
@RequestMapping(path = "/wechat/autoReply", method = RequestMethod.POST)
public class AutoReplyController {

    @Autowired
    private AutoReplyService autoReplyService;

    /**
     * 微信公众号自动回复消息
     */
    @RequestMapping(path = "/autoReplyzzMessage")
    public String autoReplyzzMessage(@RequestBody XmlDTO xmlDTO) throws IOException, DocumentException {

        String message = autoReplyService.autoReplyzzMessage(xmlDTO);
        return message;
    }


}
