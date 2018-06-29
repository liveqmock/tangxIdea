package com.topaiebiz.system.security.dao;


import com.topaiebiz.system.security.dto.SystemUserDto;
import org.apache.ibatis.annotations.Mapper;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.system.security.dto.SecurityMemberDto;
import com.topaiebiz.system.security.entity.SystemUserEntity;

/**
 * 描述：系统权限用户持久层接口。
 * 
 * @author Created by Amir Wang on 2017年10月30日。
 * 
 * @since 1.1.2
 */
@Mapper
public interface SystemUserDao extends BaseDao<SystemUserEntity> {

}
