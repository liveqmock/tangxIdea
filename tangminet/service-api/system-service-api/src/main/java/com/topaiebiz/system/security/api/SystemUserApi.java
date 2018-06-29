package com.topaiebiz.system.security.api;

import com.topaiebiz.system.dto.CurrentUserDto;

public interface SystemUserApi {

    //修改用户密码
    boolean editUserPassword(String mobilePhone, String password);

    //修改用户手机号
    boolean editUserTelephone(String beforeMobilePhone, String afterMobilePhone);

    //删除用户
    boolean removeSystemUser(String mobilePhone);

    //根据用户Id修改商家ID,同时更新登录信息
    boolean editUserMerchantId(Long merchantId, String userLoginId);

    //入驻成功以后 修改用户类型 增加
    boolean editUserType(Long merchantId, Long storeId);

    //根据店铺ID查询用户
    CurrentUserDto getByStoreId(Long storeId);

    //锁定账户
    boolean closeUser(String mobilePhone);

    //打开账户
    boolean openUser(String mobilePhone);

}
