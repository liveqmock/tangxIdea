package com.topaiebiz.openapi.core.operation;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.openapi.core.AbstractOperation;
import com.topaiebiz.openapi.dto.GoodStockNumDTO;
import com.topaiebiz.openapi.dto.RequestParamDTO;
import com.topaiebiz.openapi.dto.ResponseResultDTO;
import com.topaiebiz.openapi.facade.GoodSkuServiceFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description 修改商品库存
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/27 13:20
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class UpdateGoodStockOperation extends AbstractOperation {

    @Autowired
    private GoodSkuServiceFacade goodSkuServiceFacade;

    @Override
    public ResponseInfo operation(RequestParamDTO paramDTO) {
        GoodStockNumDTO goodStockNumDTO = super.convertStrToBean(paramDTO.getParams(), GoodStockNumDTO.class);
        goodStockNumDTO.setStoreId(paramDTO.getStoreId());
        log.info(">>>>>>>>>>update goods's stockNumber params:{}", JSON.toJSONString(goodStockNumDTO));
        goodSkuServiceFacade.updateStockNum(goodStockNumDTO);
        return ResponseResultDTO.success();
    }
}
