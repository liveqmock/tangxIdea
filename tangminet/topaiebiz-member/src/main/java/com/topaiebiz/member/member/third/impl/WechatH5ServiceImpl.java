package com.topaiebiz.member.member.third.impl;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.exception.MemberExceptionEnum;
import com.topaiebiz.member.member.bo.WechatAccessTokenBo;
import com.topaiebiz.member.member.bo.WechatUserBo;
import com.topaiebiz.member.member.third.WechatH5Service;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

@Service
public class WechatH5ServiceImpl implements WechatH5Service {

    @Value("${wechat.public.appid}")
    private String appid = "wx24bc37f96ece6210";
    @Value("${wechat.public.secret}")
    private String secret = "c7382239c6764c31085af29683193e74";
    private String authorizeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
    @Value("${wechat.h5.login.callback.url}")
    private String callbackUrl = "http://10.80.17.18/vip-web/member/wechat_h5_login";
    private String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    private String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Charset utf8 = Charset.forName("UTF-8");

    @Override
    public String getAuthorizeUrl() {
        try {
            return String.format(authorizeUrl, appid, URLEncoder.encode(callbackUrl, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override
    public String getAuthorizeUrl(String redirectType) {
        String tempUrl = callbackUrl;
        if (StringUtils.isNotBlank(redirectType)) {
            tempUrl = callbackUrl + "?redirectType=" + redirectType;
        }
        try {
            return String.format(authorizeUrl, appid, URLEncoder.encode(tempUrl, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }


    @Override
    public WechatAccessTokenBo getAccessToken(String code) {
        String content = null;
        WechatAccessTokenBo accessToken;
        try {
            content = Request.Get(String.format(accessTokenUrl, appid, secret, code)).execute().returnContent().asString(utf8);
            accessToken = JSON.parseObject(content, WechatAccessTokenBo.class);
            if (StringUtils.isBlank(accessToken.getAccessToken())) {
                logger.error("获取微信Token异常content={}" + content);
                throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_LOGIN_ERROR);
            }

        } catch (IOException e) {
            logger.error("获取微信Token异常：" + content, e);
            throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_LOGIN_ERROR);
        }
        return accessToken;
    }

    @Override
    public WechatUserBo getUserInfo(WechatAccessTokenBo accessToken) {
        String content = null;
        try {
            content = Request.Get(String.format(userInfoUrl, accessToken.getAccessToken(), accessToken.getOpenid())).execute().returnContent().asString(utf8);
            WechatUserBo wechatUserBo = JSON.parseObject(content, WechatUserBo.class);
            if (StringUtils.isBlank(accessToken.getUnionid())) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_LOGIN_ERROR);
            }
            return wechatUserBo;
        } catch (IOException e) {
            logger.error("获取微信用户信息异常：" + content, e);
            throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_LOGIN_ERROR);
        }
    }

}
