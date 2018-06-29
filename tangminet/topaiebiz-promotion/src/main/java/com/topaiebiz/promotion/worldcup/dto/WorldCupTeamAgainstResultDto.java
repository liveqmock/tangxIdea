package com.topaiebiz.promotion.worldcup.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 10:24 2018/5/29
 * @Modified by:
 */
@Data
public class WorldCupTeamAgainstResultDto {

	/** 比赛时间 **/
	private Date competitionTime;

	/** 主队球星 **/
	private String homePlayer;

	/** 主队球员照片 **/
	private String homePlayPhoto;

	/** 主队国籍 **/
	private String homeCountry;

	/** 主队国旗照片 **/
	private String homeNationalFlag;

	/** 客队球星 **/
	private String visitingPlayer;

	/** 客队球员照片 **/
	private String visitingPlayPhoto;

	/** 客队国籍 **/
	private String visitingCountry;

	/** 客队国旗照片 **/
	private String visitingNationalFlag;

	/** 比赛结果 **/
	private String againstResult;

	/** 投注积分 **/
	private Integer investmentPoints;

	/** 投注输赢 **/
	private Integer investmentResult;
}
