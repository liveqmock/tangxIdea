package com.topaiebiz.thirdparty.access.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.thirdparty.access.service.AccessService;
import com.topaiebiz.thirdparty.config.WeChatConfig;
import com.topaiebiz.thirdparty.dto.AccessTokenDTO;
import com.topaiebiz.thirdparty.util.Sha1Util;
import com.topaiebiz.thirdparty.util.WeChatHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Description 微信服务器接入以及授权验证通知/回调等等
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017/11/12 17:15
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@RestController
@RequestMapping("/wechat")
public class WXAccessController {

    @Autowired
    private AccessService accessService;

    /**
     * Description: 微信接入验证接口
     * <p>
     * Author: hxpeng
     * createTime: 2017/11/12
     *
     * @param:
     **/
    @ResponseBody
    @RequestMapping(value = "/access")
    public String servletMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String dispatch = accessService.dispatch(request, response);
        return dispatch;
    }


    /**
     * Description: 微信接入验证接口
     * <p>
     * Author: hxpeng
     * createTime: 2017/11/12
     *
     * @param:
     **/
    @ResponseBody
    @RequestMapping(value = "/dispatch")
    public String dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String dispatch = accessService.dispatch(request, response);
        return dispatch;
    }


    /**
     * 模拟微信接口获取token
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getAccessToken")
    public AccessTokenDTO getAccessToken() throws IOException {
        AccessTokenDTO accessToken = accessService.getAccessToken();
        return accessToken;

    }

    /**
     * Description: 微信JS SDK授权
     * <p>
     * Author: hxpeng
     * createTime: 2017/11/16
     *
     * @param:
     **/
    @RequestMapping(value = "/jsSdkAuth", method = RequestMethod.GET)
    public ResponseInfo wechatSdkAuth(String url) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(url)) {
            return new ResponseInfo();
        }
        String noncestr = WeChatHelper.generateNonceStr();
        String timestamp = String.valueOf(WeChatHelper.getCurrentTimestamp());
        String appId = WeChatConfig.APP_ID;

        SortedMap<String, String> signParams = new TreeMap<>();
        signParams.put("noncestr", noncestr);
        signParams.put("timestamp", timestamp);
        signParams.put("jsapi_ticket", WeChatConfig.getJsSdkTictet());
        signParams.put("url", url);
        String signature;
        try {
            signature = Sha1Util.createSHA1Sign(signParams);
            map.put("appId", appId);
            map.put("timestamp", timestamp);
            map.put("noncestr", noncestr);
            map.put("signature", signature);
        } catch (Exception e) {
            throw new RuntimeException("[校验微信SDK,生成签名错误：]" + e.getMessage());
        }
        return new ResponseInfo(map);
    }


}
