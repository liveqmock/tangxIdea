package com.topaiebiz.promotion.util;

import com.nebulapaas.common.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/***
 * @author yfeng
 * @date 2017-12-21 14:43
 */
public class SaleRateUtil {
    private static final BigDecimal TOP_RATE_LIMIT = new BigDecimal(99);
    private static final BigDecimal INCREMENT = new BigDecimal(5);

    public static Integer rendFakeSaleRate(Long goodsId, Date start) {
        if (goodsId == null) {
            return null;
        }

        //增加基于商品ID的固定数量，数值哈希以保证尽量基准值不同
        long goosIDHash = (goodsId.hashCode() % 15) + 5;
        BigDecimal goodsBase = new BigDecimal(goosIDHash);

        //每7分钟增加5
        int halfHour = 7 * 60 * 1000;
        long timeDiff = System.currentTimeMillis() - start.getTime();
        long increaseRate = (timeDiff / halfHour);
        if (timeDiff <= 0) {
            // 未开始
            return 0;
        }
        BigDecimal timeIncrease = new BigDecimal(increaseRate);

        //计算基准值 + ID哈希 + 时间增长
        BigDecimal saleRate = goodsBase.add(timeIncrease.multiply(INCREMENT));

        //防止出现超过99%
        if (less(TOP_RATE_LIMIT, saleRate)) {
            saleRate = TOP_RATE_LIMIT;
        }

        return saleRate.setScale(0).intValue();
    }

    public static Integer rendRate(Long goodsId, Date start, Date end) {
        if (goodsId == null) {
            return null;
        }

        //增加基于商品ID的固定数量，数值哈希以保证尽量基准值不同
        long goosIDHash = (goodsId.hashCode() % 15) + 5;
        BigDecimal goodsBase = new BigDecimal(goosIDHash);

        Date now = new Date();
        if (now.before(start)) {
            // 未开始
            return 0;
        }
        if (now.after(end)) {
            // 已经结束
            return 100;
        }

        //起止时间范围
        BigDecimal range = new BigDecimal(DateUtils.minuteDiff(start, end));

        //时间差值
        BigDecimal timeDiff = new BigDecimal(DateUtils.minuteDiff(start, now));
        BigDecimal increaseRate = timeDiff.divide(range, 2, RoundingMode.HALF_UP);

        //距极限值的差额
        long diffHash = 1 + goodsId.hashCode() % 5;
        //商品进度条上限
        BigDecimal good_top_limit = new BigDecimal(100 - diffHash);

        //计算基准值 + ID哈希 + 时间增长
        BigDecimal saleRate = goodsBase.add(increaseRate.multiply(good_top_limit));

        //防止出现超过商品上限
        if (less(good_top_limit, saleRate)) {
            saleRate = good_top_limit;
        }

        return saleRate.intValue();
    }

    public static boolean less(BigDecimal v1, BigDecimal v2) {
        int compare = v1.compareTo(v2);
        return compare < 0;
    }
}