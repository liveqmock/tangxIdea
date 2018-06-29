package com.topaiebiz.thirdparty.config;

import com.alibaba.fastjson.JSONObject;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.topaiebiz.thirdparty.constants.WechatCacheKey;
import com.topaiebiz.thirdparty.dto.AccessTokenDTO;
import com.topaiebiz.thirdparty.dto.JsapiTicketDTO;
import com.topaiebiz.thirdparty.util.WeChatHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;

/**
 * Description 微信配置类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017/11/13 10:46
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class WeChatConfig implements InitializingBean{

    @Autowired
    private Environment environment;

    /**
     * 公众号APPID
     */
    public static String APP_ID;

    /**
     * 公众号密码
     */
    private static String APP_SECRET;

    /**
     * 商户号
     */
    public static String MCH_ID;

    /**
     * 签名类型
     */
    public static String SIGN_TYPE;

    /**
     * 微信统一下单路径
     */
    public static String WECHAT_PAY_URL;

    /**
     * 微信统一退款路径
     */
    public static String REFUND_PAY_URL;

    /**
     * 支付成功 通知地址
     */
    public static String PAY_NOTIFY_URL;

    /**
     * （商品）微信支付前，授权后重定向支付地址
     */
    public static String PAY_AUTH_CALLBACK_URL;

    /**
     * （美礼卡）微信支付前，授权后重定向支付地址
     */
    public static String CARD_PAY_AUTH_CALLBACK_URL;

    /**
     * 商品平台密钥key
     */
    public static String APP_KEY;

    /**
     * 证书字节
     */
    public static byte[] CERT_BYTE;

    /**
     * 商户海关备案号
     */
    public static String MCH_CUSTOMS_NO;

    /**
     * 微信报关地址
     */
    public static String MCH_CUSTOMS_URL;

    /**
     * 微信商户海关备案号
     */
    public static String CUSTOMS;

    /**
    *
    * Description: 获取AccessToken的接口路径
    *
    * Author: hxpeng
    * createTime: 2018/1/16
    *
    * @param: 
    **/
    private static String getAccessTokenUrl() {
        return MessageFormat.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}", APP_ID, APP_SECRET);
    }

    /**
    *
    * Description: 根据网页授权ACCESS_CODE 获取微信OPENID
    *
    * Author: hxpeng
    * createTime: 2018/1/16
    *
    * @param: 
    **/
    private static String getOpenIdUrl(String code) {
        return MessageFormat.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid={0}&secret={1}&code={2}&grant_type=authorization_code", APP_ID, APP_SECRET, code);
    }

    /**
    *
    * Description: 获取网页JS JDK使用权限
    *
    * Author: hxpeng
    * createTime: 2018/1/16
    *
    * @param: 
    **/
    private static String getJsapiTicketUrl() {
        return MessageFormat.format("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={0}&type=jsapi", getAccessToken());
    }


    /**
    *
    * Description: 微信公众号支付前 获取openID 跳转路径
    *
    * Author: hxpeng
    * createTime: 2018/2/7
    *
    * @param:
    **/
    public static String buildRedirectUrl(String orderPayId, String url) throws UnsupportedEncodingException {
        return MessageFormat.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid={0}&redirect_uri={1}&response_type=code&scope=snsapi_base&state={2}#wechat_redirect",
                APP_ID, URLEncoder.encode(url, "utf-8"), orderPayId);
    }

    /**
     *
     * Description: 获取基础支持的AccessToken， 公众号的全局唯一接口调用凭据
     *
     * Author: hxpeng
     * createTime: 2018/1/16
     *
     * @param:
     **/
    private static String getAccessToken() {
        JedisContext.loadJedisInstance();
        try {
            Jedis jedis = JedisContext.getJedis();
            if (null == jedis){
                log.error("----------WechatUtil.getTokenMethod() jedis is null !");
                return null;
            }
            String accessToken = jedis.get(WechatCacheKey.WECHAT_ACCESS_TOKEN);
            if (StringUtils.isBlank(accessToken)){
                String buf = WeChatHelper.postDataToWeiXin(WeChatConfig.getAccessTokenUrl(), null);
                log.info("----------getAccessToken buf:{}", buf);
                AccessTokenDTO accessTokenDTO = JSONObject.parseObject(buf, AccessTokenDTO.class);
                if (StringUtils.isBlank(accessTokenDTO.getErrcode()) && StringUtils.isNotBlank(accessTokenDTO.getAccess_token())) {
                    accessToken = accessTokenDTO.getAccess_token();
                    jedis.set(WechatCacheKey.WECHAT_ACCESS_TOKEN, accessToken);
                    jedis.expire(WechatCacheKey.WECHAT_ACCESS_TOKEN, Integer.valueOf(accessTokenDTO.getExpires_in()));
                }else{
                    log.error("----------获取access token 失败！微信返回值：{}", buf);
                }
            }
            return accessToken;
        } catch (Throwable t) {
            log.error("----------WechatUtil.getTokenMethod() 异常!");
            throw t;
        } finally {
            JedisContext.releaseJeids();
        }
    }


    /**
     *
     * Description: 根据授权code， 换取网页授权，获取用户信息
     *
     * Author: hxpeng
     * createTime: 2018/1/16
     *
     * @param:
     **/
    public static String getOpenId(String code) {
        JSONObject jsonObject = JSONObject.parseObject(WeChatHelper.postDataToWeiXin(WeChatConfig.getOpenIdUrl(code), null));
        log.info("----------request wechat get opendId result : {}", jsonObject.toJSONString());
        return jsonObject.getString("openid");
    }

    /**
     *
     * Description: 获取微信网页js sdk 权限票根
     *
     * Author: hxpeng
     * createTime: 2018/1/16
     *
     * @param:
     **/
    public static String getJsSdkTictet() {
        JedisContext.loadJedisInstance();
        try {
            Jedis jedis = JedisContext.getJedis();
            if (null == jedis){
                log.error("----------WechatUtil.getJsSdkTictet() jedis is null !");
                return null;
            }
            String jsTicket = jedis.get(WechatCacheKey.WECHAT_JS_SDK_TICKET);
            if (StringUtils.isBlank(jsTicket)){
                String buf = WeChatHelper.postDataToWeiXin(WeChatConfig.getJsapiTicketUrl(), null);
                JsapiTicketDTO jsapiTicketDto = JSONObject.parseObject(buf, JsapiTicketDTO.class);
                if (StringUtils.isNotBlank(jsapiTicketDto.getErrcode()) && jsapiTicketDto.getErrcode().equals("0")) {
                    jsTicket = jsapiTicketDto.getTicket();
                    jedis.set(WechatCacheKey.WECHAT_JS_SDK_TICKET, jsTicket);
                    jedis.expire(WechatCacheKey.WECHAT_JS_SDK_TICKET, 7200);
                }else{
                    log.error("----------获取js sdk ticket 失败！微信返回值：{}", buf);
                }
            }
            return jsTicket;
        } catch (Throwable t) {
            log.error("----------WechatUtil.getJsSdkTictet() 异常!");
            throw t;
        } finally {
            JedisContext.releaseJeids();
        }
    }


    private static void getCertByte() {
        log.info("----------初始化，获取微信支付的证书，转换为byte数组属性");
        try {
            ClassPathResource resource = new ClassPathResource("apiclient_cert.p12");
            InputStream fis = resource.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > -1 ) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
            fis.close();
            CERT_BYTE = bos.toByteArray();
            bos.close();
            log.info("----------获取微信证书成功");
        } catch (Exception e) {
            log.error("----------转换微信支付证书为byte数组失败！！", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        APP_ID = environment.getProperty("wechat.public.appid");
        APP_SECRET = environment.getProperty("wechat.public.secret");
        MCH_ID = environment.getProperty("wechatpay.mchid");
        SIGN_TYPE = environment.getProperty("wechatpay.sign.type");
        WECHAT_PAY_URL = environment.getProperty("wechatpay.wechat.pay.url");
        REFUND_PAY_URL = environment.getProperty("wechatpay.refund.pay.url");
        PAY_NOTIFY_URL = environment.getProperty("wechatpay.pay.notify.url");
        PAY_AUTH_CALLBACK_URL = environment.getProperty("wechatpay.pay.auth.callback.url");
        CARD_PAY_AUTH_CALLBACK_URL = environment.getProperty("wechatpay.card.pay.auth.callback.url");
        APP_KEY = environment.getProperty("wechatpay.app.key");
        MCH_CUSTOMS_NO = environment.getProperty("wechat.mch.customs.no");
        MCH_CUSTOMS_URL = environment.getProperty("wechat.mch.customs.url");
        CUSTOMS = environment.getProperty("wecaht.customs");
        getCertByte();
    }

//    public static void test(){
//        APP_ID = "wxd17c02bad3d9d844";
//        APP_SECRET = "218f2ef9a7798b8e571c9e391c2f94f1";
//        MCH_ID = "1255340701";
//        SIGN_TYPE = "MD5";
//        APP_KEY = "96e79218965eb72c92a549dd5a330112";
//        MCH_CUSTOMS_NO = "3302462319";
//        MCH_CUSTOMS_URL = "https://api.mch.weixin.qq.com/cgi-bin/mch/customs/customdeclareorder";
//        CUSTOMS = "HANGZHOU_ZS";
//        getCertByte();
//    }


//    public static void main(String[] args) {
//        try {
//            ClassPathResource resource = new ClassPathResource("apiclient_cert.p12");
//            InputStream fis = resource.getInputStream();
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024];
//            int len;
//            while ((len = fis.read(buffer)) > -1 ) {
//                bos.write(buffer, 0, len);
//            }
//            bos.flush();
//            fis.close();
//            CERT_BYTE = bos.toByteArray();
//            bos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
