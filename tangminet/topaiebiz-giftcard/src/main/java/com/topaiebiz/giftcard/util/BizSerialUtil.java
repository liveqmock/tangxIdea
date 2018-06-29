package com.topaiebiz.giftcard.util;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @description: 业务序号生成工具
 * @author: Jeff Chen
 * @date: created in 下午7:17 2018/1/16
 */
public class BizSerialUtil {

    public static final long ID_START = 1000000L;

    private BizSerialUtil() {
    }

    /**
     * 卡批次号，后台应用未考虑高并发
     *
     * @return
     */
    public static String getBatchNo() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("BN").append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).toString());
        return stringBuffer.toString();
    }

    /**
     * 生成卡id
     *
     * @param prefix
     * @return
     */
    public static String getCardNo(String prefix) {
        StringBuffer sb = new StringBuffer();
        sb.append(prefix).append(System.currentTimeMillis()).append(getFixLenthString(2));
        return sb.toString();
    }

    /**
     * 在last基础上递增
     *
     * @param prefix
     * @param last
     * @return
     */
    public static String getCardNo(String prefix,Long last) {
        StringBuffer sb = new StringBuffer();
        sb.append(prefix).append(last);
        return sb.toString();
    }

    /**
     * 从指定字符串指定位置提取后面的整数
     * @param src
     * @param idx
     * @return
     */
    public static Long extractLong(String src, int idx) {
        if (StringUtils.isBlank(src) ||
                idx < 0 || idx > src.length()) {
            return 0L;
        }
        return Long.valueOf(src.substring(idx, src.length()));
    }

    /**
     * 固定长度随机数
     *
     * @param strLength
     * @return
     */
    public static String getFixLenthString(int strLength) {

        Random rm = new Random();
        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);
        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);
        // 返回固定的长度的随机数
        return fixLenthString.substring(1, strLength + 1);
    }

    /**
     * UUID字符串
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
