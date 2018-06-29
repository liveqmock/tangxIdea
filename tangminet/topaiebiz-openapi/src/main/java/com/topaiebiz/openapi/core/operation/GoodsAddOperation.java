package com.topaiebiz.openapi.core.operation;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.dto.sku.ApiGoodsDTO;
import com.topaiebiz.openapi.core.AbstractOperation;
import com.topaiebiz.openapi.dto.RequestParamDTO;
import com.topaiebiz.openapi.facade.GoodsServiceFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ward on 2018-05-28.
 */
@Slf4j
@Component
public class GoodsAddOperation extends AbstractOperation {
    @Autowired
    private GoodsServiceFacade goodsServiceFacade;

    @Override
    public ResponseInfo operation(RequestParamDTO paramDTO) {
        ApiGoodsDTO apiGoodsDTO = super.convertStrToBean(paramDTO.getParams(), ApiGoodsDTO.class);
        apiGoodsDTO.setBelongStore(paramDTO.getStoreId());
        List<ApiGoodsDTO> apiGoodsDTOList = new ArrayList<>();
        apiGoodsDTOList.add(apiGoodsDTO);
        log.info(">>>>>>>>>>query goods'page params:{}", JSON.toJSONString(apiGoodsDTOList));
        return new ResponseInfo(goodsServiceFacade.saveItems(apiGoodsDTOList));
    }
}
