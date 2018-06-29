package com.topaiebiz.promotion.mgmt.service;

import com.topaiebiz.promotion.mgmt.dto.floor.FloorDTO;
import com.topaiebiz.promotion.mgmt.dto.floor.FloorGoodsDTO;

import java.util.List;

/**
 * 楼层业务（限时秒杀/限量折扣）
 */
public interface FloorGoodsService {

    List<FloorDTO> selectListByConfigCode(String code);

    List<FloorGoodsDTO> selectGoodsListByFloorCode(String floorCode);
}
