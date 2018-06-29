package com.nebulapaas.common;

import org.joda.time.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 工具类：[日期]、[字符串]、[时间]三者的转换工具类 三者的转换是非常常用的，所以定义此工具类。三者的简要说明如下： [日期]：日期对象 [字符串]：指的是日期的字符串表示
 * [时间]：long型的值
 */
public class DateUtils {
    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static final String DATETIMEFORMAT = "yyyyMMddHHmmss";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final long TIME_OF_MINUTE = 60000L;

    public static final long TIME_OF_HOUR = 3600000L;

    public static final long TIME_OF_DAY = 86400000L;

    /**
     * 将[日期]按照给定的[日期格式]转成[字符串]
     *
     * @param date   日期
     * @param format 日期格式
     * @return
     */
    public static String parseDateToString(Date date, String format) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isEmpty(format)) {
            format = DATE_TIME_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 将[时间]按照指定的[日期格式]转成[字符串]
     *
     * @param time   时间
     * @param format 日期格式
     * @return
     */
    public static String parseLongToString(long time, String format) {
        SimpleDateFormat mydate = new SimpleDateFormat(format);
        return mydate.format(new Date(time));
    }

    public static String getTimeStampStrByNow() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        return simpleDateFormat.format(new Date());
    }

    /**
     * 将[字符串]按照指定的[日期格式]转成[时间]
     *
     * @param time   字符串
     * @param format 日期格式
     * @return
     */
    public static long parseStringToLong(String time, String format) {
        SimpleDateFormat mydate = new SimpleDateFormat(format);
        try {
            Date date = mydate.parse(time);
            if (date != null) {
                return date.getTime();
            }
        } catch (ParseException e) {
            logger.error("Date parse error:", e);
        }
        return 0;
    }

    /**
     * 将[字符串]按照指定的[日期格式]转成[日期]
     *
     * @param time   字符串
     * @param format 日期格式
     * @return
     */
    public static Date parseStringToDate(String time, String format) {
        SimpleDateFormat mydate = new SimpleDateFormat(format);
        try {
            Date date = mydate.parse(time);
            if (date != null) {
                return date;
            }
        } catch (ParseException e) {
            logger.error("Date parse error:", e);
        }
        return null;
    }

    /**
     * 添加天数
     *
     * @param date   日期
     * @param amount 增加天数
     * @return
     */
    public static Date addDay(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.DAY_OF_MONTH, amount);

        return calendar.getTime();
    }

    /**
     * 获取某天的开始时间
     *
     * @param date 日期
     */
    public static Date getStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    /**
     * 获取某天的结束时间
     *
     * @param date 日期
     */
    public static Date getEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }


    /**
     * 获取指定时间小时的结束时间
     *
     * @param date 日期
     */
    public static Date getHourEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 比较两个时间是否不是同一天
     *
     * @param day1 时间1
     * @param day2 时间2
     * @return 若入参两个时间不在同一天，则返回true，否则返回false
     */
    public static boolean notSameDay(Date day1, Date day2) {
        if (day1 == null || day2 == null) {
            return false;
        }
        LocalDate date1 = LocalDate.fromDateFields(day1);
        LocalDate date2 = LocalDate.fromDateFields(day2);
        return 0 != Days.daysBetween(date1, date2).getDays();
    }

    /**
     * 比较两个时间是否是同一天
     *
     * @param day1 时间1
     * @param day2 时间2
     * @return 若入参两个时间在同一天，则返回true，否则返回false
     */
    public static boolean sameDay(Date day1, Date day2) {
        if (day1 == null || day2 == null) {
            return false;
        }
        LocalDate date1 = LocalDate.fromDateFields(day1);
        LocalDate date2 = LocalDate.fromDateFields(day2);
        return 0 == Days.daysBetween(date1, date2).getDays();
    }

    /**
     * 根据参数时间计算下月第一天的日期
     */
    public static Date nextMonthStart(Date day) {
        LocalDate date = LocalDate.fromDateFields(day);
        //时间向前推移一个月
        LocalDate nextMonth = date.plusMonths(1);

        //回到月首
        LocalDate nextMonthStart = nextMonth.withDayOfMonth(1);
        return nextMonthStart.toDate();
    }

    /**
     * 根据参数时间计算往后推进n天的日期
     */
    public static Date getNextDay(Date day, int step) {
        LocalDate date = LocalDate.fromDateFields(day);
        return date.plusDays(step).toDate();
    }

    /**
     * 根据参数时间推进7天的时间
     */
    public static Date nextWeek(Date day) {
        LocalDate date = LocalDate.fromDateFields(day);
        return date.plusWeeks(1).toDate();
    }

    /**
     * 根据参数时间计算是否是当月1号
     */
    public static boolean isMonthFirstDay(Date day) {
        LocalDate date = LocalDate.fromDateFields(day);
        return 1 == date.getDayOfMonth();
    }

    /**
     * 根据参数时间计算下周一的日期
     */
    public static Date nextWeekStart(Date day) {
        LocalDate date = LocalDate.fromDateFields(day);

        //推进到下周今天
        LocalDate nextWeek = date.plusWeeks(1);

        //推进到下周一
        LocalDate nextWeekToday = nextWeek.plusDays(1 - nextWeek.getDayOfWeek());
        return nextWeekToday.toDate();
    }

    /**
     * 根据参数时间计算是否是当月1号
     */
    public static boolean isWeekFirstDay(Date day) {
        LocalDate date = LocalDate.fromDateFields(day);
        return 1 == date.getDayOfWeek();
    }

    public static int minuteDiff(Date day1, Date day2) {
        if (day1 == null || day2 == null) {
            return 0;
        }
        LocalDateTime date1 = LocalDateTime.fromDateFields(day1);
        LocalDateTime date2 = LocalDateTime.fromDateFields(day2);
        return Minutes.minutesBetween(date1, date2).getMinutes();
    }

    public static int secondDiff(Date day1, Date day2) {
        if (day1 == null || day2 == null) {
            return 0;
        }
        LocalDateTime date1 = LocalDateTime.fromDateFields(day1);
        LocalDateTime date2 = LocalDateTime.fromDateFields(day2);
        return Seconds.secondsBetween(date1, date2).getSeconds();
    }

    public static void main(String[] args) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date day1 = sdf.parse("2018-05-30 10:36:00");
        Date day2 = sdf.parse("2018-05-30 10:39:00");
        System.out.println(secondDiff(day1, day2));
    }
}