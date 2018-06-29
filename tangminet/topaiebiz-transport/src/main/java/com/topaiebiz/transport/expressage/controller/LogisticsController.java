package com.topaiebiz.transport.expressage.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.transport.expressage.dto.LogisticsCompanyDto;
import com.topaiebiz.transport.expressage.exception.ExpressageExceptionEnum;
import com.topaiebiz.transport.expressage.service.ExpressageService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/transport/logistics", method = RequestMethod.POST)
public class LogisticsController {

    @Autowired
    private ExpressageService expressageService;

    @PermissionController(value = PermitType.PLATFORM,operationName = "物流商家列表")
    @RequestMapping(value = "/getList")
    public ResponseInfo getList(@RequestBody LogisticsCompanyDto logisticsCompanyDto) {
        return new ResponseInfo(expressageService.getList(logisticsCompanyDto));
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "新增物流商家")
    @RequestMapping(value = "/add")
    public ResponseInfo add(@RequestBody @Valid LogisticsCompanyDto logisticsCompanyDto, BindingResult result) {
        if (result.hasErrors()) {
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        expressageService.add(logisticsCompanyDto);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "编辑物流商家")
    @RequestMapping(value = "/edit")
    public ResponseInfo edit(@RequestBody @Valid LogisticsCompanyDto logisticsCompanyDto, BindingResult result) {
        if (result.hasErrors()) {
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        if(logisticsCompanyDto.getId() == null){
            throw new GlobalException(ExpressageExceptionEnum.ID_IS_NULL);
        }
        expressageService.edit(logisticsCompanyDto);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "删除物流商家")
    @RequestMapping(value = "/remove")
    public ResponseInfo remove(@RequestBody List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            throw new GlobalException(ExpressageExceptionEnum.ID_IS_NULL);
        }
        expressageService.remove(ids);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "启用物流商家")
    @RequestMapping(value = "/enabled")
    public ResponseInfo enabled(@RequestBody List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            throw new GlobalException(ExpressageExceptionEnum.ID_IS_NULL);
        }
        expressageService.editStatus(ids, 0);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "禁用物流商家")
    @RequestMapping(value = "/disabled")
    public ResponseInfo disabled(@RequestBody List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            throw new GlobalException(ExpressageExceptionEnum.ID_IS_NULL);
        }
        expressageService.editStatus(ids, 1);
        return new ResponseInfo();
    }

}
