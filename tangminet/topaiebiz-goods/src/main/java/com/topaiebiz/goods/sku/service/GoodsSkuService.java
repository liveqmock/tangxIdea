package com.topaiebiz.goods.sku.service;

import com.baomidou.mybatisplus.service.IService;
import com.topaiebiz.goods.sku.entity.GoodsSkuEntity;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017/12/20 16:10
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface GoodsSkuService {

    boolean removeGoodsSkus(Long[] id);
}
