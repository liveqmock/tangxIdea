package com.topaiebiz.promotion.worldcup.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.promotion.worldcup.entity.WorldCupMemberInvestmentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorldCupMemberInvestmentDao extends BaseDao<WorldCupMemberInvestmentEntity> {


	List<WorldCupMemberInvestmentEntity> selectListByGroup(@Param("matchId") Long updateMatchId, @Param("start") Integer start, @Param("end") Integer end);
}