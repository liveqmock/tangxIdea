package com.topaiebiz.settlement.contants;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface Constants {

    /**
     * 结算状态
     */
    @AllArgsConstructor
    @Getter
    enum SettlementStateEnum {
        NO_MERCHANT_CHECK(1, "待商家审核"),
        NO_COMMERCE_CHECK(2, "待商务审核"),
        NO_FINANCE_CHECK(3, "待财务审核"),
        FINISHED(4, "已结算");

        private Integer code;
        private String desc;

        public static String getValueByCode(Integer code) {
            for (SettlementStateEnum e : SettlementStateEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getDesc();
                }
            }
            return null;
        }
    }

    //导出起始条数
    int EXPORT_PAGE_NO = 0;
    //导出最大条数
    int EXPORT_PAGE_SIZE = 50000;
}
