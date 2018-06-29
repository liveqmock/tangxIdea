package com.topaiebiz.member.member.third.impl;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.exception.MemberExceptionEnum;
import com.topaiebiz.member.member.bo.WechatAccessTokenBo;
import com.topaiebiz.member.member.bo.WechatUserBo;
import com.topaiebiz.member.member.third.WechatAppService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class WechatAppServiceImpl implements WechatAppService {

    //@Value("${wechat.app.appid}")
    @Value("${wechat.public.appid}")
    private String appid = "wx350017a87ccee559";
    //@Value("${wechat.app.secret}")
    @Value("${wechat.public.secret}")
    private String secret = "8f6ce5885afae30fd824f71dcc4a2022";
    private String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    private String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Charset utf8 = Charset.forName("UTF-8");

    @Override
    public WechatAccessTokenBo getAccessToken(String code) {
        String content = null;
        try {
            content = Request.Get(String.format(accessTokenUrl, appid, secret, code)).execute().returnContent().asString(utf8);
            WechatAccessTokenBo accessToken = JSON.parseObject(content, WechatAccessTokenBo.class);
            if (StringUtils.isBlank(accessToken.getAccessToken())) {

                throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_LOGIN_ERROR);
            }
            return accessToken;
        } catch (IOException e) {
            logger.error("获取微信Token异常：" + content, e);
            throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_LOGIN_ERROR);
        }
    }

    @Override
    public WechatUserBo getUserInfo(WechatAccessTokenBo accessToken) {
        return getUserInfo(accessToken.getAccessToken(), accessToken.getOpenid());
    }

    @Override
    public WechatUserBo getUserInfo(String accessToken, String openid) {
        String content = null;
        try {
            content = Request.Get(String.format(userInfoUrl, accessToken, openid)).execute().returnContent().asString(utf8);
            WechatUserBo wechatUserBo = JSON.parseObject(content, WechatUserBo.class);
            if (StringUtils.isBlank(wechatUserBo.getUnionid())) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_LOGIN_ERROR);
            }
            return wechatUserBo;
        } catch (IOException e) {
            logger.error("获取微信用户信息异常：" + content, e);
            throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_LOGIN_ERROR);
        }
    }

}
