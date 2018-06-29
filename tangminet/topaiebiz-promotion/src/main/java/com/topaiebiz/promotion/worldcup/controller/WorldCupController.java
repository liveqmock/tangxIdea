package com.topaiebiz.promotion.worldcup.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.promotion.mgmt.util.PromotionUtils;
import com.topaiebiz.promotion.worldcup.dto.InvestmentPointsDto;
import com.topaiebiz.promotion.worldcup.dto.InvestmentTeamDto;
import com.topaiebiz.promotion.worldcup.dto.MatchResultDto;
import com.topaiebiz.promotion.worldcup.exception.WorldCupExceptionEnum;
import com.topaiebiz.promotion.worldcup.service.WorldCupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 19:59 2018/5/28
 * @Modified by:
 */

@RestController
@RequestMapping(path = "/promotion/worldCup", method = RequestMethod.POST)
public class WorldCupController {

	@Autowired
	private WorldCupService worldCupService;

	private final String teamEndTime= "2018-06-29 12:00:00";

	/**
	 * @param
	 * @Author: tangx.w
	 * @Description: 获取世界杯首页
	 * @Date: 2018/5/16 13:32
	 */
	@MemberLogin
	@RequestMapping(path = "/getWorldCupHomePage")
	public ResponseInfo getWorldCupHomePage() throws ParseException {
		return new ResponseInfo(worldCupService.getWorldCupHomePage());
	}

	/**
	 * @param
	 * @Author: tangx.w
	 * @Description: 获取用户积分
	 * @Date: 2018/5/16 13:32
	 */
	@MemberLogin
	@RequestMapping(path = "/getMemberPoints")
	public ResponseInfo getMemberPoints() {
		return new ResponseInfo(worldCupService.getMemberPoints());
	}

	/**
	 * @param
	 * @Author: tangx.w
	 * @Description: 竞猜胜负投注接口
	 * @Date: 2018/5/16 13:32
	 */
	@MemberLogin
	@RequestMapping(path = "/winInvestmentPoints")
	public ResponseInfo winInvestmentPoints(@RequestBody InvestmentPointsDto investmentPointsDto) throws ParseException {
		Integer pointBalance = worldCupService.getMemberPoints();
		Calendar cal = Calendar.getInstance();
		Date now = new Date();
		Date endTime = PromotionUtils.pareTime(investmentPointsDto.getCompetitionTime());
		cal.setTime(endTime);
		cal.add(Calendar.MINUTE, -30);
		endTime = cal.getTime();
		Integer investmentPoint = investmentPointsDto.getInvestmentPoints();
		if (now.getTime()>=endTime.getTime()){
			throw new GlobalException(WorldCupExceptionEnum.THE_BETTING_TIME_OF_THE_GAME_HAS_PASSED);
		}
		if (investmentPoint > pointBalance ) {
			throw new GlobalException(WorldCupExceptionEnum.INSUFFICIENT_BALANCE_BALANCE);
		}else if (investmentPoint > 2000){
			throw new GlobalException(WorldCupExceptionEnum.INJECTION_INTEGRAL_OVER_LIMIT);
		}
		MemberTokenDto memberTokenDto = MemberContext.getCurrentMemberToken();
		worldCupService.winInvestmentPoints(investmentPointsDto,memberTokenDto);
		return new ResponseInfo();
	}

	/**
	 * @param
	 * @Author: tangx.w
	 * @Description: 获取世界杯球队信息
	 * @Date: 2018/5/16 13:32
	 */
	@MemberLogin
	@RequestMapping(path = "/getTeamInfo")
	public ResponseInfo getTeamInfo() throws ParseException {
		return new ResponseInfo(worldCupService.getWorldCupTeamInfo(teamEndTime));
	}

	/**
	 * @param
	 * @Author: tangx.w
	 * @Description: 竞猜冠军
	 * @Date: 2018/5/16 13:32
	 */
	@MemberLogin
	@RequestMapping(path = "/investmentTeam")
	public ResponseInfo investmentTeam(@RequestBody InvestmentTeamDto investmentTeamDto) throws ParseException {
		Integer pointBalance = worldCupService.getMemberPoints();
		Integer investmentPoint = investmentTeamDto.getInvestmentPoints();
		Date now = new Date();
		Date endTime = PromotionUtils.pareTime(teamEndTime);
		if (now.getTime()>=endTime.getTime()){
			throw new GlobalException(WorldCupExceptionEnum.THE_BETTING_TIME_OF_THE_GAME_HAS_PASSED);
		}
		if (investmentPoint > pointBalance) {
			throw new GlobalException(WorldCupExceptionEnum.INSUFFICIENT_BALANCE_BALANCE);
		}else if(investmentPoint != 1000){
			throw new GlobalException(WorldCupExceptionEnum.INCONFORMITY_OF_INJECTION_INTEGRAL);
		}
		MemberTokenDto memberTokenDto = MemberContext.getCurrentMemberToken();
		worldCupService.investmentTeam(investmentTeamDto,memberTokenDto);
		return new ResponseInfo();
	}

	/**
	 * @param
	 * @Author: tangx.w
	 * @Description: 上传胜负平
	 * @Date: 2018/5/16 13:32
	 */
	@RequestMapping(path = "/uploadResult")
	public ResponseInfo uploadResult(@RequestBody List<MatchResultDto> matchResultList) throws Exception {
		worldCupService.uploadResult(matchResultList);
		return new ResponseInfo();
	}

	/**
	 * @param
	 * @Author: tangx.w
	 * @Description: 上传冠军
	 * @Date: 2018/5/16 13:32
	 */
	@RequestMapping(path = "/uploadChampion")
	public ResponseInfo uploadChampion(@RequestBody MatchResultDto matchResult) {
		worldCupService.uploadChampion(matchResult);
		return new ResponseInfo();
	}



}
