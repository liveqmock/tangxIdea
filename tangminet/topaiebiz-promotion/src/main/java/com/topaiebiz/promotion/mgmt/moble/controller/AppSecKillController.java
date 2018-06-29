package com.topaiebiz.promotion.mgmt.moble.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.promotion.mgmt.dto.HomeSeckillDto;
import com.topaiebiz.promotion.mgmt.dto.HomeSeckillGoodsDTO;
import com.topaiebiz.promotion.mgmt.dto.floor.FloorDTO;
import com.topaiebiz.promotion.mgmt.dto.floor.FloorGoodsDTO;
import com.topaiebiz.promotion.mgmt.service.PromotionGoodsService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/secKill/customer", method = RequestMethod.POST)
public class AppSecKillController {
    @Autowired
    private PromotionGoodsService promotionGoodsService;

    /**
     * 获取秒杀列表
     *
     * @param plateCode
     * @return
     */
    @RequestMapping(path = "/showList/{plateCode}")
    public ResponseInfo showList(@PathVariable("plateCode") String plateCode) {
        // 查询活动数据
        List<HomeSeckillDto> showList = promotionGoodsService.getSeckillList(plateCode);
        return new ResponseInfo(showList);
    }

    /**
     * 秒杀列表以楼层的形式展示
     *
     * @param plateCode
     * @return
     */
    @RequestMapping(path = "/floorList/{plateCode}")
    public ResponseInfo floorList(@PathVariable("plateCode") String plateCode) {
        // 查询活动数据
        List<HomeSeckillDto> secKillList = promotionGoodsService.getSeckillList(plateCode);
        if (CollectionUtils.isEmpty(secKillList)) {
            return new ResponseInfo();
        }
        List<FloorDTO> floorList = convertToFloor(secKillList);
        return new ResponseInfo(floorList);
    }

    /**
     * 将秒杀列表转换成楼层形式
     *
     * @param secKillList
     * @return
     */
    private List<FloorDTO> convertToFloor(List<HomeSeckillDto> secKillList) {
        List<FloorDTO> floorList = new ArrayList<>();
        for (HomeSeckillDto secKill : secKillList) {
            FloorDTO floor = new FloorDTO();
            //活动名称
            floor.setTypeName(secKill.getName());
            if (CollectionUtils.isEmpty(secKill.getPromotionGoodsDtos())) {
                break;
            }
            List<FloorGoodsDTO> floorGoodsList = new ArrayList<>();
            for (HomeSeckillGoodsDTO secKillGoods : secKill.getPromotionGoodsDtos()) {
                FloorGoodsDTO floorGoods = new FloorGoodsDTO();
                floorGoods.setGoodsId(secKillGoods.getId());
                floorGoods.setGoodsName(secKillGoods.getName());
                floorGoods.setImage(secKillGoods.getPictureName());
                floorGoods.setMarketPrice(secKillGoods.getMarketPrice());
                floorGoods.setDiscountPrice(secKillGoods.getDefaultPrice());
                floorGoodsList.add(floorGoods);
            }
            floor.setGoodsList(floorGoodsList);
            floorList.add(floor);
        }

        return floorList;
    }
}
