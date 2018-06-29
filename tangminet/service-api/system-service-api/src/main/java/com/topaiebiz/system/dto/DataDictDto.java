package com.topaiebiz.system.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Description 数据字典DOT
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/10 17:53
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class DataDictDto implements Serializable{

    private static final long serialVersionUID = 6720303423818124743L;

    /**
     * 数据字典ID
     */
    private Long id;

    /**
     * 数据字典code
     */
    private String code;

    /**
     * 数据字典字符描述
     */
    private String value;

    /**
     * 数据字典排序
     */
    private Integer sort;

    /**
     * 备注，保留字段
     */
    private String memo;

}
