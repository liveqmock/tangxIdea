package com.topaiebiz.trade.order.util;

import org.apache.commons.lang3.StringUtils;

/***
 * @author yfeng
 * @date 2018-03-09 13:01
 */
public class OrderUtil {
    private static int userAgentMaxLength = 255;

    public static String orderUserAgent(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return "";
        }
        if (userAgent.length() > userAgentMaxLength) {
            return userAgent.substring(0, userAgentMaxLength);
        }
        return userAgent;
    }
}
