package com.topaiebiz.trade.constants;

/***
 * @author yfeng
 * @date 2018-03-26 10:49
 */
public interface RefundConstants {

    /**
     * 退款完成
     */
    class RefundStatus {
        /**
         * 完成
         */
        public static Integer COMPLETED = 6;
    }
    class RefundRange {
        /**
         * 整单退
         */
        public static Integer FULL = 1;
        /**
         * 单品退
         */
        public static Integer GOODS = 0;
    }
}