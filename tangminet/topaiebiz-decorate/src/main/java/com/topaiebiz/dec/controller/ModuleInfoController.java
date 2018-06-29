package com.topaiebiz.dec.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.dec.dto.ModuleDetailDto;
import com.topaiebiz.dec.service.ModuleInfoService;

import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 模块信息详情表
 前端控制器
 * </p>
 *
 * @author hzj
 * @since 2018-01-08
 */
@RestController
@RequestMapping(value = "/decorate/moduleInfo",method = RequestMethod.POST)
public class ModuleInfoController {

    @Autowired
    ModuleInfoService moduleInfoService;

    @PermissionController(value = PermitType.PLATFORM,operationName = "根据模块详情集合批量添加模块详情")
    @RequestMapping(value = "/addModuleInfo")
    public ResponseInfo addModuleInfo(@RequestBody List<ModuleDetailDto> moduleDetailDtos) throws GlobalException {
        moduleInfoService.saveModuleInfoDto(moduleDetailDtos);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "根据模块详情集合修改模块详情")
    @RequestMapping(value ="/modifyModuleInfo")
    public ResponseInfo modifyModuleInfo(@RequestBody  List<ModuleDetailDto> moduleDetailDtos) throws GlobalException {
        moduleInfoService.modifyModuleInfo(moduleDetailDtos);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "根据模块详情id删除模块详情")
    @RequestMapping(value ="/removeModuleInfo")
    public ResponseInfo deleteModuleInfo(@RequestBody Long moduleInfoDtoId) throws GlobalException{
       moduleInfoService.deleteModuleInfo(moduleInfoDtoId);
       return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "根据模块id集合查询模块详情")
    @RequestMapping(value="/searchModuleInfo")
    public ResponseInfo searchModuleInfo(@RequestBody List<Long> moduleIds) throws GlobalException{
        return new ResponseInfo(moduleInfoService.getByModuleIds(moduleIds));
    }

}
