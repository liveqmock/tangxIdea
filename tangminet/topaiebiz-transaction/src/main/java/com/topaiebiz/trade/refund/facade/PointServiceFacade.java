package com.topaiebiz.trade.refund.facade;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.member.api.PointApi;
import com.topaiebiz.member.dto.point.AssetChangeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description 用户积分
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/30 14:29
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class PointServiceFacade {

    @Autowired
    private PointApi pointApi;


    public Boolean rollbackAccountAssets(AssetChangeDto assetChangeDto) {
        Boolean result = pointApi.rollbackAccountAssets(assetChangeDto);
        log.info("----------pointApi.addPoint-- request params:{}, response", JSON.toJSONString(assetChangeDto), result);
        return result;
    }


}
