package com.topaiebiz.card.constant;

/**
 * @description: 礼卡适用范围
 * @author: Jeff Chen
 * @date: created in 上午10:10 2018/1/9
 */
public enum ApplyScopeEnum {
    APPLY_ALL(1, "全平台通用"),
    APPLY_INCLUDE(2, "部分店铺可用"),
    APPLY_EXCLUDE(3, "部分店铺不可用"),
    APPLY_GOODS(4, "部分商品可用");
    /**
     * 范围id
     */
    private Integer scopeId;
    /**
     * 范围说明
     */
    private String scopeDesc;

    ApplyScopeEnum(Integer scopeId, String scopeDesc) {
        this.scopeId = scopeId;
        this.scopeDesc = scopeDesc;
    }

    public Integer getScopeId() {
        return scopeId;
    }

    public String getScopeDesc() {
        return scopeDesc;
    }

    public static ApplyScopeEnum getById(Integer scopeId) {
        for (ApplyScopeEnum applyScopeEnum : ApplyScopeEnum.values()) {
            if (applyScopeEnum.getScopeId().equals(scopeId)) {
                return applyScopeEnum;
            }
        }
        return APPLY_ALL;
    }
}
