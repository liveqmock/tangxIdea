package com.nebulapaas.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/***
 * @author yfeng
 * @date 2017-12-19 10:24
 */
@Slf4j
public class ServletRequestUtil {
    private static final String UNKNOW_IP = "unknown";

    public static String getRequestUri(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder(request.getRequestURI());
        String query = request.getQueryString();
        if (StringUtils.isNotBlank(query)) {
            builder.append("?").append(query);
        }
        return builder.toString();
    }

    private static boolean getIpFail(String ip) {
        return StringUtils.isBlank(ip) || UNKNOW_IP.equalsIgnoreCase(ip);
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (getIpFail(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (getIpFail(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (getIpFail(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (getIpFail(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (getIpFail(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}