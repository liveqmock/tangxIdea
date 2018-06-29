package com.topaiebiz.monitor.util;

import com.topaiebiz.monitor.contants.MonitorConstants;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/***
 * @author yfeng
 * @date 2018-05-30 10:27
 */
public class MonitorCacheUtil {
    public static String getCacheKey(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH");
        return StringUtils.join(MonitorConstants.CacheKey.REQUEST_COUNT_CAHCE_PREFIX,sdf.format(time));
    }
}