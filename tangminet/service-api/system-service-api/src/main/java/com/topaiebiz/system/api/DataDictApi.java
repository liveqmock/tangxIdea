package com.topaiebiz.system.api;

import com.topaiebiz.system.dto.DataDictDto;

import java.util.List;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/10 17:32
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface DataDictApi {


    /**
    *
    * Description: 根据ID查询
    *
    * Author: hxpeng
    * createTime: 2018/1/10
    *
    * @param:
    **/
    DataDictDto getById(Long id);

    /**
    *
    * Description: 根据code 获取数据字段集合
    *
    * Author: hxpeng
    * createTime: 2018/1/10
    *
    * @param:
    **/
    List<DataDictDto> getByCode(String code);

    /**
    *
    * Description: 插入字体
    *
    * Author: hxpeng
    * createTime: 2018/1/10
    *
    * @param: 
    **/
    Long insert(DataDictDto dataDictDto);

    /**
    *
    * Description: 删除数据字典by ID
    *
    * Author: hxpeng
    * createTime: 2018/1/10
    *
    * @param:
    **/
    boolean deleteById(Long id);

}
