package com.topaiebiz.system.security.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.security.dto.SecurityResourceDto;
import com.topaiebiz.system.security.dto.SecurityUserDto;
import com.topaiebiz.system.security.dto.SystemUserDto;

import java.util.List;

/**
 * 描述：系统用户业务层接口。
 * 
 * @author Created by Amir Wang on 2017年10月30日。
 * 
 * @since 1.1.2
 */
public interface SystemUserService {

	/**
	 * 描述：登录的业务逻辑接口。
	 * 
	 * @param systemUserDto 系统用户信息数据传输对象。
	 * 
	 * @return 登陆后的用户的信息数据对象。
	 * 
	 * @throws GlobalException 当出现异常时，抛出统一的GlobalException全局异常。
	 * 
	 * @author Created by Amir Wang on 2017年10月30日。
	 */
	SecurityUserDto login(SystemUserDto systemUserDto);
	
	/**
	 * 注销登陆
	 * @param userLoginId
	 */
    void logout(String userLoginId);

	/**
	 * 用户分页查询
	 * @param systemUserDto
	 * @return
	 */
	PageInfo<SystemUserDto> getList(SystemUserDto systemUserDto);

	/**
	 * 根据手机号获取会员信息
	 * @param mobilePhone
	 * @return
	 */
	MemberDto getMember(String mobilePhone);

	/**
	 * 修改用户商家Id
	 * @param id
	 * @param merchantId
	 * @return
	 */
	boolean editUserMerchantId(Long id, Long merchantId);

	/**
	 * 保存用户
	 * @param systemUserDto
	 */
	void saveUser(SystemUserDto systemUserDto);

	/**
	 * 修改用户角色
	 * @param systemUserDto
	 */
	void editUser(SystemUserDto systemUserDto);

	/**
	 * 删除用户
	 * @param ids
	 */
	void removeUser(List<Long> ids);

	/**
	 * 获取资源树
	 */
	List<SecurityResourceDto> getResource();

	/**
	 * 入驻成功 修改用户类型
	 */
	void editUserType(Long merchantId, Long storeId);

	/**
	 * 根据手机号修改密码
	 * @param mobilePhone
	 * @param password
	 * @return
	 */
	boolean editUserPassword(String mobilePhone, String password);

	/**
	 * 修改手机号
	 * @param beforeMobilePhone
	 * @param afterMobilePhone
	 * @return
	 */
	boolean editUserTelephone(String beforeMobilePhone, String afterMobilePhone);

	/**
	 * 根据手机号删除用户
	 * @param mobilePhone
	 * @return
	 */
	boolean removeSystemUser(String mobilePhone);

	//根据店铺ID查询用户
    CurrentUserDto getByStoreId(Long storeId);

    //锁定用户
	boolean closeUser(String mobilePhone);

	//解锁用户
	boolean openUser(String mobilePhone);

    void test();
}
