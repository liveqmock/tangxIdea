package com.topaiebiz.goods.repair.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.repair.ImageRepairService;
import com.topaiebiz.goods.repair.ItemRecoverService;
import com.topaiebiz.goods.repair.service.ItemRepairService;
import com.topaiebiz.goods.sku.dao.ItemDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/***
 * @author yfeng
 * @date 2018-02-11 15:28
 */
@Slf4j
@RestController
@RequestMapping(value = "/goods/repair/item/", method = RequestMethod.POST)
public class ItemRepairController {

    @Autowired
    private ImageRepairService imageRepairService;

    @Autowired
    private ItemRecoverService itemRecoverService;
    @Autowired
    private ItemRepairService itemRepairService;

    @Autowired
    private ItemDao itemDao;

    @RequestMapping(path = "/startJob")
    public ResponseInfo startJob() {
        itemRecoverService.start();
        return new ResponseInfo();
    }

    @RequestMapping(path = "/picStart")
    public ResponseInfo picStart() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                imageRepairService.start();
            }
        }).start();
        return new ResponseInfo();
    }

    @RequestMapping(path = "/itemPic/{itemId}")
    public ResponseInfo itemPic(@PathVariable Long itemId) {
        log.info("---->>> {}", itemId);
        imageRepairService.repair(itemId);
        return new ResponseInfo();
    }

    @RequestMapping(path = "/{id}")
    public ResponseInfo item(@PathVariable Long id) {
        itemRepairService.repair(id);
        return new ResponseInfo();
    }

    @RequestMapping(path = "/log")
    public ResponseInfo log() {
        log.info("测试log ---------->>>>>>>>>>>>>>>>>>>>");
        return new ResponseInfo();
    }

    @RequestMapping(path = "/updateSaleVolume")
    public ResponseInfo updateSaleVolume() {
        itemRepairService.updateSaleVolume();
        return new ResponseInfo();
    }
}