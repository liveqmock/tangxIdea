package com.topaiebiz.thirdparty.util;

import com.alibaba.fastjson.JSONObject;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.topaiebiz.thirdparty.config.WeChatConfig;
import com.topaiebiz.thirdparty.constants.WechatCacheKey;
import com.topaiebiz.thirdparty.dto.AccessTokenDTO;
import com.topaiebiz.thirdparty.dto.JsapiTicketDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;


@Slf4j
public class WeChatHelper {

	/**
	 * 默认连接超时时间
	 */
	private static final int DEFAULT_CONNECT_TIMEOUT_MS = 6*1000;

	/**
	 * 默认读取数据超时时间
	 */
	private static final int DEFAULT_READ_TIMEOUT_MS = 8*1000;


    /**
     *
     * Description: 判断参数区分请求类型
     *
     * Author: hxpeng
     * createTime: 2018/1/16
     *
     * @param:
     **/
    public static String postDataToWeiXin(String strURL, String params) {
        String resString = "";
        if (StringUtils.isNotBlank(params)) {
            resString = postDataToWeiXin(strURL, params, "POST");
        } else {
            resString = postDataToWeiXin(strURL, params, "GET");
        }

        return resString;
    }

    /**
     *
     * Description: 请求微信
     *
     * Author: hxpeng
     * createTime: 2018/1/16
     *
     * @param:
     * strURL  提交的网址
     * params  提交的参数
     * method  提交方式
     **/
    private static String postDataToWeiXin(String strURL, String params, String method) {
        log.info("----------WechatHelper postDataToWeiXin, 路径{}, 参数{}, 方法{}", strURL, params, method);
        StringBuilder bufferRes = new StringBuilder();
        try {
            String resString = "";
            URL realUrl = new URL(strURL);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 连接超时
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MS);
            // 读取超时 --服务器响应比较慢,增大时间
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT_MS);
            HttpURLConnection.setFollowRedirects(true);
            // 请求方式
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("User-Agent", "wxpay sdk java v1.0 ");
            conn.setRequestProperty("Referer", "https://api.weixin.qq.com/");
            conn.setRequestProperty("Content-Type", "text/xml");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            if (StringUtils.isNotBlank(params)) {
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                //out.write(URLEncoder.encode(params,"UTF-8"));
                out.write(params);
                out.flush();
                out.close();
            }
            InputStream in = conn.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String valueString = null;
            while ((valueString = read.readLine()) != null) {
                bufferRes.append(valueString).append("\n");
            }
            resString = bufferRes.toString();
            in.close();
            conn.disconnect();
            log.info("----------WechatHelper postDataToWeiXin 输出{}", resString);
            return resString;
        } catch (Exception e) {
            log.info("----------WechatHelper postDataToWeiXin异常！");
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取当前时间戳，单位秒
     * @return
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis()/1000;
    }

    /**
     * 获取随机字符串 Nonce Str
     *
     * @return String 随机字符串
     */
    public static String generateNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }
}
