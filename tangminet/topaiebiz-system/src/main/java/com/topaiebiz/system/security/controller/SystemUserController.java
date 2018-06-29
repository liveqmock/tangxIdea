package com.topaiebiz.system.security.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.system.annotation.NotLoginPermit;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.security.dto.SecurityUserDto;
import com.topaiebiz.system.security.dto.SystemUserDto;
import com.topaiebiz.system.security.exception.SystemExceptionEnum;
import com.topaiebiz.system.security.service.SystemUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/base/system/user",method = RequestMethod.POST)
@Slf4j
public class SystemUserController {

	@Autowired
	private SystemUserService systemUserService;

	@NotLoginPermit
	@RequestMapping(value = "/login")
	public ResponseInfo login(@RequestBody @Valid SystemUserDto systemUserDto, BindingResult result) {
		// 如果参数非法，抛出异常。
		if (result.hasErrors()) {
			// 初始化非法参数的提示信息。
			IllegalParamValidationUtils.initIllegalParamMsg(result);
			// 获取非法参数异常信息对象，并抛出异常。
			throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
		}
		//调用service查询
		SecurityUserDto securityUserDto = systemUserService.login(systemUserDto);
		return new ResponseInfo(securityUserDto);
	}

	@NotLoginPermit
	@RequestMapping(value = "/logout")
	public ResponseInfo logout(HttpServletRequest request) {
		String userLoginId = request.getHeader("userLoginId");
		systemUserService.logout(userLoginId);
		return new ResponseInfo();
	}

	@PermissionController(value = PermitType.PLATFORM,operationName = "用户分页查询")
	@RequestMapping(value = "/getList")
	public ResponseInfo getList(@RequestBody SystemUserDto systemUserDto) {
		return new ResponseInfo(systemUserService.getList(systemUserDto));
	}

	@PermissionController(value = PermitType.PLATFORM,operationName = "新增用户查询会员")
	@RequestMapping(value = "/getMember/{mobilePhone}")
	public ResponseInfo getMember(@PathVariable String mobilePhone) {
		if (StringUtils.isBlank(mobilePhone)){
			throw new GlobalException(SystemExceptionEnum.MOBILEPHONE_IS_NULL);
		}
		return new ResponseInfo(systemUserService.getMember(mobilePhone));
	}

	@PermissionController(value = PermitType.PLATFORM,operationName = "新增用户保存")
	@RequestMapping(value = "/addUser")
	public ResponseInfo addUser(@RequestBody @Valid SystemUserDto systemUserDto, BindingResult result) {
		// 如果参数非法，抛出异常。
		if (result.hasErrors()) {
			// 初始化非法参数的提示信息。
			IllegalParamValidationUtils.initIllegalParamMsg(result);
			// 获取非法参数异常信息对象，并抛出异常。
			throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
		}
		if(StringUtils.isBlank(systemUserDto.getMobilePhone())){
			throw new GlobalException(SystemExceptionEnum.MOBILEPHONE_IS_NULL);
		}
		if(systemUserDto.getRoleId() == null){
			throw new GlobalException(SystemExceptionEnum.ROLE_NAME_IS_NULL);
		}
		if(systemUserDto.getPassword() == null || "".equals(systemUserDto.getPassword().trim())){
			throw new GlobalException(SystemExceptionEnum.PASSWORD_IS_NULL);
		}
		systemUserService.saveUser(systemUserDto);
		return new ResponseInfo();
	}

	@PermissionController(value = PermitType.PLATFORM,operationName = "修改用户角色")
	@RequestMapping(value = "/editUser")
	public ResponseInfo editUser(@RequestBody SystemUserDto systemUserDto) {
		if(systemUserDto.getId() == null){
			throw new GlobalException(SystemExceptionEnum.USER_ID_IS_NULL);
		}
		if(systemUserDto.getRoleId() == null){
			throw new GlobalException(SystemExceptionEnum.ROLE_NAME_IS_NULL);
		}
		systemUserService.editUser(systemUserDto);
		return new ResponseInfo();
	}

	@PermissionController(value = PermitType.PLATFORM,operationName = "删除用户")
	@RequestMapping(value = "/removeUser")
	public ResponseInfo removeUser(@RequestBody List<Long> ids) {
		if(CollectionUtils.isEmpty(ids)){
			throw new GlobalException(SystemExceptionEnum.USER_ID_IS_NULL);
		}
		systemUserService.removeUser(ids);
		return new ResponseInfo();
	}

	@PermissionController(value = PermitType.PLATFORM, operationName = "查询权限树")
	@RequestMapping(path = "/getResource")
	public ResponseInfo getResource() {
		return new ResponseInfo(systemUserService.getResource());
	}

	//测试初始化权限
//	@RequestMapping(path = "/test")
//	@NotLoginPermit
//	public ResponseInfo test() {
//		systemUserService.test();
//		return new ResponseInfo();
//	}


}
