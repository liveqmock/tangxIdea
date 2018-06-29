package com.topaiebiz.system.security.service;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.system.security.dto.SecurityRoleDto;
import com.topaiebiz.system.security.dto.SystemRoleDto;

import java.util.List;


public interface SystemRoleService {

	PageInfo<SystemRoleDto> getList(SystemRoleDto systemRoleDto);

	void save(SystemRoleDto systemRoleDto);

	void edit(SystemRoleDto systemRoleDto);

    List<SecurityRoleDto> getRole();

	void remove(List<Long> ids);

	SystemRoleDto getRoleDetail(Long id);
}
