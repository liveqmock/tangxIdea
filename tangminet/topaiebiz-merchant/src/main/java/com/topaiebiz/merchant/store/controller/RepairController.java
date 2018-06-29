package com.topaiebiz.merchant.store.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.merchant.store.service.MerchantRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/***
 * @author yfeng
 * @date 2018-02-26 19:10
 */
@RestController
@RequestMapping(path = "/merchant/repair", method = RequestMethod.POST)
public class RepairController {

    @Autowired
    private MerchantRepairService repairService;

    @RequestMapping(path = "/avatars")
    public ResponseInfo getStoreInfoList(){
        repairService.repairMerchants();
        return new ResponseInfo();
    }

    @RequestMapping(path="/merchantId")
    public ResponseInfo moteMerchantId(){
        repairService.moteMerchantId();
        return new ResponseInfo();
    }
}