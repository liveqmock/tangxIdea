package com.topaiebiz.member.point.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.member.point.entity.MemberAssetEntity;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ward on 2018-01-18.
 */
@Mapper
public interface MemberAssetDao extends BaseDao<MemberAssetEntity> {

    Integer updateOnlyAsset(@Param("pointChange") Integer pointChange, @Param("balanceChange") BigDecimal balanceChange, @Param("memberId") Long memberId, @Param("nowTime") Date nowTime);

}

