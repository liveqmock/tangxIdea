package com.topaiebiz.goods.repair.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.repair.service.CategoryRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by hecaifeng on 2018/2/28.
 */
@RestController
@RequestMapping(value = "/goods/repair/category/", method = RequestMethod.POST)
public class CategoryRepairController {

    @Autowired
    private CategoryRepairService categoryRepairService;

    /**
     * 商家表中添加一二级类目
     *
     * @return
     */
    @RequestMapping(path = "/addCategory")
    public ResponseInfo addCategory() {
        categoryRepairService.addCategory();
        return new ResponseInfo();
    }

    /**
     * 自营店在商家表里面添加类目
     *
     * @return
     */
    @RequestMapping(path = "/addOwnCategory/{merchantId}")
    public ResponseInfo addOwnCategory(@PathVariable Long merchantId) {
        categoryRepairService.addOwnCategory(merchantId);
        return new ResponseInfo();
    }


}
