package com.topaiebiz.promotion.worldcup.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.base.Stopwatch;
import com.topaiebiz.member.api.PointApi;
import com.topaiebiz.member.constants.PointOperateType;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.dto.point.MemberAssetDto;
import com.topaiebiz.member.dto.point.PointChangeDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.promotion.constants.PromotionConstants;
import com.topaiebiz.promotion.mgmt.util.PromotionUtils;
import com.topaiebiz.promotion.worldcup.dao.WorldCupAgainstDao;
import com.topaiebiz.promotion.worldcup.dao.WorldCupMemberInvestmentDao;
import com.topaiebiz.promotion.worldcup.dao.WorldCupTeamDao;
import com.topaiebiz.promotion.worldcup.dto.*;
import com.topaiebiz.promotion.worldcup.entity.WorldCupAgainstEntity;
import com.topaiebiz.promotion.worldcup.entity.WorldCupMemberInvestmentEntity;
import com.topaiebiz.promotion.worldcup.entity.WorldCupTeamEntity;
import com.topaiebiz.promotion.worldcup.exception.WorldCupRuntimeException;
import com.topaiebiz.promotion.worldcup.service.WorldCupService;
import com.topaiebiz.promotion.worldcup.util.WorldCupUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 20:19 2018/5/28
 * @Modified by:
 */
@Slf4j
@Service
public class WorldCupServiceImpl implements WorldCupService {

	@Autowired
	private WorldCupAgainstDao worldCupAgainstDao;

	@Autowired
	private WorldCupTeamDao worldCupTeamDao;

	@Autowired
	private WorldCupMemberInvestmentDao worldCupMemberInvestmentDao;

	@Autowired
	private PointApi pointApi;

	private Integer num = 500;
	Integer result = 0;
	private Long updateMatchId = null;
	BigDecimal odds = null;


	/**
	 * @param * @param null
	 * @Author: tangx.w
	 * @Description: 获取世界活动首页
	 * @Date: 2018/5/29 13:23
	 */
	@Override
	public WorldCupPageDto getWorldCupHomePage() throws ParseException {
		WorldCupPageDto worldCupPageDto = new WorldCupPageDto();
		/** 获取会员id **/
		Long memberId = MemberContext.tryGetMemberId();
		Date now = new Date();
		String startTime = PromotionUtils.formatTime(WorldCupUtils.getDayStartTime(now));
		String endTime = PromotionUtils.formatTime(WorldCupUtils.getDayEndTime(WorldCupUtils.getDayStartTime(now)));

		List<WorldCupAgainstEntity> worldCupAgainstList = worldCupAgainstDao.getDayMatch(startTime, endTime);
		log.info("世界杯活动-获取当日比赛信息={}", worldCupAgainstList);
		/** 获取参赛球队信息 **/
		Map<Long, WorldCupTeamEntity> teamMap = getTeamInfo();
		Map<Long, WorldCupMemberInvestmentEntity> memberInvestmentMap = new HashMap<>();
		if (memberId != null) {
			/** 获取该会员所有投注信息 **/
			memberInvestmentMap = getMemberInvestmentInfo(memberId);
			/** 获取该会员的投注 结果信息 **/
			List<WorldCupMemberInvestmentEntity> investmentResultList = getMemberInvestmentResult(memberId);
			/** 获取所有比赛赛程对应map **/
			Map<Long, WorldCupAgainstEntity> allAgainstMap = getAllAgainstMap();
			/** 打包投注结果 **/
			List<WorldCupTeamAgainstResultDto> teamAgainstResult = packageTeamResultList(investmentResultList, allAgainstMap, teamMap);
			worldCupPageDto.setTeamAgainstResultList(teamAgainstResult);
		} else {
			List<WorldCupTeamAgainstResultDto> teamAgainstResult = new ArrayList<>();
			worldCupPageDto.setTeamAgainstResultList(teamAgainstResult);
		}
		/** 打包当日球队对战信息 **/
		List<WorldCupTeamAgainstDto> teamAgainst = packageTeamList(worldCupAgainstList, teamMap, memberInvestmentMap);
		worldCupPageDto.setTeamAgainstList(teamAgainst);


		return worldCupPageDto;
	}

	public Map<Long, WorldCupTeamEntity> getTeamInfo() {
		EntityWrapper<WorldCupTeamEntity> entityWrapper = new EntityWrapper();
		entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
		List<WorldCupTeamEntity> worldCupTeamList = worldCupTeamDao.selectList(entityWrapper);
		return worldCupTeamList.stream().collect(Collectors.toMap(WorldCupTeamEntity::getId, e -> e));
	}

	public Map<Long, WorldCupMemberInvestmentEntity> getMemberInvestmentInfo(Long memberId) {
		EntityWrapper<WorldCupMemberInvestmentEntity> entityWrapper = new EntityWrapper();
		entityWrapper.eq("memberId", memberId);
		entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
		entityWrapper.eq("investmentType", PromotionConstants.InvestmentType.WIN_AND_LOSE);
		List<WorldCupMemberInvestmentEntity> worldCupInvestmentList = worldCupMemberInvestmentDao.selectList(entityWrapper);
		return worldCupInvestmentList.stream().collect(Collectors.toMap(WorldCupMemberInvestmentEntity::getMatchId, e -> e));
	}

	public List<WorldCupTeamAgainstDto> packageTeamList(List<WorldCupAgainstEntity> worldCupAgainstList, Map<Long, WorldCupTeamEntity> teamMap, Map<Long, WorldCupMemberInvestmentEntity> memberInvestmentMap) throws ParseException {
		List<WorldCupTeamAgainstDto> teamAgainstList = new ArrayList<>();
		for (WorldCupAgainstEntity worldCupAgainstEntity : worldCupAgainstList) {
			WorldCupTeamAgainstDto teamAgainst = new WorldCupTeamAgainstDto();
			/** 获取主队信息 **/
			Long homeiId = worldCupAgainstEntity.getHomeId();
			teamAgainst = WorldCupUtils.getHomeTeamInfo(teamMap, homeiId, teamAgainst);
			/** 获取客队信息 **/
			Long visitingId = worldCupAgainstEntity.getVisitingId();
			teamAgainst = WorldCupUtils.getVisitingTeamInfo(teamMap, visitingId, teamAgainst);
			teamAgainst.setOdds(worldCupAgainstEntity.getOdds());
			Long matchId = worldCupAgainstEntity.getId();
			teamAgainst.setMatchId(matchId);
			teamAgainst.setCompetitionTime(worldCupAgainstEntity.getCompetitionTime());
			WorldCupMemberInvestmentEntity memberInvestmentEntity = memberInvestmentMap.get(matchId);
			teamAgainst.setIsOpen(WorldCupUtils.isEndInvestment(worldCupAgainstEntity));
			if (memberInvestmentEntity != null) {
				teamAgainst.setInvestmentPoints(memberInvestmentEntity.getInvestmentPoints());
				teamAgainst.setInvestmentProject(memberInvestmentEntity.getInvestmentProject());
			}
			teamAgainstList.add(teamAgainst);
		}
		return teamAgainstList;
	}

	public List<WorldCupMemberInvestmentEntity> getMemberInvestmentResult(Long memberId) {
		EntityWrapper<WorldCupMemberInvestmentEntity> entityWrapper = new EntityWrapper<>();
		entityWrapper.isNotNull("investmentResult");
		entityWrapper.eq("memberId", memberId);
		entityWrapper.eq("investmentType", PromotionConstants.InvestmentType.WIN_AND_LOSE);
		entityWrapper.orderBy("createdTime", false);
		entityWrapper.eq("deletedFlag",PromotionConstants.DeletedFlag.DELETED_NO);
		return worldCupMemberInvestmentDao.selectList(entityWrapper);
	}

	public Map<Long, WorldCupAgainstEntity> getAllAgainstMap() {
		EntityWrapper<WorldCupAgainstEntity> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
		List<WorldCupAgainstEntity> againstList = worldCupAgainstDao.selectList(entityWrapper);
		return againstList.stream().collect(Collectors.toMap(WorldCupAgainstEntity::getId, e -> e));
	}

	public List<WorldCupTeamAgainstResultDto> packageTeamResultList(List<WorldCupMemberInvestmentEntity> investmentResultList, Map<Long, WorldCupAgainstEntity> allAgainstMap, Map<Long, WorldCupTeamEntity> teamMap) {
		List<WorldCupTeamAgainstResultDto> teamAgainstResult = new ArrayList<>();
		for (WorldCupMemberInvestmentEntity investmentEntity : investmentResultList) {
			WorldCupTeamAgainstResultDto teamAgainstResultDto = new WorldCupTeamAgainstResultDto();
			Long matchId = investmentEntity.getMatchId();
			WorldCupAgainstEntity againstInfo = allAgainstMap.get(matchId);
			OddsDto oddsDto = JSONObject.parseObject(againstInfo.getOdds(), OddsDto.class);
			BigDecimal odds;
			if (PromotionConstants.WinoOrLose.WIN.equals(Integer.valueOf(investmentEntity.getInvestmentProject()))) {
				odds = oddsDto.getWin();
			} else if (PromotionConstants.WinoOrLose.DRAW.equals(Integer.valueOf(investmentEntity.getInvestmentProject()))) {
				odds = oddsDto.getDraw();
			} else {
				odds = oddsDto.getLose();
			}
			/** 获取投注结果主队信息**/
			Long homeId = againstInfo.getHomeId();
			teamAgainstResultDto = WorldCupUtils.getResultHomeTeamInfo(teamAgainstResultDto, teamMap, homeId);
			/** 获取投注结果客队信息**/
			Long visitingId = againstInfo.getVisitingId();
			teamAgainstResultDto = WorldCupUtils.getResultVisitingTeamInfo(teamAgainstResultDto, teamMap, visitingId);
			teamAgainstResultDto.setCompetitionTime(againstInfo.getCompetitionTime());
			teamAgainstResultDto.setAgainstResult(againstInfo.getResultGame());
			teamAgainstResultDto.setInvestmentResult(investmentEntity.getInvestmentResult());
			if (PromotionConstants.InvestmentResult.WIN.equals(investmentEntity.getInvestmentResult())) {
				teamAgainstResultDto.setInvestmentPoints((new BigDecimal(investmentEntity.getInvestmentPoints()).multiply(odds)).intValue());
			} else {
				teamAgainstResultDto.setInvestmentPoints(investmentEntity.getInvestmentPoints());
			}
			teamAgainstResult.add(teamAgainstResultDto);
		}
		return teamAgainstResult;
	}

	/**
	 * @param * @param null
	 * @Author: tangx.w
	 * @Description: 获取用户积分余额
	 * @Date: 2018/5/29 17:27
	 */
	@Override
	public Integer getMemberPoints() {
		/** 获取会员id **/
		Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
		MemberAssetDto memberAssetDto = pointApi.getMemberAsset(memberId);
		return memberAssetDto.getPoint();
	}

	/**
	 * @param investmentPointsDto
	 * @Author: tangx.w
	 * @Description: 投注
	 * @Date: 2018/5/29 17:49
	 */
	@Override
	public void winInvestmentPoints(InvestmentPointsDto investmentPointsDto, MemberTokenDto memberTokenDto) {
		Long memberId = memberTokenDto.getMemberId();
		PointChangeDto pointChangeDto = new PointChangeDto();
		pointChangeDto.setMemberId(memberId);
		String memo = "世界杯投注消耗" + investmentPointsDto.getInvestmentPoints();
		pointChangeDto.setMemo(memo);
		pointChangeDto.setOperateSn(investmentPointsDto.getMactchId().toString() + PromotionConstants.PointType.CONSUME);
		pointChangeDto.setOperateType(PointOperateType.ACTIVITY_REDUCE);
		pointChangeDto.setPoint(investmentPointsDto.getInvestmentPoints());
		pointChangeDto.setTelephone(memberTokenDto.getTelephone());
		pointChangeDto.setUserName(memberTokenDto.getUserName());
		pointApi.consumePoint(pointChangeDto);
		WorldCupMemberInvestmentEntity investmentEntity = new WorldCupMemberInvestmentEntity();
		investmentEntity.setInvestmentPoints(investmentPointsDto.getInvestmentPoints());
		investmentEntity.setInvestmentProject(investmentPointsDto.getInvestmentProject().toString());
		investmentEntity.setCreatedTime(new Date());
		investmentEntity.setInvestmentType(PromotionConstants.InvestmentType.WIN_AND_LOSE);
		investmentEntity.setMatchId(investmentPointsDto.getMactchId());
		investmentEntity.setCreatorId(memberId);
		investmentEntity.setMemberId(memberId);
		worldCupMemberInvestmentDao.insert(investmentEntity);
	}

	/**
	 * @param * @param null
	 * @Author: tangx.w
	 * @Description: 获取世界杯球队信息
	 * @Date: 2018/5/29 19:48
	 */
	@Override
	public TeamInfoDto getWorldCupTeamInfo(String teamEndTime) throws ParseException {
		Long memberId = MemberContext.tryGetMemberId();
		TeamInfoDto teamInfoDto = new TeamInfoDto();
		EntityWrapper<WorldCupTeamEntity> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
		teamInfoDto.setTeamList(worldCupTeamDao.selectList(entityWrapper));
		Date now = new Date();
		Date end = PromotionUtils.pareTime(teamEndTime);
		if (now.getTime() >= end.getTime()) {
			teamInfoDto.setIsOpen(false);
		} else {
			teamInfoDto.setIsOpen(true);
		}
		if (memberId != null) {
			WorldCupMemberInvestmentEntity investmentEntity = null;
			EntityWrapper<WorldCupMemberInvestmentEntity> entityEntityWrapper = new EntityWrapper<>();
			entityEntityWrapper.eq("memberId", memberId);
			entityEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
			entityEntityWrapper.eq("investmentType", PromotionConstants.InvestmentType.CHAMPION);
			List<WorldCupMemberInvestmentEntity> investmentEntityList = worldCupMemberInvestmentDao.selectList(entityEntityWrapper);
			if (investmentEntityList.size() != 0) {
				investmentEntity = investmentEntityList.get(0);
			}
			if (investmentEntity != null) {
				teamInfoDto.setInvestmentTeamId(investmentEntity.getInvestmentProject());
			}
		}
		return teamInfoDto;
	}

	/**
	 * @param * @param null
	 * @Author: tangx.w
	 * @Description: 夺冠球队押注
	 * @Date: 2018/5/29 20:16
	 */
	@Override
	public void investmentTeam(InvestmentTeamDto investmentTeamDto, MemberTokenDto memberTokenDto) {
		Long memberId = memberTokenDto.getMemberId();
		PointChangeDto pointChangeDto = new PointChangeDto();
		pointChangeDto.setMemberId(memberId);
		String memo = "世界杯竞猜冠军消耗" + investmentTeamDto.getInvestmentPoints();
		pointChangeDto.setMemo(memo);
		pointChangeDto.setOperateSn(investmentTeamDto.getTeamId().toString() + PromotionConstants.PointType.CONSUME);
		pointChangeDto.setOperateType(PointOperateType.ACTIVITY_REDUCE);
		pointChangeDto.setPoint(investmentTeamDto.getInvestmentPoints());
		pointChangeDto.setTelephone(memberTokenDto.getTelephone());
		pointChangeDto.setUserName(memberTokenDto.getUserName());
		pointApi.consumePoint(pointChangeDto);
		WorldCupMemberInvestmentEntity investmentEntity = new WorldCupMemberInvestmentEntity();
		investmentEntity.setInvestmentPoints(investmentTeamDto.getInvestmentPoints());
		investmentEntity.setInvestmentProject(investmentTeamDto.getTeamId().toString());
		investmentEntity.setCreatedTime(new Date());
		investmentEntity.setInvestmentType(PromotionConstants.InvestmentType.CHAMPION);
		investmentEntity.setCreatorId(memberId);
		investmentEntity.setMemberId(memberId);
		worldCupMemberInvestmentDao.insert(investmentEntity);
	}

	/**
	 * @param matchResultList
	 * @Author: tangx.w
	 * @Description: 上传比赛结果
	 * @Date: 2018/5/29 20:32
	 */
	@Override
	public void uploadResult(List<MatchResultDto> matchResultList) throws InterruptedException {
		List<WorldCupAgainstEntity> worldCupTeamEntityList = new ArrayList<>();
		for (MatchResultDto matchResultDto : matchResultList) {
			WorldCupAgainstEntity worldCupAgainstEntity = worldCupAgainstDao.selectById(Long.valueOf(matchResultDto.getMatchId()));
			ResultGameDto resultGameDto = new ResultGameDto();
			if (worldCupAgainstEntity == null) {
				throw new WorldCupRuntimeException("比赛场次不存在！");
			}
			WorldCupAgainstEntity teamAgainstEntity = new WorldCupAgainstEntity();
			teamAgainstEntity.setId(Long.valueOf(matchResultDto.getMatchId()));
			resultGameDto.setHomeTeamScored(Integer.valueOf(matchResultDto.getHomeTeamScored()));
			resultGameDto.setVisitingTeamScored(Integer.valueOf(matchResultDto.getVisitingTeamScored()));
			teamAgainstEntity.setResultGame(JSON.toJSONString(resultGameDto));
			teamAgainstEntity.setVersion(null);
			worldCupAgainstDao.updateById(teamAgainstEntity);
			worldCupTeamEntityList.add(teamAgainstEntity);
		}
		for (WorldCupAgainstEntity worldCupAgainstEntity : worldCupTeamEntityList) {
			/** 更新赛果到对战信息表 **/
			updateMatchId = worldCupAgainstEntity.getId();
			WorldCupAgainstEntity againstEntity = worldCupAgainstDao.selectById(worldCupAgainstEntity.getId());
			ResultGameDto resultGameDto = JSONObject.parseObject(worldCupAgainstEntity.getResultGame(), ResultGameDto.class);
			OddsDto oddsDto = JSONObject.parseObject(againstEntity.getOdds(), OddsDto.class);
			if (resultGameDto.getHomeTeamScored() > resultGameDto.getVisitingTeamScored()) {
				result = PromotionConstants.WinoOrLose.WIN;
				odds = oddsDto.getWin();
			} else if (resultGameDto.getHomeTeamScored().equals(resultGameDto.getVisitingTeamScored())) {
				result = PromotionConstants.WinoOrLose.DRAW;
				odds = oddsDto.getDraw();
			} else {
				result = PromotionConstants.WinoOrLose.LOSE;
				odds = oddsDto.getLose();
			}
			WorldCupResultDto worldCupResultDTO = new WorldCupResultDto();
			Integer groupNum = 1;
			try {
				do {
					worldCupResultDTO = updateResult(num, groupNum, worldCupResultDTO, result, updateMatchId);
					groupNum = groupNum + 1;
				} while (worldCupResultDTO.getResultSize() != 0);
			} catch (Exception ex) {
				log.error("更新赛果失败", ex);
			}
			Thread.sleep(1000);
		}

	}

	public WorldCupResultDto updateResult(Integer num, Integer groupNum, WorldCupResultDto worldCupResultDTO, Integer result, Long updateMatchId) {
		Integer start = (groupNum - 1) * num;
		Integer end = groupNum * num;
		Stopwatch methodStopWatch = Stopwatch.createStarted();
		List<WorldCupMemberInvestmentEntity> memberInvestmentList = worldCupMemberInvestmentDao.selectListByGroup(updateMatchId, start, end);
		if (CollectionUtils.isEmpty(memberInvestmentList)) {
			worldCupResultDTO.setResultSize(memberInvestmentList.size());
			return worldCupResultDTO;
		}
		CountDownLatch cdl = new CountDownLatch(memberInvestmentList.size());
		for (WorldCupMemberInvestmentEntity memberInvestmentEntity : memberInvestmentList) {
			threadPool.submit(new Run(memberInvestmentEntity, cdl, result));
		}
		try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		worldCupResultDTO.setResultSize(memberInvestmentList.size());
		log.info("更新{}条投注记录消耗{}ms", memberInvestmentList.size(), methodStopWatch.elapsed(TimeUnit.MILLISECONDS));
		return worldCupResultDTO;
	}

	private Integer coreThreadNum = 5;
	private Integer maxThreadNUM = 10;
	private Integer aliveTime = 60;
	private TimeUnit timeUnit = TimeUnit.SECONDS;
	private BlockingQueue queue = new LinkedBlockingQueue(1000);


	private ExecutorService threadPool = new ThreadPoolExecutor(coreThreadNum, maxThreadNUM, aliveTime, timeUnit, queue);


	@AllArgsConstructor
	class Run implements Runnable {
		WorldCupMemberInvestmentEntity memberInvestmentEntity;
		CountDownLatch cdl;
		Integer result;

		@Override
		public void run() {
			try {
				doUpdate(memberInvestmentEntity, result);
			} finally {
				cdl.countDown();
			}
		}
	}

	public void doUpdate(WorldCupMemberInvestmentEntity memberInvestmentEntity, Integer result) {
		if (result.equals(Integer.valueOf(memberInvestmentEntity.getInvestmentProject()))) {
			memberInvestmentEntity.setInvestmentResult(PromotionConstants.InvestmentResult.WIN);
			/** 增加该用户积分 **/
			PointChangeDto pointChangeDto = new PointChangeDto();
			pointChangeDto.setMemberId(memberInvestmentEntity.getMemberId());
			String memo = "世界杯竞猜成功奖励" + new BigDecimal(memberInvestmentEntity.getInvestmentPoints()).multiply(odds).toString();
			pointChangeDto.setMemo(memo);
			pointChangeDto.setOperateSn(memberInvestmentEntity.getMatchId().toString() + PromotionConstants.PointType.ADD);
			pointChangeDto.setOperateType(PointOperateType.ACTIVITY_PRIZE);
			pointChangeDto.setPoint((new BigDecimal(memberInvestmentEntity.getInvestmentPoints()).multiply(odds)).intValue());
			pointApi.addPoint(pointChangeDto);
		} else {
			memberInvestmentEntity.setInvestmentResult(PromotionConstants.InvestmentResult.LOSE);
		}
		worldCupMemberInvestmentDao.updateById(memberInvestmentEntity);
	}


	/**
	 * @param matchResult
	 * @Author: tangx.w
	 * @Description: 上传冠军
	 * @Date: 2018/5/29 20:32
	 */
	@Override
	public void uploadChampion(MatchResultDto matchResult) {
		EntityWrapper<WorldCupMemberInvestmentEntity> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("investmentType", PromotionConstants.InvestmentType.CHAMPION);
		entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
		/** 冠军球队 **/
		WorldCupTeamEntity worldCupTeamEntity = worldCupTeamDao.selectById(matchResult.getChampionId());
		worldCupTeamEntity.setState(PromotionConstants.TeamState.CHAMPION);
		worldCupTeamDao.updateById(worldCupTeamEntity);
		log.info("{}上传冠军{}", new Date(), matchResult.getChampionId());
		/** 其他球队置为淘汰 **/
		EntityWrapper<WorldCupTeamEntity> entityEntityWrapper = new EntityWrapper<>();
		entityEntityWrapper.eq("state", PromotionConstants.TeamState.NORMAL);
		entityEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
		List<WorldCupTeamEntity> teamEntityList = worldCupTeamDao.selectList(entityEntityWrapper);
		for (WorldCupTeamEntity teamEntity : teamEntityList) {
			teamEntity.setState(PromotionConstants.TeamState.ELIMINATE);
			worldCupTeamDao.updateById(teamEntity);
		}
		List<WorldCupMemberInvestmentEntity> investmentEntityList = worldCupMemberInvestmentDao.selectList(entityWrapper);
		for (WorldCupMemberInvestmentEntity investmentEntity : investmentEntityList) {
			if (matchResult.getChampionId().equals(investmentEntity.getInvestmentProject())) {
				investmentEntity.setInvestmentResult(PromotionConstants.InvestmentResult.WIN);
				/** 增加该用户积分 **/
				PointChangeDto pointChangeDto = new PointChangeDto();
				pointChangeDto.setMemberId(investmentEntity.getMemberId());
				String memo = "世界杯冠军竞猜成功奖励" + investmentEntity.getInvestmentPoints() * 2;
				pointChangeDto.setMemo(memo);
				pointChangeDto.setOperateSn(investmentEntity.getInvestmentProject().toString() + PromotionConstants.PointType.ADD);
				pointChangeDto.setOperateType(PointOperateType.ACTIVITY_PRIZE);
				pointChangeDto.setPoint(investmentEntity.getInvestmentPoints() * 2);
				pointApi.addPoint(pointChangeDto);
			} else {
				investmentEntity.setInvestmentResult(PromotionConstants.InvestmentResult.LOSE);
			}
			worldCupMemberInvestmentDao.updateById(investmentEntity);
		}
	}

}
