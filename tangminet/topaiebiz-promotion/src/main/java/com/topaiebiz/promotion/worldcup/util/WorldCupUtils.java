package com.topaiebiz.promotion.worldcup.util;

import com.topaiebiz.promotion.mgmt.util.PromotionUtils;
import com.topaiebiz.promotion.worldcup.dto.WorldCupTeamAgainstDto;
import com.topaiebiz.promotion.worldcup.dto.WorldCupTeamAgainstResultDto;
import com.topaiebiz.promotion.worldcup.entity.WorldCupAgainstEntity;
import com.topaiebiz.promotion.worldcup.entity.WorldCupTeamEntity;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 11:05 2018/5/29
 * @Modified by:
 */
public class WorldCupUtils {

	/**
	 * @param now
	 * @Author: tangx.w
	 * @Description: 获取世界杯当日比赛开始时间
	 * @Date: 2018/5/29 13:21
	 */
	public static Date getDayStartTime(Date now) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, 0);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date noon = cal.getTime();
		if (now.getTime() >= noon.getTime()) {
			return noon;
		} else {
			cal.add(Calendar.DAY_OF_YEAR, -1);
			cal.set(Calendar.HOUR_OF_DAY, 12);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			return cal.getTime();
		}
	}

	/**
	 * @param startTime
	 * @Author: tangx.w
	 * @Description: 获取世界杯当日比赛结束时间
	 * @Date: 2018/5/29 13:21
	 */
	public static Date getDayEndTime(Date startTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		return cal.getTime();
	}


	/**
	 * @param teamMap
	 * @param id
	 * @param teamAgainst
	 * @Author: tangx.w
	 * @Description: 获取主队信息
	 * @Date: 2018/5/29 14:23
	 */
	public static WorldCupTeamAgainstDto getHomeTeamInfo(Map<Long, WorldCupTeamEntity> teamMap, Long id, WorldCupTeamAgainstDto teamAgainst) {
		WorldCupTeamEntity worldCupTeamEntity = teamMap.get(id);
		teamAgainst.setHomePlayer(worldCupTeamEntity.getName());
		teamAgainst.setHomeCountry(worldCupTeamEntity.getCountry());
		teamAgainst.setHomeNationalFlag(worldCupTeamEntity.getNationalFlag());
		teamAgainst.setHomePlayPhoto(worldCupTeamEntity.getPlayerPhoto());
		return teamAgainst;
	}

	/**
	 * @param teamMap
	 * @param id
	 * @param teamAgainst
	 * @Author: tangx.w
	 * @Description: 获取客队信息
	 * @Date: 2018/5/29 14:25
	 */
	public static WorldCupTeamAgainstDto getVisitingTeamInfo(Map<Long, WorldCupTeamEntity> teamMap, Long id, WorldCupTeamAgainstDto teamAgainst) {
		WorldCupTeamEntity worldCupTeamEntity = teamMap.get(id);
		teamAgainst.setVisitingPlayer(worldCupTeamEntity.getName());
		teamAgainst.setVisitingCountry(worldCupTeamEntity.getCountry());
		teamAgainst.setVisitingNationalFlag(worldCupTeamEntity.getNationalFlag());
		teamAgainst.setVisitingPlayPhoto(worldCupTeamEntity.getPlayerPhoto());
		return teamAgainst;
	}

	/**
	 * @param worldCupAgainstEntity
	 * @Author: tangx.w
	 * @Description: 获取投注入口是否关闭
	 * @Date: 2018/5/29 15:00
	 */
	public static boolean isEndInvestment(WorldCupAgainstEntity worldCupAgainstEntity) {
		Date date = new Date();
		Date time = worldCupAgainstEntity.getCompetitionTime();
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.add(Calendar.MINUTE, -30);
		Date endInvestment = cal.getTime();
		if (date.getTime() > endInvestment.getTime()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @param teamAgainstResultDto
	 * @param teamMap
	 * @param homeId
	 * @Author: tangx.w
	 * @Description: 获取比赛结果的主队信息
	 * @Date: 2018/5/29 16:53
	 */
	public static WorldCupTeamAgainstResultDto getResultHomeTeamInfo(WorldCupTeamAgainstResultDto teamAgainstResultDto, Map<Long, WorldCupTeamEntity> teamMap, Long homeId) {
		WorldCupTeamEntity worldCupTeamEntity = teamMap.get(homeId);
		teamAgainstResultDto.setHomeCountry(worldCupTeamEntity.getCountry());
		teamAgainstResultDto.setHomePlayer(worldCupTeamEntity.getName());
		teamAgainstResultDto.setHomeNationalFlag(worldCupTeamEntity.getNationalFlag());
		teamAgainstResultDto.setHomePlayPhoto(worldCupTeamEntity.getPlayerPhoto());
		return teamAgainstResultDto;
	}



	/**
	 * @param teamAgainstResultDto
	 * @param teamMap
	 * @param homeId
	 * @Author: tangx.w
	 * @Description: 获取比赛结果的客队信息
	 * @Date: 2018/5/29 16:53
	 */
	public static WorldCupTeamAgainstResultDto getResultVisitingTeamInfo(WorldCupTeamAgainstResultDto teamAgainstResultDto, Map<Long, WorldCupTeamEntity> teamMap, Long homeId) {
		WorldCupTeamEntity worldCupTeamEntity = teamMap.get(homeId);
		teamAgainstResultDto.setVisitingCountry(worldCupTeamEntity.getCountry());
		teamAgainstResultDto.setVisitingPlayer(worldCupTeamEntity.getName());
		teamAgainstResultDto.setVisitingNationalFlag(worldCupTeamEntity.getNationalFlag());
		teamAgainstResultDto.setVisitingPlayPhoto(worldCupTeamEntity.getPlayerPhoto());
		return teamAgainstResultDto;
	}


}


