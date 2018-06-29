package com.topaiebiz.merchant.freight.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.merchant.enter.exception.MerchantEnterException;
import com.topaiebiz.merchant.freight.dto.AddFreightTempleteDto;
import com.topaiebiz.merchant.freight.dto.FreightTempleteDto;
import com.topaiebiz.merchant.freight.dto.MerFreightTempleteDto;
import com.topaiebiz.merchant.freight.service.MerchantFreightService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 运费模版
 */
@RestController
@RequestMapping(path = "/merchant/freight",method = RequestMethod.POST)
public class MerchantFreightController {

    @Autowired
    private MerchantFreightService merchantFreightService;

    //运费模版分页
    @RequestMapping(path = "/getMerFreightTempleteList" )
    @PermissionController(value = PermitType.MERCHANT, operationName = "运费模板列表分页检索")
    public ResponseInfo getMerFreightTempleteList(@RequestBody MerFreightTempleteDto merFreightTempleteDto) {
        return new ResponseInfo(merchantFreightService.getMerFreightTempleteList(merFreightTempleteDto));
    }

    @RequestMapping(path = "/addMerFreightTemplete")
    @PermissionController(value = PermitType.MERCHANT, operationName = "添加运费模板")
    public ResponseInfo addMerFreightTemplete(@RequestBody AddFreightTempleteDto addFreightTempleteDto)
            throws GlobalException {
        if (addFreightTempleteDto.getFreightName() == null) {
            throw new GlobalException(MerchantEnterException.FREIGHTNAME_NOT_NOLL);
        }
        if (addFreightTempleteDto.getPricing() == null) {
            throw new GlobalException(MerchantEnterException.PRICING_NOT_NOLL);
        }
        merchantFreightService.saveMerFreightTemplete(addFreightTempleteDto);
        return new ResponseInfo();
    }

    @RequestMapping(path = "/delectMerFreightTempleteById/{id}", method = RequestMethod.POST)
    @ResponseBody
    @PermissionController(value = PermitType.MERCHANT, operationName = "删除运费模板")
    public ResponseInfo cancelMerFreightTempleteById(@PathVariable  Long id) {
        merchantFreightService.removeMerFreightTempleteById(id);
        return new ResponseInfo();
    }

    @RequestMapping(path = "/selectMerFreightTempleteById/{id}")
    @PermissionController(value = PermitType.MERCHANT, operationName = "运费模板修改回显")
    public ResponseInfo selectMerFreightTempleteById(@PathVariable Long id) {
        return new ResponseInfo(merchantFreightService.selectMerFreightTempleteById(id));
    }

    @RequestMapping(path = "/updateMerFreightTempleteById")
    @PermissionController(value = PermitType.MERCHANT, operationName = "修改运费模板")
    public ResponseInfo updateMerFreightTempleteById(@RequestBody AddFreightTempleteDto addFreightTempleteDto) {
        if (addFreightTempleteDto.getFreightName() == null) {
            throw new GlobalException(MerchantEnterException.FREIGHTNAME_NOT_NOLL);
        }
        if (addFreightTempleteDto.getPricing() == null) {
            throw new GlobalException(MerchantEnterException.PRICING_NOT_NOLL);
        }
        merchantFreightService.updateMerFreightTempleteById(addFreightTempleteDto);
        return new ResponseInfo();
    }

    @RequestMapping(path = "/getList")
    @PermissionController(value = PermitType.MERCHANT, operationName = "发布商品获取运费模版")
    public ResponseInfo getList() {
        List<FreightTempleteDto> dtoList = merchantFreightService.getList();
        return new ResponseInfo(dtoList);
    }

}
