package com.topaiebiz.basic.api;

import java.util.List;

public interface ConfigApi {

    /**
     * 查询code对应的value值
     *
     * @param code
     * @return
     */
    String getConfig(String code);

    Integer deleteConfig(String code);

    Boolean insertConfig(String code, String value);

    /**
     * 获取ID集合
     *
     * @param code 配置CODE
     * @return
     */
    List<Long> convertValueToList(String code);
}
