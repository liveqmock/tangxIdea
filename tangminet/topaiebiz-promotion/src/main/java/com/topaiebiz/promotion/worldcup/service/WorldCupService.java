package com.topaiebiz.promotion.worldcup.service;

import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.promotion.worldcup.dto.*;
import com.topaiebiz.promotion.worldcup.entity.WorldCupTeamEntity;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 20:17 2018/5/28
 * @Modified by:
 */
public interface WorldCupService {
	/**
	 * @param * @param null
	 * @Author: tangx.w
	 * @Description: 获取世界活动首页
	 * @Date: 2018/5/29 10:43
	 */
	WorldCupPageDto getWorldCupHomePage() throws ParseException;

	/**
	 * @param * @param null
	 * @Author: tangx.w
	 * @Description: 获取用户积分余额
	 * @Date: 2018/5/29 17:27
	 */
	Integer getMemberPoints();

	/**
	 * @param investmentPointsDto
	 * @Author: tangx.w
	 * @Description: 投注
	 * @Date: 2018/5/29 17:49
	 */
	void winInvestmentPoints(InvestmentPointsDto investmentPointsDto, MemberTokenDto memberTokenDto);

	/**
	 * @param * @param null
	 * @Author: tangx.w
	 * @Description: 获取世界杯球队信息
	 * @Date: 2018/5/29 19:48
	 */
	TeamInfoDto getWorldCupTeamInfo(String teamEndTime) throws ParseException;

	/**
	 * @param * @param null
	 * @Author: tangx.w
	 * @Description: 夺冠球队押注
	 * @Date: 2018/5/29 20:16
	 */
	void investmentTeam(InvestmentTeamDto investmentTeamDto, MemberTokenDto memberTokenDto);

	/**
	 * @param matchResultList
	 * @Author: tangx.w
	 * @Description: 上传比赛结果
	 * @Date: 2018/5/29 20:32
	 */
	void uploadResult(List<MatchResultDto> matchResultList) throws ParseException, IOException, InvalidFormatException, InterruptedException;


	/**
	 * @param matchResult
	 * @Author: tangx.w
	 * @Description: 上传冠军
	 * @Date: 2018/5/29 20:32
	 */
	void uploadChampion(MatchResultDto matchResult);

}
