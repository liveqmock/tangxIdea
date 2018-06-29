package com.topaiebiz.goods.repair.service;

import com.topaiebiz.goods.repair.dto.RepairResultDTO;
import com.topaiebiz.goods.sku.entity.ItemEntity;

import java.util.List;

/***
 * @author yfeng
 * @date 2018-02-11 15:29
 */
public interface ItemRepairService {

    RepairResultDTO repare(Long lastId, Long taskSize);

    boolean repair(Long itemId);

    void doRepair(List<ItemEntity> itemLists);

    boolean stop();

    void updateSaleVolume();
}