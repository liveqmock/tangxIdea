package com.topaiebiz.dec.controller;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.dec.dto.TemplateModuleDto;
import com.topaiebiz.dec.service.TemplateModuleService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 装修模板模块表 前端控制器
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@RestController
@Slf4j
@RequestMapping(value = "/decorate/templateModule",method = RequestMethod.POST)
public class TemplateModuleController {
    @Autowired
    TemplateModuleService templateModuleService;

    @PermissionController(value = PermitType.PLATFORM,operationName = "添加模板模块")
    @RequestMapping(value = "/addTemplateModule")
    public ResponseInfo addTemplateModule(@RequestBody List<TemplateModuleDto> templateModuleDtos) throws GlobalException {
        log.info(" add {} ", JSON.toJSONString(templateModuleDtos));
        templateModuleService.saveTemplateModuleDto(templateModuleDtos);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "根据模板id查询模板模块")
    @RequestMapping(value = "/searchTemplateModule")
    public ResponseInfo searchTemplateModule(@RequestBody Long infoId) throws GlobalException{
        log.info(" search {} ", String.valueOf(infoId));
        return new ResponseInfo(templateModuleService.getTemplateModuleDto(infoId));
    }
;
    @PermissionController(value = PermitType.PLATFORM,operationName = "根据模块id删除模板模块")
    @RequestMapping(value = "/removeTemplateModule")
    public ResponseInfo removeTemplateModule(@RequestBody Long id) throws GlobalException{
        log.info(" remove {} ", String.valueOf(id));
        templateModuleService.deleteById(id);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "修改模板模块")
    @RequestMapping(value = "/modifyTemplateModule")
    public ResponseInfo modifyTemplateModule(@RequestBody TemplateModuleDto templateModuleDto) throws GlobalException{
        log.info(" remove {} ",JSON.toJSONString(templateModuleDto));
        templateModuleService.updateTemplateModule(templateModuleDto);
        return new ResponseInfo();
    }
}
