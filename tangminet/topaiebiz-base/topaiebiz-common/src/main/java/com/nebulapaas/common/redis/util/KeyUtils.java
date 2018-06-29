package com.nebulapaas.common.redis.util;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class KeyUtils {

    public static List<String> keys(String keyPrefix, List<Long> ids) {
        return ids.stream().map(id -> StringUtils.join(keyPrefix, id)).collect(Collectors.toList());
    }
}