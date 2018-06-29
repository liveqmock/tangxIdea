package com.topaiebiz.trade.order.core.order.context;

import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SkuContext {

    private static final ThreadLocal<Map<Long, GoodsSkuDTO>> context = new ThreadLocal<>();

    public static void set(List<GoodsSkuDTO> skuList) {
        Map<Long, GoodsSkuDTO> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(skuList)) {
            for (GoodsSkuDTO skuDTO : skuList) {
                map.put(skuDTO.getId(), skuDTO);
            }
        }
        context.set(map);
    }

    public static void set(Map<Long, GoodsSkuDTO> skuMap) {
        context.set(skuMap);
    }

    public static Map<Long, GoodsSkuDTO> get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}