package com.topaiebiz.member.member.utils;

/**
 * Created by ward on 2018-01-27.
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

public class HttpUtil {
    public static final Charset utf8 = Charset.forName("UTF-8");

    public HttpUtil() {
    }

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-real-ip");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        } else {
            ip = request.getHeader("X-Real-IP");
            if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
                return ip;
            } else {
                ip = request.getHeader("X-Forwarded-For");
                if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
                    int index = ip.indexOf(",");
                    return index != -1?ip.substring(0, index):ip;
                } else {
                    return request.getRemoteAddr();
                }
            }
        }
    }

    public static Cookie getCookie(String name, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length != 0) {
            Cookie[] arr$ = cookies;
            int len$ = cookies.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Cookie cookie = arr$[i$];
                if(name.equals(cookie.getName())) {
                    return cookie;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public static String getCookieValue(String name, HttpServletRequest request) {
        Cookie cookie = getCookie(name, request);
        return cookie != null?cookie.getValue():null;
    }

    public static String get(String url, Map<String, String> param) throws Exception {
        return Request.Get(url + getQueryString(param)).execute().returnContent().asString(utf8);
    }

    public static String post(String url, Map<String, String> param) throws Exception {
        Form form = Form.form();
        if(param != null && !param.isEmpty()) {
            Iterator i$ = param.keySet().iterator();

            while(i$.hasNext()) {
                String key = (String)i$.next();
                form.add(key, (String)param.get(key));
            }
        }

        return Request.Post(url).bodyForm(form.build(), utf8).execute().returnContent().asString(utf8);
    }

    public static String getQueryString(Map<String, String> param) {
        if(param != null && !param.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Iterator i$ = param.keySet().iterator();

            while(i$.hasNext()) {
                String key = (String)i$.next();
                String value = (String)param.get(key);
                if(!StringUtils.isBlank(value)) {
                    sb.append(key).append("=").append((String)param.get(key)).append("&");
                }
            }

            return sb.deleteCharAt(sb.length() - 1).toString();
        } else {
            return "";
        }
    }

    public static String urlDecode(String value) {
        try {
            if(value != null) {
                return URLDecoder.decode(value, "UTF-8");
            }
        } catch (Exception var2) {
            ;
        }

        return null;
    }

    public static String urlDecode2(String value) {
        try {
            if(value != null) {
                return new String(value.getBytes("ISO-8859-1"), "UTF-8");
            }
        } catch (Exception var2) {
            ;
        }

        return null;
    }
}
