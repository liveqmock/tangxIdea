package com.topaiebiz.thirdparty.autoReply.dto;

/**
 * 消息体基础类
 * Created by Joe on 2018/5/3.
 */
public class BaseMessageDTO {

    private String ToUserName;
    private String FromUserName;
    private long CreateTime;
    private String MsgType;
    public String getToUserName() {
        return ToUserName;
    }
    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }
    public String getFromUserName() {
        return FromUserName;
    }
    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }
    public long getCreateTime() {
        return CreateTime;
    }
    public void setCreateTime(long createTime) {
        CreateTime = createTime;
    }
    public String getMsgType() {
        return MsgType;
    }
    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

}
