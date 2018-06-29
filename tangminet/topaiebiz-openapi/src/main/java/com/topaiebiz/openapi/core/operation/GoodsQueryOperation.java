package com.topaiebiz.openapi.core.operation;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.dto.sku.ApiGoodsQueryDTO;
import com.topaiebiz.openapi.core.AbstractOperation;
import com.topaiebiz.openapi.dto.RequestParamDTO;
import com.topaiebiz.openapi.facade.GoodsServiceFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by ward on 2018-05-16.
 */
@Slf4j
@Component
public class GoodsQueryOperation extends AbstractOperation {

    @Autowired
    private GoodsServiceFacade goodsServiceFacade;

    @Override
    public ResponseInfo operation(RequestParamDTO paramDTO) {
        ApiGoodsQueryDTO goodsQueryDTO = super.convertStrToBean(paramDTO.getParams(), ApiGoodsQueryDTO.class);
        goodsQueryDTO.setStoreId(paramDTO.getStoreId());
        log.info(">>>>>>>>>>query goods'page params:{}", JSON.toJSONString(goodsQueryDTO));
        return new ResponseInfo(goodsServiceFacade.getGoodsList(goodsQueryDTO));
    }
}
