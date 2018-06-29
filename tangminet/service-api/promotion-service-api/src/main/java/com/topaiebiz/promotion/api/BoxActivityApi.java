package com.topaiebiz.promotion.api;

/**
 * 开宝箱活动
 */
public interface BoxActivityApi {
    /**
     * 定时初始化当前活动的礼卡剩余库存
     */
    void initRestStorage();
}
