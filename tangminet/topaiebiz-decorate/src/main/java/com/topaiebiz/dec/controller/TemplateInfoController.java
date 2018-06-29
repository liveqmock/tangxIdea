package com.topaiebiz.dec.controller;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.dec.dto.TemplateInfoDto;
import com.topaiebiz.dec.service.TemplateInfoService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 装修模板信息表 前端控制器
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@RestController
@Slf4j
@RequestMapping(value = "/decorate/templateInfo",method = RequestMethod.POST)
public class TemplateInfoController {
    @Autowired
    TemplateInfoService templateInfoService;

    @PermissionController(value = PermitType.PLATFORM,operationName = "添加模板")
    @RequestMapping(value = "/addTemplateInfo")
    public ResponseInfo addTemplateInfo(@RequestBody TemplateInfoDto templateInfoDto) throws GlobalException {
        templateInfoService.saveTemplateInfoDto(templateInfoDto);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "根据模板id删除模板")
    @RequestMapping(value = "/removeTemplateInfo")
    public ResponseInfo removeTemplateInfo(@RequestBody Long id) throws GlobalException {
        log.info(" remove {} ",String.valueOf(id));
        templateInfoService.deleteTemplateInfo(id);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "修改模板")
    @RequestMapping(value = "/modifyTemplateInfo")
    public ResponseInfo modifyTemplateInfo(@RequestBody TemplateInfoDto templateInfoDto) throws GlobalException {
        log.info(" modify {} ", JSON.toJSONString(templateInfoDto));
        templateInfoService.modifyTemplateInfo(templateInfoDto);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "查询模板")
    @RequestMapping(value = "/searchTemplateInfo")
    public ResponseInfo searchTemplateInfo() throws GlobalException {
        return new ResponseInfo(templateInfoService.getTemplateInfoDtos());
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "根据商家id查询商家模板")
    @RequestMapping(value = "/searchStoreTemplate")
    public ResponseInfo searchStoreTemplate(@RequestBody Long storeId) throws GlobalException{
        return new ResponseInfo(templateInfoService.getStoreTemplate(storeId));
    }
}
