package com.topaiebiz.promotion.mgmt.dto.init.data;

import lombok.Data;

import java.util.List;

@Data
public class InitDataDTO<T> {
    /**
     * 提示信息
     */
    private String message;

    /**
     * 记录列表
     */
    List<T> records;
}
