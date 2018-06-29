package com.topaiebiz.dec.controller;


import com.nebulapaas.web.response.ResponseInfo;

import com.topaiebiz.dec.service.AppHomePageService;
import com.topaiebiz.dec.service.ModuleGoodsService;
import com.topaiebiz.dec.service.TitleGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/decorate/appHomePage")

public class AppHomePageController {

    @Autowired
    private AppHomePageService appHomePageService;

    @Autowired
    private TitleGoodsService titleGoodsService;

    @Autowired
    private ModuleGoodsService moduleGoodsService;

    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public ResponseInfo search(@RequestBody Long templateId) {
        return new ResponseInfo(appHomePageService.search(templateId));
    }

    @RequestMapping(value = "/refreshCache",method = RequestMethod.POST)
    public ResponseInfo refreshCache(@RequestBody Long templateId) {
        appHomePageService.refreshCache(templateId);
        return new ResponseInfo();
    }

    @RequestMapping(value = "/storeSearch",method = RequestMethod.POST)
    public ResponseInfo storeSearch(@RequestBody Long storeId) {
        return new ResponseInfo(appHomePageService.storeSearch(storeId));
    }

    @RequestMapping(value = "/getTitleItems/{titleId}",method = RequestMethod.GET)
    public ResponseInfo getTitleItems(@PathVariable Long titleId){
        return new ResponseInfo(titleGoodsService.getTitleItemDto(titleId));
    }

    @RequestMapping(value = "/getModuleItems/{moduleId}",method = RequestMethod.GET)
    public ResponseInfo getModuleItems(@PathVariable Long moduleId){
        return new ResponseInfo(moduleGoodsService.getModuleItemDto(moduleId));
    }
}
