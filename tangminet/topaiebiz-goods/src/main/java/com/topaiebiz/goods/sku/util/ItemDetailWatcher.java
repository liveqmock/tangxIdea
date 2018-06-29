package com.topaiebiz.goods.sku.util;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by hecaifeng on 2018/6/11.
 */
@Getter
@NoArgsConstructor
public class ItemDetailWatcher {
    private Long itemId;
    private long totalSpend;
    private Stopwatch stopwatch = Stopwatch.createStarted();
    private LinkedHashMap<String, Long> spendDetail = new LinkedHashMap<>();
    private static long SLOW_TIME = 1000;

    public ItemDetailWatcher(Long itemId) {
        this.itemId = itemId;
    }

    public void logOperation(String record) {
        long spend = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        spendDetail.put(record, spend);
        stopwatch.reset().start();
        totalSpend += spend;
    }

    public void logPerformance(Logger logger) {
        if (totalSpend > SLOW_TIME) {
            logger.error("item {} 耗时 {} 毫秒  详细信息:{}", itemId, totalSpend, JSON.toJSONString(spendDetail));
        }
        stopwatch.stop();
    }
}
