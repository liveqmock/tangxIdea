package com.topaiebiz.trade.order.core.order.context;

import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StoreContext {

    private static final ThreadLocal<Map<Long, StoreInfoDetailDTO>> context = new ThreadLocal<>();

    public static void set(Map<Long, StoreInfoDetailDTO> storeMap) {
        context.set(storeMap);
    }

    public static Map<Long, StoreInfoDetailDTO> get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}