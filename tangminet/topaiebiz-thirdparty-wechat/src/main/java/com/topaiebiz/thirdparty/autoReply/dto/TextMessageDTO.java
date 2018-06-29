package com.topaiebiz.thirdparty.autoReply.dto;

/**
 * 文本消息
 * Created by Joe on 2018/5/3.
 */
public class TextMessageDTO extends BaseMessageDTO {

    private String Content;
    private String MsgId;
    public String getContent() {
        return Content;
    }
    public void setContent(String content) {
        Content = content;
    }
    public String getMsgId() {
        return MsgId;
    }
    public void setMsgId(String msgId) {
        MsgId = msgId;
    }

}
