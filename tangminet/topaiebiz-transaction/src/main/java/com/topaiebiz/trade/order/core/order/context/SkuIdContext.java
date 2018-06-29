package com.topaiebiz.trade.order.core.order.context;

import com.alibaba.fastjson.JSON;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SkuIdContext {

    private static final ThreadLocal<List<Long>> context = new ThreadLocal<>();

    public static void set(List<Long> skuIds) {
        log.info(">>>>> SkuIdConext.set({})", JSON.toJSONString(skuIds));
        context.set(skuIds);
    }

    public static List<Long> get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}