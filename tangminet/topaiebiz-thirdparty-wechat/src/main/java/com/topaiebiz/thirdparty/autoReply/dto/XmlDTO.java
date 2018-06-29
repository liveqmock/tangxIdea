package com.topaiebiz.thirdparty.autoReply.dto;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * xml根元素
 * Created by Joe on 2018/5/3.
 */
@Data
public class XmlDTO {

    /** 开发者微信号 */
    private String toUserName;

    /** 发送方账号 */
    private String fromUserName;

    /** 消息创建时间（整型） */
    private Integer createTime;

    /** 消息类型 */
    private String msgType;

    /** 消息内容 */
    private String content;

    /** 事件类型 */
    private String event;

    /** 事件KEY值 */
    private String eventKey;

    /** 消息id，64位整型 */
    private String msgId;

    /** 加解密字符串 */
    private String encrypt;

    /** 图片链接 */
    private String picUrl;

    /** 图片消息媒体id */
    private String mediaId;

    /** 语音格式，如amr，speex等 */
    private String format;

    /** 语音识别结果，UTF8编码 */
    private String recognition;

    /** 视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据。 */
    private String thumbMediaId;

    /** 消息-地理位置维度 */
    private String locationX;

    /** 消息-地理位置经度 */
    private String locationY;

    /** 地图缩放大小 */
    private String scale;

    /** 地理位置信息 */
    private String label;

    /** 消息标题 */
    private String title;

    /** 消息描述 */
    private String description;

    /** 消息链接 */
    private String url;

    /** 二维码的ticket，可用来换取二维码图片 */
    private String ticket;

    /** 事件-地理位置纬度 */
    private String latitude;

    /** 事件-地理位置经度 */
    private String longitude;

    /** 事件-地理位置精度 */
    private String precision;

    /** 文件 */
    private String fileKey;

    /** 菜单 */
    private String menuId;

    private String fileMd5;

    private String fileTotalLen;

}
