package com.topaiebiz.trade.refund.core.context;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description 时间转化
 * <p>
 *
 * @Author hxpeng
 * <p>
 * Date 2018/4/12 16:21
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public class SimpleDateFormatContext {

    private static final String DATE_FORMAT_TEMPLATE = "yyyy-MM-dd HH:mm:ss";

    private static ThreadLocal<SimpleDateFormat> simpleDateFormatThreadLocal = new ThreadLocal<>();

    public static SimpleDateFormat getFormat() {
        SimpleDateFormat simpleDateFormat = simpleDateFormatThreadLocal.get();
        if (null == simpleDateFormat) {
            simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_TEMPLATE);
            simpleDateFormatThreadLocal.set(simpleDateFormat);
        }
        return simpleDateFormat;
    }

    public static SimpleDateFormat getFormat(String format) {
        SimpleDateFormat simpleDateFormat = simpleDateFormatThreadLocal.get();
        if (null == simpleDateFormat) {
            simpleDateFormat = new SimpleDateFormat(format);
            simpleDateFormatThreadLocal.set(simpleDateFormat);
        }
        return simpleDateFormat;
    }

    public static String format(Date date) {
        return SimpleDateFormatContext.getFormat().format(date);
    }

    public static String format(Date date, String formatStr) {
        return SimpleDateFormatContext.getFormat(formatStr).format(date);
    }

    public static void remove() {
        simpleDateFormatThreadLocal.remove();
    }

}
