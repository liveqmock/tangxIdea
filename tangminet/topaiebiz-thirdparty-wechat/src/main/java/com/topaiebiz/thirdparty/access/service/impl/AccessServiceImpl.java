package com.topaiebiz.thirdparty.access.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.thoughtworks.xstream.XStream;
import com.topaiebiz.thirdparty.access.service.AccessService;
import com.topaiebiz.thirdparty.access.util.AccessCheckSignUtil;
import com.topaiebiz.thirdparty.access.util.AesException;
import com.topaiebiz.thirdparty.access.util.WXBizMsgCrypt;
import com.topaiebiz.thirdparty.autoReply.dto.XmlDTO;
import com.topaiebiz.thirdparty.autoReply.service.AutoReplyService;
import com.topaiebiz.thirdparty.constants.AutoReplyMessage;
import com.topaiebiz.thirdparty.dto.AccessTokenDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by Joe on 2018/5/8.
 */
@Slf4j
@Service
public class AccessServiceImpl implements AccessService {

    @Value("${wechat.public.appid}")
    private String appId = "wx9df6e8ecc9c8dbc3";

    @Value("${wechat.public.secret}")
    private String appsecret = "509365fffb6f301fabe98a5a9bea9dc2";

    @Value("${wechat.public.server.encodingAesKey}")
    private String encodingAesKey = "Zlhdqo8QRAOZhF1ml3IOlbVrOxt3MIJDYf3HbdDrs5J";

    @Value("${wechat.public.server.token}")
    private String serverToken = "qinziezhandeyanzhengtoken";

    private static Charset utf8 = Charset.forName("UTF-8");
    private static int timeout = 3000;


    @Autowired
    private RedisCache redisCache;

    @Override
    public AccessTokenDTO getAccessToken() throws IOException {
        String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appsecret;
        String cacheKey = "WECHAT_ACCESS_TOKEN";
        int expire = 7200;
        AccessTokenDTO accessTokenDTO = redisCache.get(cacheKey, AccessTokenDTO.class);
        if (null == accessTokenDTO) {
            String accessTokenString = Request.Get(access_token_url).connectTimeout(timeout)
                    .socketTimeout(timeout)
                    .execute()
                    .returnContent()
                    .asString(utf8);
            log.info("----------wechat accessToken accessTokenString:{}", accessTokenString);
            accessTokenDTO = JSONObject.parseObject(accessTokenString, AccessTokenDTO.class);
            if (null != accessTokenDTO.getAccess_token()) {
                log.info("----------wechat accessToken accessTokenDTO:{}", JSONObject.toJSONString(accessTokenDTO));
                redisCache.set(cacheKey, JSON.toJSONString(accessTokenDTO), expire);
            } else {
                log.info("----------wechat accessToken errcode:{},errmsg:{}", accessTokenDTO.getErrcode(), accessTokenDTO.getErrmsg());
            }
        }
        return accessTokenDTO;
    }

    @Override
    public String dispatch(HttpServletRequest request, HttpServletResponse response) throws AesException, IOException, DocumentException {

        String resultMessage = "success";
        // 请求方式
        String method = request.getMethod();
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");
        // 签名串
        String msgSignature = request.getParameter("msg_signature");

        if (RequestMethod.GET.toString().equals(method)) {
            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            if (AccessCheckSignUtil.checkSignature(signature, timestamp, nonce)) {
                try {
                    response.getWriter().write(echostr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if (RequestMethod.POST.toString().equals(method)) {
            InputStream inputStream;
            // 提供接收和推送给公众平台消息的加解密接口(UTF8编码的字符串).
            WXBizMsgCrypt pc = new WXBizMsgCrypt(serverToken, encodingAesKey, appId);
            String noticeXml = null;
            try {
                inputStream = request.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String str;
                StringBuilder buffer = new StringBuilder();
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str);
                }
                noticeXml = buffer.toString();
                log.info("----------wechat access noticeXml:{}", noticeXml);

            } catch (IOException e) {
                log.error("----------wechat access noticeXml获取失败:{}", e.toString());
                e.printStackTrace();
            }
            // 解密
            String xmlString = pc.decryptMsg(msgSignature, timestamp, nonce, noticeXml);
            log.info("----------wechat access xmlString:{}", xmlString);
            // xml转对象
            XmlDTO xmlDTO = xmlToObject(xmlString);
            // 回复公众平台的消息
            String xmlMessage = autoReplyzzMessage(xmlDTO);
            log.info("------------wechat access xmlMessage:{}", xmlMessage);
            if (cansSendToXiaoneng(xmlDTO.getMsgType())) {
                xmlToXiaoneng(xmlString);
            }
            // 加密
            resultMessage = pc.encryptMsg(xmlMessage, timestamp, nonce);
            log.info("------------wechat access resultMessage:{}", xmlMessage);
        }

        return resultMessage;
    }

    /**
     * 检测消息是否转发给小能
     */
    private boolean cansSendToXiaoneng(String msgType) {
        boolean can = false;
        if (StringUtils.isBlank(msgType)) {
            return can;
        } else if (AutoReplyMessage.MessageType.TEXT.equals(msgType)) {
            can = true;
        } else if (AutoReplyMessage.MessageType.IMAGE.equals(msgType)) {
            can = true;
        } else if (AutoReplyMessage.MessageType.FILE.equals(msgType)) {
            can = true;
        }
        return can;
    }

    private XmlDTO xmlToObject(String xmlString) {
        //创建xStream对象
        XStream xstream = new XStream();
        //将别名与xml名字相对应
        xstream.alias("xml", XmlDTO.class);
        xstream.aliasAttribute(XmlDTO.class, "toUserName", "ToUserName");
        xstream.aliasAttribute(XmlDTO.class, "fromUserName", "FromUserName");
        xstream.aliasAttribute(XmlDTO.class, "createTime", "CreateTime");
        xstream.aliasAttribute(XmlDTO.class, "msgType", "MsgType");
        xstream.aliasAttribute(XmlDTO.class, "content", "Content");
        xstream.aliasAttribute(XmlDTO.class, "event", "Event");
        xstream.aliasAttribute(XmlDTO.class, "eventKey", "EventKey");
        xstream.aliasAttribute(XmlDTO.class, "msgId", "MsgId");
        xstream.aliasAttribute(XmlDTO.class, "encrypt", "Encrypt");
        xstream.aliasAttribute(XmlDTO.class, "picUrl", "PicUrl");
        xstream.aliasAttribute(XmlDTO.class, "mediaId", "MediaId");
        xstream.aliasAttribute(XmlDTO.class, "format", "Format");
        xstream.aliasAttribute(XmlDTO.class, "recognition", "Recognition");
        xstream.aliasAttribute(XmlDTO.class, "thumbMediaId", "ThumbMediaId");
        xstream.aliasAttribute(XmlDTO.class, "locationX", "Location_X");
        xstream.aliasAttribute(XmlDTO.class, "locationY", "Location_Y");
        xstream.aliasAttribute(XmlDTO.class, "scale", "Scale");
        xstream.aliasAttribute(XmlDTO.class, "label", "Label");
        xstream.aliasAttribute(XmlDTO.class, "title", "Title");
        xstream.aliasAttribute(XmlDTO.class, "description", "Description");
        xstream.aliasAttribute(XmlDTO.class, "url", "Url");
        xstream.aliasAttribute(XmlDTO.class, "ticket", "Ticket");
        xstream.aliasAttribute(XmlDTO.class, "latitude", "Latitude");
        xstream.aliasAttribute(XmlDTO.class, "longitude", "Longitude");
        xstream.aliasAttribute(XmlDTO.class, "precision", "Precision");
        xstream.aliasAttribute(XmlDTO.class, "fileKey", "FileKey");
        xstream.aliasAttribute(XmlDTO.class, "menuId", "MenuId");
        xstream.aliasAttribute(XmlDTO.class, "fileMd5", "FileMd5");
        xstream.aliasAttribute(XmlDTO.class, "fileTotalLen", "FileTotalLen");
        XmlDTO xmlDto = (XmlDTO) xstream.fromXML(xmlString);
        return xmlDto;
    }

    @Autowired
    private AutoReplyService autoReplyService;

    public String autoReplyzzMessage(XmlDTO xmlDTO) throws IOException, DocumentException {
        log.info("access xmlDTO:{}", JSONObject.toJSONString(xmlDTO));
        String message = autoReplyService.autoReplyzzMessage(xmlDTO);
        return message;
    }

    /**
     * 小能服务器人工对话接口
     */
    private static final String XIAO_NENG_WEIXIN_NTALKER_URL = "http://bwx2.ntalker.com/agent/weixin";

    private void xmlToXiaoneng(String xmlString) throws IOException {
        ContentType contentType = ContentType.create("text/xml", utf8);
        Response response = Request.Post(XIAO_NENG_WEIXIN_NTALKER_URL).setHeader("Content-type", "text/xml;charset=UTF-8").bodyString(xmlString, contentType).connectTimeout(timeout)
                .socketTimeout(timeout)
                .execute();
        int statusCode = response.returnResponse().getStatusLine().getStatusCode();
        if (statusCode != 200) {
            log.error("xmlToxiaoneng ====Post请求失败url:{},发送数据:{},错误代码:{}", XIAO_NENG_WEIXIN_NTALKER_URL, xmlString, statusCode);
        }
    }

}
