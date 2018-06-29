package com.topaiebiz.promotion.mgmt.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitBoxActivityItemDTO;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitFloorCardDTO;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitFloorGoodsDTO;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitPromotionGoodsDTO;
import com.topaiebiz.promotion.mgmt.service.InitDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.topaiebiz.promotion.constants.PromotionConstants.InitDataRecord.*;

@RestController
@RequestMapping(path = "/initData/mgmt", method = RequestMethod.POST)
public class InitDataController {
    @Autowired
    private InitDataService initDataService;

    /**
     * 读取配置文件, 获取活动商品配置记录
     *
     * @param file 文件路径
     * @return
     */
    @RequestMapping("/readPromotionGoods")
    public ResponseInfo readPromotionGoods(@RequestParam("file") MultipartFile file) {
        return new ResponseInfo(initDataService.readExcel(file, PROMOTION_GOODS));
    }

    /**
     * 新增活动商品记录
     *
     * @param promotionGoodsList 活动商品列表
     * @param promotionId        活动ID
     * @return
     */
    @RequestMapping("/addPromotionGoods/{promotionId}")
    public ResponseInfo addPromotionGoods(@RequestBody List<InitPromotionGoodsDTO> promotionGoodsList, @PathVariable("promotionId") Long promotionId) {
        return new ResponseInfo(initDataService.addPromotionGoodsRecords(promotionGoodsList, promotionId));
    }

    /**
     * 读取配置文件, 获取楼层配置记录
     *
     * @param file 文件路径
     * @return
     */
    @RequestMapping("/readFloorGoods")
    public ResponseInfo readFloorGoods(@RequestParam("file") MultipartFile file) {
        return new ResponseInfo(initDataService.readExcel(file, FLOOR_GOODS));
    }

    /**
     * 新增楼层记录
     *
     * @param floorGoodsList 楼层列表
     * @return
     */
    @RequestMapping("/addFloorGoods")
    public ResponseInfo addFloorGoods(@RequestBody List<InitFloorGoodsDTO> floorGoodsList) {
        return new ResponseInfo(initDataService.addFloorGoodsRecords(floorGoodsList));
    }

    /**
     * 读取配置文件, 获取楼层配置记录
     *
     * @param file 文件
     * @return
     */
    @RequestMapping("/readBoxActivityItems")
    public ResponseInfo readBoxActivityItems(@RequestParam("file") MultipartFile file) {
        return new ResponseInfo(initDataService.readExcel(file, BOX_ACTIVITY_ITEMS));
    }

    /**
     * 新增宝箱奖品配置记录
     *
     * @param boxActivityItemList 宝箱奖品配置列表
     * @return
     */
    @RequestMapping("/addBoxActivityItems")
    public ResponseInfo addBoxActivityItems(@RequestBody List<InitBoxActivityItemDTO> boxActivityItemList) {
        return new ResponseInfo(initDataService.addBoxActivityItemRecords(boxActivityItemList));
    }

    /**
     * 读取配置文件, 获取楼层配置记录
     *
     * @param file 文件路径
     * @return
     */
    @RequestMapping("/readFloorCard")
    public ResponseInfo readFloorCard(@RequestParam("file") MultipartFile file) {
        return new ResponseInfo(initDataService.readExcel(file, FLOOR_CARD));
    }

    /**
     * 新增楼层记录
     *
     * @param floorCardList 楼层列表
     * @return
     */
    @RequestMapping("/addFloorCard")
    public ResponseInfo addFloorCard(@RequestBody List<InitFloorCardDTO> floorCardList) {
        return new ResponseInfo(initDataService.addFloorCardRecords(floorCardList));
    }
}
