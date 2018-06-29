package com.topaiebiz.promotion.mgmt.service;

import com.topaiebiz.promotion.mgmt.dto.init.data.InitBoxActivityItemDTO;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitFloorCardDTO;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitFloorGoodsDTO;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitPromotionGoodsDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InitDataService {

    /**
     * 读取EXCEL的内容
     *
     * @param file 文件
     * @return
     */
    <T> List<T> readExcel(MultipartFile file, Integer type);

    /**
     * 新增宝箱奖品配置
     *
     * @param boxActivityItemList 宝箱奖品配置列表
     * @return
     */
    Integer addBoxActivityItemRecords(List<InitBoxActivityItemDTO> boxActivityItemList);

    /**
     * 新增记录
     *
     * @param floorGoodsList 活动商品列表
     * @return
     */
    Integer addFloorGoodsRecords(List<InitFloorGoodsDTO> floorGoodsList);

    /**
     * 新增记录
     *
     * @param promotionGoodsList 活动商品列表
     * @param promotionId        活动ID
     * @return
     */
    Integer addPromotionGoodsRecords(List<InitPromotionGoodsDTO> promotionGoodsList, Long promotionId);

    /**
     * 新增记录
     *
     * @param floorCardList 活动商品列表
     * @return
     */
    Integer addFloorCardRecords(List<InitFloorCardDTO> floorCardList);

}
