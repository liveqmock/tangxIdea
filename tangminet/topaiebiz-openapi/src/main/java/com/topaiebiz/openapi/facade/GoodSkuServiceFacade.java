package com.topaiebiz.openapi.facade;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.goods.api.GoodsSkuApi;
import com.topaiebiz.openapi.dto.GoodStockNumDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/22 16:34
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class GoodSkuServiceFacade {

    @Autowired
    private GoodsSkuApi goodsSkuApi;

    /**
     * Description: 修改库存
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/22
     *
     * @param:
     **/
    public boolean updateStockNum(GoodStockNumDTO goodStockNumDTO) {
        boolean result = goodsSkuApi.stockNumberToZero(goodStockNumDTO.getArticleNumber(), goodStockNumDTO.getStoreId(), goodStockNumDTO.getStockNum());
        log.info(">>>>>>>>>>update good's sku stockNum, params:{}, result:{}", JSON.toJSONString(goodStockNumDTO), result);
        return result;
    }


}
