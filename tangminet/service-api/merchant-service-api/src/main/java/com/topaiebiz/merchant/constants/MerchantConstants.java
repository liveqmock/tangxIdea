package com.topaiebiz.merchant.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/***
 * @author yfeng
 * @date 2018-01-16 15:20
 */
public interface MerchantConstants {

    /**
     * 运费计算方式
     */
    @Getter
    enum FreightPriceType {
        GOODS_NUM(1, "件数"),
        VOLUME(2, "体积"),
        WEIGHT(3, "WEIGHT");

        private String name;
        private Integer value;

        private FreightPriceType(Integer value, String name) {
            this.value = value;
            this.name = name;
        }
    }

    class StoreStatus {
        /**
         * 正常或解冻
         */
        public static Integer OPEN = 0;
        /**
         * 变更中
         */
        public static Integer CHANGED = 1;
        /**
         * 已冻结
         */
        public static Integer FROZED = 2;
    }

    @AllArgsConstructor
    @Getter
    enum IsOwnShop {
        NOSELF_STORE(0, "非自营店"),
        SELF_STORE(1, "自营店");

        private Integer code;
        private String value;
    }

    @AllArgsConstructor
    @Getter
    enum SettleCycle {
        SETTLECYCLE_MONTH("MONTH", "月"),
        SETTLECYCLE_HALFMONTH("HALFMONTH", "半月"),
        SETTLECYCLE_WEEK("WEEK", "周"),
        SETTLECYCLE_FIVEDAY("FIVE_DAY", "5天");

        private String settleCycle;
        private String value;

        public static String getValueByCode(String code) {
            for (SettleCycle e : SettleCycle.values()) {
                if (e.getSettleCycle().equals(code)) {
                    return e.getValue();
                }
            }
            return null;
        }
    }

    class Haitao {
        /**
         * 0为否
         */
        public static final Integer DENY_HAITAO = 0;
        /**
         * 1为是
         */
        public static final Integer CONFIRM_HAITAO = 1;
    }
    
    class Identification {
        /**
         * 再次审核标识
         */
        public static final Integer IDENTIFICATION_ONE = 1;
    }

    //再次审核状态
    class ModifyStatus {
        /**
         * 审核通过
         */
        public static final Integer EXAMINE_ADOPT = 0;
        /**
         * 待审核
         */
        public static final Integer EXAMINE_WAIT = 1;
        /**
         * 审核未通过
         */
        public static final Integer EXAMINE_NOT_THROUGH = 2;
    }


}
