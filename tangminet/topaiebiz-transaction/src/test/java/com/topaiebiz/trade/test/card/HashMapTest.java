package com.topaiebiz.trade.test.card;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-05-08 15:26
 */
public class HashMapTest {

    @Test
    public void testValueOrder() {
        Map<String, Long> map = new LinkedHashMap();
        for (Long i = 1L; i <= 10; i++) {
            map.put("key" + i, 10 * i);
        }

        Class valuesClazz = map.values().getClass();
        System.out.println(valuesClazz);
        System.out.println(map.values());

        for (Map.Entry<String, Long> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "->" + entry.getValue());
        }
    }
}
