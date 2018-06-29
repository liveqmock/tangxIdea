package com.topaiebiz.member.member.third.impl;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.nebulapaas.common.redis.aop.JedisOperation;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.exception.MemberExceptionEnum;
import com.topaiebiz.member.member.bo.WechatAccessTokenBo;
import com.topaiebiz.member.member.bo.WechatUserBo;
import com.topaiebiz.member.member.constants.MemberCacheKey;
import com.topaiebiz.member.member.third.WechatPublicService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class WechatPublicServiceImpl implements WechatPublicService {

    //@Value("${wechat.public.appid}")
    private String appid = "wx24bc37f96ece6210";
    // @Value("${wechat.public.secret}")
    private String secret = "c7382239c6764c31085af29683193e74";
    private String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    private String userInfoUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Charset utf8 = Charset.forName("UTF-8");

    @Override
    @JedisOperation
    public String getAccessToken() {
        Jedis jedis = JedisContext.getJedis();
        String token = jedis.get(MemberCacheKey.WECHAT_ACCESS_TOKEN_KEY);
        if (!StringUtils.isBlank(token)) {
            return token;
        }
        String content = null;
        try {
            content = Request.Get(String.format(accessTokenUrl, appid, secret)).execute().returnContent().asString(utf8);
            WechatAccessTokenBo accessToken = JSON.parseObject(content, WechatAccessTokenBo.class);
            if (StringUtils.isBlank(accessToken.getAccessToken())) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_LOGIN_ERROR);
            }
            jedis.setex(MemberCacheKey.WECHAT_ACCESS_TOKEN_KEY, accessToken.getExpiresIn(), accessToken.getAccessToken());
            return accessToken.getAccessToken();
        } catch (IOException e) {
            logger.error("获取微信公众号Token异常：" + content, e);
            throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_LOGIN_ERROR);
        }

    }

    @Override
    public WechatUserBo getUserInfo(String openId) {
        String content = null;
        try {
            content = Request.Get(String.format(userInfoUrl, getAccessToken(), openId)).execute().returnContent().asString(utf8);
            WechatUserBo wechatUserBo = JSON.parseObject(content, WechatUserBo.class);
            if (wechatUserBo.getSubscribe() == null || wechatUserBo.getSubscribe() == 0) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_LOGIN_ERROR);
            }
            return wechatUserBo;
        } catch (IOException e) {
            logger.error("获取微信公众号Token异常：" + content, e);
            throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_LOGIN_ERROR);
        }
    }

}
