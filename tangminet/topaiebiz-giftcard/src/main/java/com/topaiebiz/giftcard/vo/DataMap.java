package com.topaiebiz.giftcard.vo;

import java.util.HashMap;
import java.util.List;

/**
 * @description: 通用Map数据处理
 * @author: Jeff Chen
 * @date: created in 上午10:57 2018/1/19
 */
public class DataMap extends HashMap{

    public Integer getInt(String key) {
        Object obj = get(key);
        if (obj instanceof java.lang.Integer) {
            return Integer.parseInt(String.valueOf(obj));
        }
        return 0;
    }

    public Long getLong(String key) {
        Object obj = get(key);
        if (obj instanceof java.lang.Long||obj instanceof java.lang.Integer) {
            return Long.parseLong(String.valueOf(obj));
        }
        return 0L;
    }

    public List getList(String key) {
        Object obj = get(key);
        if (obj instanceof java.util.List) {
            return (List) obj;
        }
        return null;
    }

    public String getString(String key) {
        Object obj = get(key);
        if (obj instanceof java.lang.String) {
            return String.valueOf(obj);
        }
        return null;
    }
}
