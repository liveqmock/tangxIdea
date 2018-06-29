package com.topaiebiz.giftcard.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @description: 日期操作
 * @author: Jeff Chen
 * @date: created in 下午3:07 2018/1/17
 */
public class DateUtil {
    private DateUtil() {
    }

    /**
     * 未指定日期续期
     * @param srcDate
     * @param days
     * @return
     */
    public static Date renewalDays(Date srcDate, Integer days) {
        if (null == srcDate) {
            srcDate = new Date();
        }
        if (null == days||-1==days) {
            //默认3年
            days = 365 * 3;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(srcDate);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    /**
     * 在指定时间点变更天数
     * @param date
     * @param days
     * @return
     */
    public static String someDay(Date date, Integer days) {
        if (null == date) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return sdf.format(calendar.getTime());

    }

    /**
     * 左与右相差多少天
     * @param left
     * @param right
     * @return
     */
    public static int diffDays(Date left, Date right) {
        if (null == left || null == right) {
            return 0;
        }
        int days = (int) ((left.getTime() - right.getTime()) / (1000*3600*24));
        return days;
    }

    public static void main(String[] args) {
        System.out.println(someDay(new Date(),-30));
    }
}
