package com.topaiebiz.member.identity.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.member.identity.entity.MemberIdentityAuditEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by admin on 2018/5/31.
 */
@Mapper
public interface MemberIdentityAuditDao extends BaseDao<MemberIdentityAuditEntity> {
}
