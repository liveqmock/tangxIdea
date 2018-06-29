package com.topaiebiz.goods.repair.service;

import com.topaiebiz.goods.repair.dto.ItemPicDTO;

import java.util.List;

/***
 * @author yfeng
 * @date 2018-03-10 16:27
 */
public interface ItemImageRepairService {

    void repairItemImages(Long itemId, List<ItemPicDTO> pics);
}