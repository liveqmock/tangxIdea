package com.topaiebiz.thirdparty.constants;

/**
 * 微信消息回复
 *
 * Created by Joe on 2018/5/4.
 */
public interface AutoReplyMessage {

    /**
     * 消息类型
     */
    class MessageType{

        // 文本消息
        public static final String TEXT = "text";

        // 语音消息
        public static final String VOICE = "voice";

        // 图片消息
        public static final String IMAGE = "image";

        // 视频消息
        public static final String VIDEO = "video";

        // 链接消息
        public static final String LINK = "link";

        // 位置消息
        public static final String LOCATION = "location";

        //消息视频消息
        public static final String SHORT_VIDEO = "shortcideo";

        // 事件推送
        public static final String EVENT = "event";

        // 关注
        public static final String SUBSCRIBE = "subscribe";

        // 取消关注
        public static final String UNSUBSCRIBE = "unsubscribe";

        // 文件消息
        public static final String FILE = "file";

    }


}
