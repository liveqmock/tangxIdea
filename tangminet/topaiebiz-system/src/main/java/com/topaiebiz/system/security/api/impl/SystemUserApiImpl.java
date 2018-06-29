package com.topaiebiz.system.security.api.impl;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.security.api.SystemUserApi;
import com.topaiebiz.system.security.service.SystemUserService;
import com.topaiebiz.system.security.util.SystemUserCacheKey;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemUserApiImpl implements SystemUserApi {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SystemUserService systemUserService;

    @Override
    public boolean editUserPassword(String mobilePhone, String password) {
        return systemUserService.editUserPassword(mobilePhone, password);
    }

    @Override
    public boolean editUserTelephone(String beforeMobilePhone, String afterMobilePhone) {
        return systemUserService.editUserTelephone(beforeMobilePhone, afterMobilePhone);
    }

    @Override
    public boolean removeSystemUser(String mobilePhone) {
        return systemUserService.removeSystemUser(mobilePhone);
    }

    @Override
    //根据用户iD,修改商家ID
    public boolean editUserMerchantId(Long merchantId, String userLoginId) {
        String userDtoJson = redisCache.get(SystemUserCacheKey.LOGIN_USER_INFO_PREFIX + userLoginId);
        CurrentUserDto userDto = JSON.parseObject(userDtoJson, CurrentUserDto.class);
        systemUserService.editUserMerchantId(userDto.getId(), merchantId);
        userDto.setMerchantId(merchantId);
        redisCache.set(SystemUserCacheKey.LOGIN_USER_INFO_PREFIX + userLoginId, userDto);
        SecurityContextUtils.setCurrentUserDto(userDto);
        return true;
    }

    @Override
    public boolean editUserType(Long merchantId, Long storeId) {
        systemUserService.editUserType(merchantId, storeId);
        return true;
    }

    @Override
    public CurrentUserDto getByStoreId(Long storeId) {
        return systemUserService.getByStoreId(storeId);
    }

    @Override
    public boolean closeUser(String mobilePhone) {
        return systemUserService.closeUser(mobilePhone);
    }

    @Override
    public boolean openUser(String mobilePhone) {
        return systemUserService.openUser(mobilePhone);
    }


}
