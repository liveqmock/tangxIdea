package com.topaiebiz.promotion.worldcup.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.promotion.worldcup.entity.WorldCupAgainstEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface WorldCupAgainstDao extends BaseDao<WorldCupAgainstEntity>{

	/**
	 *@Author: tangx.w
	 *@Description: 获取当日比赛信息表
	 *@param  startTime
	 *@param  endTime
	 *@Date: 2018/5/29 13:28
	 */
	List<WorldCupAgainstEntity> getDayMatch(@Param("startTime") String startTime,@Param("endTime") String endTime);

}