package com.topaiebiz.member.member.third.impl;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.exception.MemberExceptionEnum;
import com.topaiebiz.member.member.bo.QQUserBo;
import com.topaiebiz.member.member.third.QQService;
import com.topaiebiz.member.member.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
@Service
public class QQServiceImpl implements QQService {

    //@Value("${qq.appid}")
    private String appid = "1105012680";
   // @Value("${qq.secret}")
    private String secret = "M796knelb3loSePb";
    private String authorizeUrl = "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=get_user_info";
   // @Value("${qq.h5.login.callback.url}")
    private String callbackUrl = "http://member.motherbuy.com/api.php?act=toqq&op=g";
    private String accessTokenUrl = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&state=st&redirect_uri=%s";
    private String openIdUrl = "https://graph.qq.com/oauth2.0/me?access_token=%s";
    private String userInfoUrl = "https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=%s&openid=%s";

    @Override
    public String getAuthorizeUrl() {
        try {
            return String.format(authorizeUrl, appid, URLEncoder.encode(callbackUrl, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            //throw new BimException(FrameworkCode.FAIL);
            return "";
            //throw new GlobalException();
        }
    }

    @Override
    public String getAccessToken(String code) {
        try {
            System.out.println(HttpUtil.get(String.format(accessTokenUrl, appid, secret, code, URLEncoder.encode(callbackUrl, "UTF-8")), null));
        } catch (Exception e) {
            log.error("获取QQ用户accessToken异常", e);
            //throw new BimException("MB_GET_QQ_USER_ERROR");
        }
        return null;
    }

    @Override
    public String getOpenId(String token) {
        try {
            System.out.println(HttpUtil.get(String.format(openIdUrl, token), null));
        } catch (Exception e) {
            log.error("获取QQ用户openId异常", e);
            //throw new BimException("MB_GET_QQ_USER_ERROR");
        }
        return null;
    }

    @Override
    public QQUserBo getUserInfo(String token, String openId) {
        QQUserBo qqUser;
        try {
            qqUser = JSON.parseObject(HttpUtil.get(String.format(userInfoUrl, token, appid, openId), null), QQUserBo.class);
            if ("0".equals(qqUser.getRet())) {
                qqUser.setRet(null);
                qqUser.setMsg(null);
                qqUser.setOpenId(openId);
                //   return qqUser;
            } else {
                log.error("获取QQ用户信息异常", JSON.toJSONString(qqUser));
                //throw new BimException("MB_GET_QQ_USER_ERROR");
            }
        } catch (GlobalException be) {
            throw be;
        } catch (Exception e) {
            log.error("获取QQ用户信息异常", e);
            //throw new BimException("MB_GET_QQ_USER_ERROR");
            throw new GlobalException(MemberExceptionEnum.MEMBER_MEMBERCODE_NOT_REPETITION);
        }
        return qqUser;
    }

}
