package com.topaiebiz.system.dict.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * Description 字典实体类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/10 17:47
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@TableName("t_sys_data_dict")
public class DataDictEntity extends BaseBizEntity<Long> {

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
