package com.topaiebiz.openapi.facade;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.dto.sku.ApiGoodsDTO;
import com.topaiebiz.goods.dto.sku.ApiGoodsQueryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by ward on 2018-05-16.
 */
@Slf4j
@Component
public class GoodsServiceFacade {

    @Autowired
    private GoodsApi goodsApi;

    /**
     * Description: 修改库存
     * <p>
     * Author: ward
     * createTime: 2018/5/16
     *
     * @param:
     **/
    public PageInfo<ApiGoodsDTO> getGoodsList(ApiGoodsQueryDTO apiGoodsQueryDTO) {
        log.info(">>>>>>>>>>query good's list, params:{}", JSON.toJSONString(apiGoodsQueryDTO));
        return goodsApi.getGoodsList(apiGoodsQueryDTO);
    }

    public Boolean saveItems(List<ApiGoodsDTO> apiGoodsDTOList) {
        log.info(">>>>>>>>>>add good's api, params:{}", JSON.toJSONString(apiGoodsDTOList));
        return goodsApi.saveItems(apiGoodsDTOList);
    }
}
