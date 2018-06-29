package com.topaiebiz.member.identity.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.member.identity.dto.MemberIdentityDto;
import com.topaiebiz.member.identity.entity.MemberIdentityEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by admin on 2018/5/31.
 */
@Mapper
public interface MemberIdentityDao extends BaseDao<MemberIdentityEntity> {


    List<MemberIdentityDto> selectMemberIdentityList(Page<MemberIdentityDto> page,MemberIdentityDto memberIdentityDto);
}
