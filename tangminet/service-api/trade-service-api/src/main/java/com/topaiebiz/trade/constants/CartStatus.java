package com.topaiebiz.trade.constants;

/***
 * @author yfeng
 * @date 2018-01-30 15:36
 */
public interface CartStatus {
    class GoodsStatus {
        //正常
        public static final Integer NORMAL = 0;
        //已下架
        public static final Integer DOWN = 1;
        //违规
        public static final Integer INVIOLATIONODTHESHELVES = 2;
        //已冻结
        public static final Integer FREEZE = 3;
        //库存不足
        public static final Integer STORAGE_LACK = 4;
    }

    class GoodsFreezeFlag {
        /**
         * 已冻结
         */
        public static final Integer GOOD_FREEZE_YES = 1;
        /**
         * 未冻结
         */
        public static final Integer GOOD_FREEZE_NO = 0;
    }
}