package com.topaiebiz.trade.order.util;

import java.math.BigDecimal;

/***
 * @author Yangfeng
 * @date 2017-03-13 21:58
 */
public class MathUtil {
    public static BigDecimal oneFen = new BigDecimal("0.01");
    public static BigDecimal fenRate = new BigDecimal(100);

    public static BigDecimal multiply(BigDecimal v1, int v2) {
        return v1.multiply(new BigDecimal(v2));
    }

    public static BigDecimal multiply(BigDecimal v1, long v2) {
        return v1.multiply(new BigDecimal(v2));
    }

    public static BigDecimal mutliply(Double v1, Long v2) {
        BigDecimal bdV1 = new BigDecimal(v1);
        BigDecimal bdV2 = new BigDecimal(v2);
        return bdV1.multiply(bdV2);
    }

    public static boolean validEntityId(Long id) {
        return id != null && id > 0;
    }

    public static boolean unvalidEntityId(Long id) {
        return id == null || id <= 0;
    }

    public static boolean greaterThanZero(BigDecimal v1) {
        if (v1 == null) {
            return false;
        }
        return less(BigDecimal.ZERO, v1);
    }

    public static boolean less(BigDecimal v1, BigDecimal v2) {
        int compare = v1.compareTo(v2);
        return compare < 0;
    }

    public static boolean greator(BigDecimal v1, BigDecimal v2) {
        int compare = v1.compareTo(v2);
        return compare > 0;
    }

    public static boolean greateEq(BigDecimal v1, BigDecimal v2) {
        int compare = v1.compareTo(v2);
        return compare >= 0;
    }

    public static BigDecimal max(BigDecimal v1, BigDecimal v2) {
        if (greateEq(v1, v2)) {
            return v1;
        }
        return v2;
    }

    public static BigDecimal min(BigDecimal v1, BigDecimal v2) {
        if (greateEq(v1, v2)) {
            return v2;
        }
        return v1;
    }

    public static BigDecimal min(BigDecimal v1, BigDecimal v2, BigDecimal v3) {
        if (greateEq(v1, v2)) {
            return min(v2, v3);
        } else {
            return min(v1, v3);
        }
    }

    public static BigDecimal cuculate(BigDecimal useful, BigDecimal goodsAmount, BigDecimal orderAmount) {
        BigDecimal val = useful.multiply(goodsAmount).divide(orderAmount, 2, BigDecimal.ROUND_DOWN);
        //小于1分的比例分配按照0处理
        if (less(val, oneFen)) {
            return BigDecimal.ZERO;
        }
        return val;
    }

    public static boolean sameValue(BigDecimal v1, BigDecimal v2) {
        if (v1 == null || v2 == null) {
            return false;
        }
        return v1.compareTo(v2) == 0;
    }

    /**
     * 校验金额的精度是否为分
     *
     * @param amount 待校验金额
     * @return
     */
    public static boolean isFenPrecision(BigDecimal amount) {
        if (amount == null) {
            return false;
        }
        Long fen = getFenVal(amount);
        BigDecimal yuanAmount = new BigDecimal(fen).divide(fenRate);
        return sameValue(amount, yuanAmount);
    }

    public static Long getFenVal(BigDecimal amount) {
        return amount.multiply(fenRate).longValue();
    }

    public static BigDecimal getScoreAmount(Integer score) {
        if (score == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(score).divide(fenRate, 2, BigDecimal.ROUND_DOWN);
    }

    public static Integer getScoreNum(BigDecimal score) {
        return score.multiply(fenRate).intValue();
    }
}