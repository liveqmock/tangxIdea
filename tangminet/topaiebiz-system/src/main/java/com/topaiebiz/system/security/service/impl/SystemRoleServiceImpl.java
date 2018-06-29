package com.topaiebiz.system.security.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.system.security.dao.SystemResourceDao;
import com.topaiebiz.system.security.dao.SystemRoleDao;
import com.topaiebiz.system.security.dao.SystemRoleResourceDao;
import com.topaiebiz.system.security.dto.SecurityResourceDto;
import com.topaiebiz.system.security.dto.SecurityRoleDto;
import com.topaiebiz.system.security.dto.SystemRoleDto;
import com.topaiebiz.system.security.entity.SystemResourceEntity;
import com.topaiebiz.system.security.entity.SystemRoleEntity;
import com.topaiebiz.system.security.entity.SystemRoleResourceEntity;
import com.topaiebiz.system.security.exception.SystemExceptionEnum;
import com.topaiebiz.system.security.service.SystemRoleService;
import com.topaiebiz.system.security.service.SystemUserService;
import com.topaiebiz.system.util.SecurityContextUtils;
import com.topaiebiz.system.util.SystemUserType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class SystemRoleServiceImpl implements SystemRoleService {

	@Autowired
	private SystemRoleDao systemRoleDao;

	@Autowired
	private SystemRoleResourceDao systemRoleResourceDao;

	@Autowired
	private SystemUserService systemUserService;

	@Autowired
	private SystemResourceDao systemResourceDao;

	@Override
	public PageInfo<SystemRoleDto> getList(SystemRoleDto systemRoleDto) {
		Page<SystemRoleDto> page = PageDataUtil.buildPageParam(systemRoleDto);
		EntityWrapper<SystemRoleEntity> roleCondition = this.getRoleCond(systemRoleDto);
		List<SystemRoleEntity> roleEntities = systemRoleDao.selectPage(page, roleCondition);
		if (CollectionUtils.isEmpty(roleEntities)) {
			return PageDataUtil.copyPageInfo(page);
		}
		List<SystemRoleDto> roleDtos = new ArrayList<>();
		for (SystemRoleEntity roleEntity : roleEntities){
			SystemRoleDto systemRoleDto1 = new SystemRoleDto();
			BeanCopyUtil.copy(roleEntity, systemRoleDto1);
			roleDtos.add(systemRoleDto1);
		}
		page.setRecords(roleDtos);
		return PageDataUtil.copyPageInfo(page);
	}

	private EntityWrapper<SystemRoleEntity> getRoleCond(SystemRoleDto systemRoleDto){
		EntityWrapper<SystemRoleEntity> roleCondition = new EntityWrapper<SystemRoleEntity>();
		//查询类型
		Integer type = SecurityContextUtils.getCurrentUserDto().getType();
		if(SystemUserType.PLATFORM.equals(type)){ //平台
			roleCondition.ne("inbuiltFlag", 3); //商家创建的人员
		}
		if(SystemUserType.MERCHANT.equals(type)){
			roleCondition.eq("merchantId", SecurityContextUtils.getCurrentUserDto().getMerchantId());
		}
		if( null != systemRoleDto.getName()  && !"".equals(systemRoleDto.getName())){
			roleCondition.like("name",systemRoleDto.getName());
		}
		roleCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
		return roleCondition;
	}

	@Override
	public void save(SystemRoleDto systemRoleDto) {
		SystemRoleEntity roleEntity = new SystemRoleEntity();
		BeanCopyUtil.copy(systemRoleDto, roleEntity);
		roleEntity.setInbuiltFlag((byte) 0);
		roleEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
		roleEntity.setRoleType(SecurityContextUtils.getCurrentUserDto().getType());
		roleEntity.setMerchantId(SecurityContextUtils.getCurrentUserDto().getMerchantId());
		roleEntity.setInbuiltFlag(SystemUserType.MERCHANT.equals(SecurityContextUtils.getCurrentUserDto().getType()) ? new Byte("3") : new Byte("2"));
		roleEntity.setCreatedTime(new Date());
		systemRoleDao.insert(roleEntity);

		List<Long> resourceIds = systemRoleDto.getResourceIds();
		for (Long resourceId : resourceIds){
			SystemRoleResourceEntity roleResource = new SystemRoleResourceEntity();
			roleResource.setRoleId(roleEntity.getId());
			roleResource.setResourceId(resourceId);
			systemRoleResourceDao.insert(roleResource);
		}
	}

	@Override
	public void edit(SystemRoleDto systemRoleDto) {
		SystemRoleEntity roleEntity = systemRoleDao.selectById(systemRoleDto.getId());
		BeanCopyUtil.copy(systemRoleDto, roleEntity);
		roleEntity.setLastModifiedTime(new Date());
		roleEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
		systemRoleDao.updateById(roleEntity);

		//删除原来角色
		EntityWrapper<SystemRoleResourceEntity> condition = new EntityWrapper<SystemRoleResourceEntity>();
		condition.eq("roleId", systemRoleDto.getId());
		systemRoleResourceDao.delete(condition);

		List<Long> resourceIds = systemRoleDto.getResourceIds();
		for (Long resourceId : resourceIds){
			SystemRoleResourceEntity roleResource = new SystemRoleResourceEntity();
			roleResource.setRoleId(roleEntity.getId());
			roleResource.setResourceId(resourceId);
			systemRoleResourceDao.insert(roleResource);
		}
	}

	@Override
	public List<SecurityRoleDto> getRole() {
		List<SecurityRoleDto> dtoList = new ArrayList<>();

		EntityWrapper<SystemRoleEntity> roleCondition = this.getRoleCond(new SystemRoleDto());


		List<SystemRoleEntity> systemRoleList = systemRoleDao.selectList(roleCondition);
		for(SystemRoleEntity entity : systemRoleList) {
			SecurityRoleDto dto = new SecurityRoleDto();
			BeanCopyUtil.copy(entity, dto);
			dtoList.add(dto);
		}
		return dtoList;
	}

	@Override
	public void remove(List<Long> ids) {
		List<SystemRoleEntity> systemRoleEntities = systemRoleDao.selectBatchIds(ids);
		for(SystemRoleEntity systemRole : systemRoleEntities){
			if(systemRole.getInbuiltFlag() == 1){
				throw new GlobalException(SystemExceptionEnum.INBUILT_ROLE_CANNOT_BE_DELETED);
			}
		}
		EntityWrapper<SystemRoleEntity> roleCondition = new EntityWrapper<>();
		roleCondition.in("id",ids);

		SystemRoleEntity entity = new SystemRoleEntity();
		entity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
		entity.setLastModifiedTime(new Date());
		entity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
		systemRoleDao.update(entity, roleCondition);

		//删除角色对应的权限
		EntityWrapper<SystemRoleResourceEntity> roleResourceCondition = new EntityWrapper<>();
		roleResourceCondition.in("roleId",ids);
		roleResourceCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

		SystemRoleResourceEntity resourceEntity = new SystemRoleResourceEntity();
		resourceEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
		systemRoleResourceDao.update(resourceEntity, roleResourceCondition);

	}

	@Override
	public SystemRoleDto getRoleDetail(Long id) {
		SystemRoleEntity roleEntity = systemRoleDao.selectById(id);

		SystemRoleDto roleDto = new SystemRoleDto();
		BeanCopyUtil.copy(roleEntity, roleDto);

		EntityWrapper<SystemRoleResourceEntity> condition = new EntityWrapper<>();
		condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
		condition.eq("roleId", id);
		List<SystemRoleResourceEntity> systemRoleResourceEntities = systemRoleResourceDao.selectList(condition);

		List<Long> ids = systemRoleResourceEntities.stream().map(entity -> entity.getResourceId()).collect(Collectors.toList());

		//拥有的资源
		EntityWrapper<SystemResourceEntity> resourceCondition = new EntityWrapper<>();
		resourceCondition.in("id", ids);
		resourceCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        if(CollectionUtils.isEmpty(ids)) resourceCondition.eq("1", "2");
		List<SystemResourceEntity> systemResourceEntities = systemResourceDao.selectList(resourceCondition);

		//所有资源树
		List<SecurityResourceDto> resource = systemUserService.getResource();
		this.signCheck(resource, systemResourceEntities);

		roleDto.setResources(resource);
		return roleDto;
	}

	//标记是否拥有
	private void signCheck(List<SecurityResourceDto> resource, List<SystemResourceEntity> systemResourceEntities) {
		if (CollectionUtils.isEmpty(resource)) {
			return ;
		}else{
			for (SecurityResourceDto dto : resource) {
				for (SystemResourceEntity entity : systemResourceEntities) {
					if (dto.getId().equals(entity.getId()) && CollectionUtils.isEmpty(dto.getChildList())) {
						dto.setIsCheck(1);
						continue ;
					}
				}
				List<SecurityResourceDto> childList = dto.getChildList();
				this.signCheck(childList, systemResourceEntities);
			}
		}
	}


}