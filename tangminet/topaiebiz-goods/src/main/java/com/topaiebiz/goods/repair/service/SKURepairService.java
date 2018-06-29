package com.topaiebiz.goods.repair.service;

import com.topaiebiz.goods.repair.dto.RepairResultDTO;
import com.topaiebiz.goods.sku.entity.GoodsSkuEntity;

import java.util.List;

/**
 * Created by hecaifeng on 2018/2/8.
 */
public interface SKURepairService {

    /**
     * Description 根据类目id，和属性名称查询对应属性
     * <p>
     * Author Hedda
     *
     * @return
     */
    RepairResultDTO repareGoods(Long categoryId, Long lastId, Integer taskSize);

    boolean repairSKU(Long skuId);

    boolean stop();

    void batchRepairSKU(List<GoodsSkuEntity> skuList);
}