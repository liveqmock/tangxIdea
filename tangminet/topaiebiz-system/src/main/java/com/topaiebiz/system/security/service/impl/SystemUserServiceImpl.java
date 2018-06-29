package com.topaiebiz.system.security.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.MD5Util;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.api.MemberApi;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.security.dao.*;
import com.topaiebiz.system.security.dto.SecurityResourceDto;
import com.topaiebiz.system.security.dto.SecurityUserDto;
import com.topaiebiz.system.security.dto.SystemUserDto;
import com.topaiebiz.system.security.entity.SystemResourceEntity;
import com.topaiebiz.system.security.entity.SystemRoleResourceEntity;
import com.topaiebiz.system.security.entity.SystemUserEntity;
import com.topaiebiz.system.security.entity.SystemUserRoleEntity;
import com.topaiebiz.system.security.exception.SystemExceptionEnum;
import com.topaiebiz.system.security.service.SystemUserService;
import com.topaiebiz.system.security.util.LoginTypeUtil;
import com.topaiebiz.system.security.util.SystemRoleUtil;
import com.topaiebiz.system.security.util.SystemUserCacheKey;
import com.topaiebiz.system.util.SecurityContextUtils;
import com.topaiebiz.system.util.SystemUserType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description： 系统权限用户表业务层
 * <p>
 * Author Aaron.Xue
 * <p>
 * Date 2017年10月28日 下午8:11:51
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
@Transactional
@Slf4j
public class SystemUserServiceImpl implements SystemUserService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private MemberApi memberApi;

    @Autowired
    private SystemUserDao systemUserDao;

    @Autowired
    private SystemRoleDao systemRoleDao;

    @Autowired
    private SystemResourceDao systemResourceDao;

    @Autowired
    private SystemUserRoleDao systemUserRoleDao;

    @Autowired
    private SystemRoleResourceDao systemRoleResourceDao;

    @Override
    public SecurityUserDto login(SystemUserDto systemUserDto) {
        SystemUserEntity systemUserEntity = null;
        //入驻
        if (LoginTypeUtil.ENTER_LOGIN == systemUserDto.getType()) {
            systemUserEntity = this.loginEnter(systemUserDto);
            return this.addCache(systemUserEntity);
        }
        systemUserEntity = this.getSystemUser(systemUserDto);
        /**用户不存在。*/
        if (null == systemUserEntity) {
            throw new GlobalException(SystemExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        } else {
            return this.addCache(systemUserEntity);
        }
    }

    //查询用户
    private SystemUserEntity getSystemUser(SystemUserDto systemUserDto) {
        /**
         * 1.根据用户名查询
         * 2.如果不为空，判断密码是否正确。正确返回，错误向下
         * 3.根据手机号查询
         * 4.如果不为空判断密码是否正确，正确返回，错误返回null
         */
        //传回来的密码加密
        String password = MD5Util.encode(systemUserDto.getPassword());
        SystemUserEntity systemUserEntity = null;
        //根据用户名查询
        EntityWrapper<SystemUserEntity> condUsername = new EntityWrapper<>();
        condUsername.eq("username", systemUserDto.getUsername());
        condUsername.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condUsername.eq("lockedFlag", 0);
        if (LoginTypeUtil.ENTER_LOGIN != systemUserDto.getType()) {
            condUsername.eq("type", systemUserDto.getType());
        }
        List<SystemUserEntity> systemUserEntities = systemUserDao.selectList(condUsername);
        if (CollectionUtils.isNotEmpty(systemUserEntities)) {
            systemUserEntity = systemUserEntities.get(0);
            if (password.equals(systemUserEntity.getPassword())) {
                return systemUserEntity;
            }
        }
        //根据手机号查询
        EntityWrapper<SystemUserEntity> condPhone = new EntityWrapper<>();
        condPhone.eq("mobilePhone", systemUserDto.getUsername());
        condPhone.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condPhone.eq("lockedFlag", 0);
        if (LoginTypeUtil.ENTER_LOGIN != systemUserDto.getType()) {
            condPhone.eq("type", systemUserDto.getType());
        }
        systemUserEntities = systemUserDao.selectList(condPhone);
        if (CollectionUtils.isNotEmpty(systemUserEntities)) {
            systemUserEntity = systemUserEntities.get(0);
            if (password.equals(systemUserEntity.getPassword())) {
                return systemUserEntity;
            }
        }
        return null;
    }

    //查询用户权限及放到redis
    private SecurityUserDto addCache(SystemUserEntity systemUserEntity) {
        //存放权限的DTO
        Set<SecurityResourceDto> resoucesSet = new HashSet<SecurityResourceDto>();
        //查询所有角色
        EntityWrapper<SystemUserRoleEntity> userRoleCondition = new EntityWrapper<SystemUserRoleEntity>();
        userRoleCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        userRoleCondition.eq("userId", systemUserEntity.getId());
        List<SystemUserRoleEntity> systemUserRoleEntities = systemUserRoleDao.selectList(userRoleCondition);
        List<Long> roles = systemUserRoleEntities.stream().map(userRole -> userRole.getRoleId()).collect(Collectors.toList());

        //查询所有权限
        EntityWrapper<SystemRoleResourceEntity> roleResourceCondition = new EntityWrapper<SystemRoleResourceEntity>();
        roleResourceCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        roleResourceCondition.in("roleId", roles);
        if(CollectionUtils.isEmpty(roles)) roleResourceCondition.eq("1", "2");

        List<SystemRoleResourceEntity> systemRoleResourceEntities = systemRoleResourceDao.selectList(roleResourceCondition);
        List<Long> resourceIds = systemRoleResourceEntities.stream().map(roleResource -> roleResource.getResourceId()).collect(Collectors.toList());

        //查询所有资源
        EntityWrapper<SystemResourceEntity> resourceCondition = new EntityWrapper<>();
        resourceCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        resourceCondition.in("id", resourceIds);
        if(CollectionUtils.isEmpty(resourceIds)) resourceCondition.eq("1", "2");
        List<SystemResourceEntity> systemResourceEntities = systemResourceDao.selectList(resourceCondition);

        for (SystemResourceEntity entity : systemResourceEntities) {
            SecurityResourceDto dto = new SecurityResourceDto();
            BeanCopyUtil.copy(entity, dto);
            resoucesSet.add(dto);
        }

        //所有访问的url
        List<String> urls = this.parseUrl(resoucesSet);
        //格式化资源树
        this.formatResource(resoucesSet);

        //生成唯一标识
        String uuid = MD5Util.encode(UUID.randomUUID().toString() + UUID.randomUUID().toString());

        CurrentUserDto currentUser = new CurrentUserDto();
        currentUser.setId(systemUserEntity.getId());
        BeanCopyUtil.copy(systemUserEntity, currentUser);

        SecurityUserDto securityUserDto = new SecurityUserDto();
        BeanCopyUtil.copy(systemUserEntity, securityUserDto);
        securityUserDto.setUserLoginId(uuid);
        securityUserDto.setResoucesSet(resoucesSet);

        redisCache.set(SystemUserCacheKey.LOGIN_USER_INFO_PREFIX + uuid, currentUser);
        redisCache.set(SystemUserCacheKey.LOGIN_USER_RESOURCE_PREFIX + uuid, urls);

        return securityUserDto;
    }

    //入住登录
    private SystemUserEntity loginEnter(SystemUserDto systemUserDto) {
        //查询会员表
        MemberDto memberDto = memberApi.getValidatedMember(systemUserDto.getUsername(), systemUserDto.getPassword());
        if (null == memberDto) {
            throw new GlobalException(SystemExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        //查询用户
        SystemUserEntity systemUserEntity = this.getSystemUser(systemUserDto);
        if (systemUserEntity == null) {
            systemUserEntity = new SystemUserEntity();
            systemUserEntity.setUsername(memberDto.getUserName());
            systemUserEntity.setPassword(memberDto.getPassword());
            systemUserEntity.setType(SystemUserType.ENTER);
            systemUserEntity.setMobilePhone(memberDto.getTelephone());
            systemUserEntity.setCreatedTime(new Date());
            //放到用户表
            systemUserDao.insert(systemUserEntity);
            //配置入住角色
            Long userId = systemUserEntity.getId();
            SystemUserRoleEntity userRoleEntity = new SystemUserRoleEntity();
            userRoleEntity.setUserId(userId);
            userRoleEntity.setRoleId(SystemRoleUtil.ENTER_ROLE);
            systemUserRoleDao.insert(userRoleEntity);
            return systemUserEntity;
        } else if (systemUserEntity.getType() == LoginTypeUtil.ENTER_LOGIN) {
            return systemUserEntity;
        } else if (systemUserEntity.getType() == LoginTypeUtil.MERCHANT_LOGIN) {
            throw new GlobalException(SystemExceptionEnum.USER_TYPE_CORRESPONDENCE);
        } else {
            throw new GlobalException(SystemExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }
    }

    @Override
    public void logout(String userLoginId) {
        if (null != userLoginId && !"".equals(userLoginId)) {
            redisCache.delete(SystemUserCacheKey.LOGIN_USER_INFO_PREFIX + userLoginId);
            redisCache.delete(SystemUserCacheKey.LOGIN_USER_RESOURCE_PREFIX + userLoginId);
        }
    }

    @Override
    public PageInfo<SystemUserDto> getList(SystemUserDto systemUserDto) {
        Page<SystemUserDto> page = PageDataUtil.buildPageParam(systemUserDto);
        //查询条件
        EntityWrapper<SystemUserEntity> userCondition = this.getUserCond(systemUserDto);
        List<SystemUserEntity> userEntities = systemUserDao.selectPage(page, userCondition);
        if (CollectionUtils.isEmpty(userEntities)) {
            return PageDataUtil.copyPageInfo(page);
        }
        List<SystemUserDto> userDtos = new ArrayList<>();
        for (SystemUserEntity userEntity : userEntities) {
            SystemUserDto systemUserDto1 = new SystemUserDto();
            BeanCopyUtil.copy(userEntity, systemUserDto1);

            EntityWrapper<SystemUserRoleEntity> condition = new EntityWrapper<>();
            condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            condition.eq("userId", systemUserDto1.getId());
            List<SystemUserRoleEntity> systemUserRoleEntities = systemUserRoleDao.selectList(condition);
            if (!CollectionUtils.isEmpty(systemUserRoleEntities)) {
                systemUserDto1.setRoleId(systemUserRoleEntities.get(0).getRoleId());
            }

            userDtos.add(systemUserDto1);
        }
        page.setRecords(userDtos);
        return PageDataUtil.copyPageInfo(page);
    }

    private EntityWrapper<SystemUserEntity> getUserCond(SystemUserDto systemUserDto) {
        EntityWrapper<SystemUserEntity> userCondition = new EntityWrapper<SystemUserEntity>();
        //查询类型
        Integer type = SecurityContextUtils.getCurrentUserDto().getType();
        if (SystemUserType.PLATFORM.equals(type)) { //平台
            userCondition.ne("inbuiltFlag", SystemUserType.MERCHANT); //商家创建的人员
        }
        if (SystemUserType.MERCHANT.equals(type)) {
            userCondition.eq("merchantId", SecurityContextUtils.getCurrentUserDto().getMerchantId());
        }
        if (null != systemUserDto.getUsername() && !"".equals(systemUserDto.getUsername())) {
            userCondition.like("username", systemUserDto.getUsername());
        }
        if (null != systemUserDto.getMobilePhone() && !"".equals(systemUserDto.getMobilePhone())) {
            userCondition.eq("mobilePhone", systemUserDto.getMobilePhone());
        }
        if (null != SecurityContextUtils.getCurrentUserDto().getMerchantId()) {
            userCondition.eq("merchantId", SecurityContextUtils.getCurrentUserDto().getMerchantId());
        }
        userCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        return userCondition;
    }

    @Override
    public MemberDto getMember(String mobilePhone) {
        MemberDto memberInfoByPhone = memberApi.getMemberInfoByPhone(mobilePhone);
        return memberInfoByPhone;
    }

    @Override
    public boolean editUserMerchantId(Long id, Long merchantId) {
        SystemUserEntity userEntity = systemUserDao.selectById(id);
        userEntity.setMerchantId(merchantId);
        userEntity.setLastModifiedTime(new Date());
        systemUserDao.updateById(userEntity);
        return true;
    }

    @Override
    public void saveUser(SystemUserDto systemUserDto) {

        //类型
        List<Integer> typeList = new ArrayList<>();
        //添加商家用户
        if (SystemUserType.MERCHANT.equals(SecurityContextUtils.getCurrentUserDto().getType())) {
            typeList.add(SystemUserType.MERCHANT);
            typeList.add(SystemUserType.ENTER);
        } else {
            typeList.add(SystemUserType.PLATFORM);
        }

        EntityWrapper<SystemUserEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("mobilePhone", systemUserDto.getMobilePhone());
        cond.in("type", typeList);
        List<SystemUserEntity> systemUserEntities = systemUserDao.selectList(cond);

        if (CollectionUtils.isNotEmpty(systemUserEntities)) {
            throw new GlobalException(SystemExceptionEnum.USER_ALREADY_EXISTS);
        }

        SystemUserEntity userEntity = new SystemUserEntity();
        BeanUtils.copyProperties(systemUserDto, userEntity);
        userEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        userEntity.setCreatedTime(new Date());
        userEntity.setMerchantId(SecurityContextUtils.getCurrentUserDto().getMerchantId());
        userEntity.setStoreId(SecurityContextUtils.getCurrentUserDto().getStoreId());
        userEntity.setType(SecurityContextUtils.getCurrentUserDto().getType());
        userEntity.setInbuiltFlag(SystemUserType.MERCHANT.equals(SecurityContextUtils.getCurrentUserDto().getType()) ? new Byte("3") : new Byte("2"));
        systemUserDao.insert(userEntity);
        //保存角色
        SystemUserRoleEntity userRoleEntity = new SystemUserRoleEntity();
        userRoleEntity.setUserId(userEntity.getId());
        userRoleEntity.setRoleId(systemUserDto.getRoleId());
        systemUserRoleDao.insert(userRoleEntity);

    }

    @Override
    public void editUser(SystemUserDto systemUserDto) {
        SystemUserRoleEntity userRoleEntity = new SystemUserRoleEntity();
        userRoleEntity.cleanInit();
        userRoleEntity.setUserId(systemUserDto.getId());
        userRoleEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        SystemUserRoleEntity userRole = systemUserRoleDao.selectOne(userRoleEntity);
        if (userRole == null) {
            userRole = new SystemUserRoleEntity();
            userRole.setUserId(systemUserDto.getId());
            userRole.setRoleId(systemUserDto.getRoleId());
            systemUserRoleDao.insert(userRole);
        } else {
            //保存角色
            userRole.setRoleId(systemUserDto.getRoleId());
            systemUserRoleDao.updateById(userRole);
        }
    }

    @Override
    public void removeUser(List<Long> ids) {

        EntityWrapper<SystemUserEntity> condition = new EntityWrapper<>();
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.in("id", ids);

        SystemUserEntity update = new SystemUserEntity();
        update.cleanInit();
        update.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        update.setLastModifiedTime(new Date());
        update.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        systemUserDao.update(update, condition);


        EntityWrapper<SystemUserRoleEntity> roleCondition = new EntityWrapper<>();
        roleCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        roleCondition.in("userId", ids);

        SystemUserRoleEntity roleUpdate = new SystemUserRoleEntity();
        update.cleanInit();
        update.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        systemUserRoleDao.update(roleUpdate, roleCondition);
    }

    /**
     * Description： 所有权限的URL
     * <p>
     * Author Aaron.Xue
     *
     * @param resoucesSet
     * @return
     */
    private List<String> parseUrl(Set<SecurityResourceDto> resoucesSet) {
        List<String> urlList = new ArrayList<String>();
        for (SecurityResourceDto securityResourceDto : resoucesSet) {
            if (securityResourceDto.getAccessUrl() != null && !"".equals(securityResourceDto.getAccessUrl())) {
                String[] split = securityResourceDto.getAccessUrl().split(",");
                for (String s : split) {
                    urlList.add(s);
                }
            }
        }
        return urlList;
    }

    /**
     * Description： 将资源构建成资源树
     * <p>
     * Author Aaron.Xue
     *
     * @param resoucesSet
     * @return
     */
    private Set<SecurityResourceDto> formatResource(Set<SecurityResourceDto> resoucesSet) {
        //删除的list
        List<SecurityResourceDto> delList = new ArrayList<SecurityResourceDto>();
        for (SecurityResourceDto securityResourceDto : resoucesSet) {
            //子权限集合
            List<SecurityResourceDto> childList = new ArrayList<SecurityResourceDto>();
            //查询出所有子权限
            for (SecurityResourceDto resourceDto : resoucesSet) {
                if (resourceDto.getParentId().equals(securityResourceDto.getId())) {
                    childList.add(resourceDto);
                    delList.add(resourceDto);
                }
            }
            securityResourceDto.setChildList(childList);
        }
        resoucesSet.removeAll(delList);
        delList.clear();
        for (SecurityResourceDto securityResourceDto : resoucesSet) {
            if (securityResourceDto.getParentId() != 0) {
                delList.add(securityResourceDto);
            }
        }
        resoucesSet.removeAll(delList);
        return resoucesSet;
    }

    private boolean editUserStoreId(Long id, Long storeId) {
        SystemUserEntity userEntity = systemUserDao.selectById(id);
        userEntity.setStoreId(storeId);
        userEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        userEntity.setLastModifiedTime(new Date());
        systemUserDao.updateById(userEntity);
        return true;
    }

    @Override
    public void editUserType(Long merchantId, Long storeId) {
        EntityWrapper<SystemUserEntity> userCond = new EntityWrapper<>();
        userCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        userCond.eq("type", SystemUserType.ENTER);
        userCond.eq("merchantId", merchantId);
        List<SystemUserEntity> systemUserEntities = systemUserDao.selectList(userCond);
        if (CollectionUtils.isEmpty(systemUserEntities)) {
            throw new GlobalException(SystemExceptionEnum.USER_TYPE_ERROR);
        }

        SystemUserEntity userEntity = systemUserEntities.get(0);

        //修改登录类型
        userEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        userEntity.setLastModifiedTime(new Date());
        userEntity.setType(SystemUserType.MERCHANT);
        systemUserDao.updateById(userEntity);
        //给入驻成功的用户配置一个商家权限
        EntityWrapper<SystemUserRoleEntity> condition = new EntityWrapper<>();
        condition.eq("userId", userEntity.getId());
        //删除原有权限
        SystemUserRoleEntity entity = new SystemUserRoleEntity();
        entity.cleanInit();
        entity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        systemUserRoleDao.update(entity, condition);
        //新增商家权限
        SystemUserRoleEntity userRoleEntity = new SystemUserRoleEntity();
        userRoleEntity.setUserId(userEntity.getId());
        userRoleEntity.setRoleId(SystemRoleUtil.MERCHANT_ADMIN_ROLE);
        systemUserRoleDao.insert(userRoleEntity);

        //修改店铺信息
        this.editUserStoreId(userEntity.getId(), storeId);

    }

    @Override
    public boolean editUserPassword(String mobilePhone, String password) {
        EntityWrapper<SystemUserEntity> condition = new EntityWrapper<>();
        condition.eq("mobilePhone", mobilePhone);
        SystemUserEntity entity = new SystemUserEntity();
        entity.cleanInit();
        entity.setPassword(password);
        entity.setLastModifiedTime(new Date());
        systemUserDao.update(entity, condition);
        return true;
    }

    @Override
    public boolean editUserTelephone(String beforeMobilePhone, String afterMobilePhone) {
        EntityWrapper<SystemUserEntity> condition = new EntityWrapper<>();
        condition.eq("mobilePhone", beforeMobilePhone);
        SystemUserEntity entity = new SystemUserEntity();
        entity.cleanInit();
        entity.setMobilePhone(afterMobilePhone);
        entity.setLastModifiedTime(new Date());
        systemUserDao.update(entity, condition);
        return true;
    }

    @Override
    public boolean removeSystemUser(String mobilePhone) {
        EntityWrapper<SystemUserEntity> condition = new EntityWrapper<>();
        condition.eq("mobilePhone", mobilePhone);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

        SystemUserEntity entity = new SystemUserEntity();
        entity.cleanInit();
        entity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        entity.setLastModifiedTime(new Date());
        systemUserDao.update(entity, condition);
        return true;
    }

    @Override
    public CurrentUserDto getByStoreId(Long storeId) {
        if (null == storeId) {
            throw new GlobalException(SystemExceptionEnum.USER_IS_NULL);
        }
        EntityWrapper<SystemUserEntity> userCond = new EntityWrapper<>();
        userCond.eq("storeId", storeId);
        userCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

        List<SystemUserEntity> systemUserEntities = systemUserDao.selectList(userCond);
        if (CollectionUtils.isNotEmpty(systemUserEntities)) {
            CurrentUserDto dto = new CurrentUserDto();
            BeanCopyUtil.copy(systemUserEntities.get(0), dto);
            return dto;
        } else {
            throw new GlobalException(SystemExceptionEnum.USER_IS_NULL);
        }

    }

    @Override
    public boolean closeUser(String mobilePhone) {
        if (StringUtils.isBlank(mobilePhone)) {
            throw new GlobalException(SystemExceptionEnum.MOBILEPHONE_IS_NULL);
        }
        SystemUserEntity entity = new SystemUserEntity();
        entity.cleanInit();
        entity.setLockedFlag((byte) 1);

        EntityWrapper<SystemUserEntity> cond = new EntityWrapper<>();
        cond.eq("mobilePhone", mobilePhone);

        Integer update = systemUserDao.update(entity, cond);
        return update != 0;
    }

    @Override
    public boolean openUser(String mobilePhone) {
        if (StringUtils.isBlank(mobilePhone)) {
            throw new GlobalException(SystemExceptionEnum.MOBILEPHONE_IS_NULL);
        }
        SystemUserEntity entity = new SystemUserEntity();
        entity.cleanInit();
        entity.setLockedFlag((byte) 0);

        EntityWrapper<SystemUserEntity> cond = new EntityWrapper<>();
        cond.eq("mobilePhone", mobilePhone);

        Integer update = systemUserDao.update(entity, cond);
        return update != 0;
    }

    @Override
    public List<SecurityResourceDto> getResource() {
        //当前登录用户的角色
        Long userId = SecurityContextUtils.getCurrentUserDto().getId();
        EntityWrapper<SystemUserRoleEntity> cond = new EntityWrapper<>();
        cond.eq("userId", userId);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<SystemUserRoleEntity> systemUserRoleEntities = systemUserRoleDao.selectList(cond);
        List<Long> roleIds = systemUserRoleEntities.stream().map(entity -> entity.getRoleId()).collect(Collectors.toList());
        //根据角色查询出资源
        EntityWrapper<SystemRoleResourceEntity> roleResourceCond = new EntityWrapper<>();
        roleResourceCond.in("roleId", roleIds);
        roleResourceCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        if(CollectionUtils.isEmpty(roleIds)) roleResourceCond.eq("1", "2");
        List<SystemRoleResourceEntity> systemRoleResourceEntities = systemRoleResourceDao.selectList(roleResourceCond);

        List<Long> resourceIds = systemRoleResourceEntities.stream().map(systemRoleResource -> systemRoleResource.getResourceId()).collect(Collectors.toList());
        //查询出所有资源详情
        Integer type = SecurityContextUtils.getCurrentUserDto().getType();
        EntityWrapper<SystemResourceEntity> resourceCondition = new EntityWrapper<>();
        resourceCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        resourceCondition.in("id", resourceIds);
        resourceCondition.eq("resourceType", type);
        if(CollectionUtils.isEmpty(resourceIds)) resourceCondition.eq("1", "2");
        List<SystemResourceEntity> systemResourceEntities = systemResourceDao.selectList(resourceCondition);

        Set<SecurityResourceDto> dtoList = new HashSet<>();
        for (SystemResourceEntity entity : systemResourceEntities) {
            SecurityResourceDto dto = new SecurityResourceDto();
            BeanCopyUtil.copy(entity, dto);
            dtoList.add(dto);
        }
        this.formatResource(dtoList);
        return new ArrayList<SecurityResourceDto>(dtoList);
    }

    @Override
    public void test() {
        EntityWrapper<SystemResourceEntity> cond = new EntityWrapper<>();
        List<SystemResourceEntity> systemResourceEntities = systemResourceDao.selectList(cond);
        for(SystemResourceEntity resource : systemResourceEntities){
            if(resource.getId().toString().startsWith("2")){
                SystemRoleResourceEntity roleResourceEntity = new SystemRoleResourceEntity();
                roleResourceEntity.setRoleId(2001L);
                roleResourceEntity.setResourceId(resource.getId());
                systemRoleResourceDao.insert(roleResourceEntity);
            }else if(resource.getId().toString().startsWith("3")){
                SystemRoleResourceEntity roleResourceEntity = new SystemRoleResourceEntity();
                roleResourceEntity.setRoleId(3001L);
                roleResourceEntity.setResourceId(resource.getId());
                systemRoleResourceDao.insert(roleResourceEntity);
            }
        }

    }

}
