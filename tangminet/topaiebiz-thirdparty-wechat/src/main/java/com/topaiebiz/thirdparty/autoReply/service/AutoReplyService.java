package com.topaiebiz.thirdparty.autoReply.service;

import com.topaiebiz.thirdparty.autoReply.dto.XmlDTO;
import org.dom4j.DocumentException;
import java.io.IOException;

/**
 * Created by Joe on 2018/5/3.
 */
public interface AutoReplyService {

    /**
     * 微信公众号自动回复消息
     *
     */
    String autoReplyzzMessage(XmlDTO xmlDTO) throws IOException, DocumentException;
}
