package com.topaiebiz.goods.brand.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.brand.dto.SuitableAgeDto;
import com.topaiebiz.goods.brand.service.SuitableAgeService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description 商品年龄段控制层
 *
 * Author Hedda
 *
 * Date 2017年8月23日 下午4:14:40
 *
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 *
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping(value = "/goods/suitableage",method = RequestMethod.POST)
public class SuitableAgeController {

    @Autowired
    private SuitableAgeService suitableAgeService;

    /**
     * Description 查询年龄段列表
     *
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM,operationName = "年龄段列表")
    @RequestMapping(value = "/getSuitableAgeList")
    public ResponseInfo getSuitableAgeList() throws GlobalException {
        List<SuitableAgeDto> listSuitableAge = suitableAgeService.getSuitableAgeList();
        return new ResponseInfo(listSuitableAge);

    }
}
