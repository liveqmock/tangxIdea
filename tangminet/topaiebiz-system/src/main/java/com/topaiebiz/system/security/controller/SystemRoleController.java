package com.topaiebiz.system.security.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.security.dto.SystemRoleDto;
import com.topaiebiz.system.security.exception.SystemExceptionEnum;
import com.topaiebiz.system.security.service.SystemRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/base/system/role", method = RequestMethod.POST)
public class SystemRoleController {

    @Autowired
    private SystemRoleService systemRoleService;

    @PermissionController(value = PermitType.PLATFORM,operationName = "角色分页查询")
    @RequestMapping(value = "/getList")
    public ResponseInfo getList(@RequestBody SystemRoleDto systemRoleDto) {
        return new ResponseInfo(systemRoleService.getList(systemRoleDto));
    }

    @PermissionController(value = PermitType.PLATFORM, operationName = "新增角色")
    @RequestMapping(value = "/addRole")
    public ResponseInfo addRole(@RequestBody SystemRoleDto systemRoleDto) throws GlobalException {
        //参数校验
        if (StringUtils.isEmpty(systemRoleDto.getName())) {
            throw new GlobalException(SystemExceptionEnum.ROLE_NAME_IS_NULL);
        }
        if(CollectionUtils.isEmpty(systemRoleDto.getResourceIds())){
            throw new GlobalException(SystemExceptionEnum.RESOURCE_IS_NULL);
        }
        systemRoleService.save(systemRoleDto);
        return new ResponseInfo();

    }

    @PermissionController(value = PermitType.PLATFORM, operationName = "修改角色")
    @RequestMapping(value = "/editRole", method = RequestMethod.POST)
    public ResponseInfo editRole(@RequestBody SystemRoleDto systemRoleDto, BindingResult result) throws GlobalException {
        //参数校验
        if (StringUtils.isEmpty(systemRoleDto.getId())) {
            throw new GlobalException(SystemExceptionEnum.ROLE_ID_IS_NULL);
        }
        if (StringUtils.isEmpty(systemRoleDto.getName())) {
            throw new GlobalException(SystemExceptionEnum.ROLE_NAME_IS_NULL);
        }
        if(CollectionUtils.isEmpty(systemRoleDto.getResourceIds())){
            throw new GlobalException(SystemExceptionEnum.RESOURCE_IS_NULL);
        }
        systemRoleService.edit(systemRoleDto);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "删除角色")
    @RequestMapping(value = "/removeRole")
    public ResponseInfo removeRole(@RequestBody List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            throw new GlobalException(SystemExceptionEnum.ROLE_ID_IS_NULL);
        }
        systemRoleService.remove(ids);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM, operationName = "角色下拉列表")
    @RequestMapping(path = "/getRole")
    public ResponseInfo getRole() {
        return new ResponseInfo(systemRoleService.getRole());
    }

    @PermissionController(value = PermitType.PLATFORM, operationName = "编辑回显详情")
    @RequestMapping(path = "/getDetail/{id}")
    public ResponseInfo getDetail(@PathVariable Long id) {
        return new ResponseInfo(systemRoleService.getRoleDetail(id));
    }


}
