package com.topaiebiz.promotion.mgmt.moble.controller;


import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.promotion.mgmt.dto.floor.FloorDTO;
import com.topaiebiz.promotion.mgmt.dto.floor.FloorGoodsDTO;
import com.topaiebiz.promotion.mgmt.service.FloorGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品秒杀楼层-分页
 */
@RestController
@RequestMapping(path = "/floorGoods/customer", method = RequestMethod.POST)
public class AppFloorGoodsController {
    @Autowired
    private FloorGoodsService floorGoodsService;

    /**
     * 根据配置CODE查询楼层列表-限量折扣列表
     *
     * @param configCode 配置code
     * @return
     */
    @RequestMapping(path = "/getList/{configCode}")
    public ResponseInfo getList(@PathVariable("configCode") String configCode) {
        List<FloorDTO> floorList = floorGoodsService.selectListByConfigCode(configCode);
        return new ResponseInfo(floorList);
    }

    /**
     * 根据楼层CODE查询楼层商品列表-限时秒杀
     *
     * @param floorCode 配置code
     * @return
     */
    @RequestMapping(path = "/getGoodsList/{floorCode}")
    public ResponseInfo getGoodsList(@PathVariable("floorCode") String floorCode) {
        List<FloorGoodsDTO> floorGoodsList = floorGoodsService.selectGoodsListByFloorCode(floorCode);
        return new ResponseInfo(floorGoodsList);
    }

}
