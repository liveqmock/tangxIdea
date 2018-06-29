package com.topaiebiz.promotion.mgmt.moble.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.promotion.mgmt.dto.card.CardActivityConfigDTO;
import com.topaiebiz.promotion.mgmt.dto.floor.FloorCardDTO;
import com.topaiebiz.promotion.mgmt.service.CardActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/carActivity/customer", method = RequestMethod.POST)
public class AppCardActivityController {

    @Autowired
    private CardActivityService cardActivityService;

    /**
     * 获取礼卡秒杀活动列表
     *
     * @return
     */
    @RequestMapping("/secKillList")
    public ResponseInfo secKillList() {
        return new ResponseInfo(cardActivityService.getSecKillList());
    }

    /**
     * 获取礼卡活动配置列表（礼卡充值送、店铺联名卡）
     *
     * @param configCode 配置编号
     * @return
     */
    @RequestMapping("/configList/{configCode}")
    public ResponseInfo configList(@PathVariable("configCode") String configCode) {
        List<CardActivityConfigDTO> list = cardActivityService.getConfigList(configCode);
        return new ResponseInfo(list);
    }

    /**
     * 根据楼层CODE查询楼层礼卡列表
     *
     * @param floorCode 配置code
     * @return
     */
    @RequestMapping(path = "/floorList/{floorCode}")
    public ResponseInfo floorList(@PathVariable("floorCode") String floorCode) {
        List<FloorCardDTO> list = cardActivityService.getFloorList(floorCode);
        return new ResponseInfo(list);
    }

    /**
     * 获取礼卡秒杀活动列表
     *
     * @return
     */
    @RequestMapping("/secKill")
    public ResponseInfo secKill() {
        return new ResponseInfo(cardActivityService.getSecKill());
    }
}
