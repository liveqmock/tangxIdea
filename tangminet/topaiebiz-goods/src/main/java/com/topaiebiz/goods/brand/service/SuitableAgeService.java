package com.topaiebiz.goods.brand.service;

import com.topaiebiz.goods.brand.dto.SuitableAgeDto;

import java.util.List;

/**
 * Description 商品年龄段接口
 *
 * Author Hedda
 *
 * Date 2017年8月23日 下午4:14:59
 *
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 *
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface SuitableAgeService {

    /**
     * Description  查询年龄段列表
     *
     * Author Hedda
     *
     * @return
     */
    List<SuitableAgeDto> getSuitableAgeList();

    /**
     * Description  app端年龄段列表
     *
     * Author Hedda
     *
     * @return
     */
    List<SuitableAgeDto> getAppListSuitableAge();
}
