package com.topaiebiz.goods.repair.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.repair.SkuRecoverService;
import com.topaiebiz.goods.repair.service.SKURepairService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hecaifeng on 2018/2/8.
 */
@Slf4j
@RestController
@RequestMapping(value = "/goods/repair/sku", method = RequestMethod.POST)
public class SKURepairController {

    @Autowired
    private SkuRecoverService specRepairService;

    @Autowired
    private SKURepairService repairService;

    /**
     * Description 根据类目id，和属性名称查询对应属性
     * <p>
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/startJob")
    public ResponseInfo startJob() {
        specRepairService.start();
        return new ResponseInfo();
    }

    @RequestMapping(path = "/{id}")
    public ResponseInfo repairSKU(@PathVariable Long id) {
        repairService.repairSKU(id);
        return new ResponseInfo();
    }

    @RequestMapping(path = "/cate/{id}")
    public ResponseInfo test(@PathVariable(name = "id") Long id) {
        try {
            specRepairService.addCategoryTask(id);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return new ResponseInfo();
    }

    @RequestMapping(path = "/stop")
    public ResponseInfo stop() {
        repairService.stop();
        return new ResponseInfo();
    }
}