package com.topaiebiz.promotion.worldcup.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 9:43 2018/5/29
 * @Modified by:
 */
@Data
public class WorldCupTeamAgainstDto {

	/**
	 * 比赛id
	 **/
	private Long matchId;

	/**
	 * 比赛时间
	 **/
	private Date competitionTime;

	/**
	 * 主队球星
	 **/
	private String homePlayer;

	/**
	 * 主队球员照片
	 **/
	private String homePlayPhoto;

	/**
	 * 主队国籍
	 **/
	private String homeCountry;

	/**
	 * 主队国旗照片
	 **/
	private String homeNationalFlag;

	/**
	 * 客队球星
	 **/
	private String visitingPlayer;

	/**
	 * 客队球员照片
	 **/
	private String visitingPlayPhoto;

	/**
	 * 客队国籍
	 **/
	private String visitingCountry;

	/**
	 * 客队国旗照片
	 **/
	private String visitingNationalFlag;

	/**
	 * 投注积分
	 **/
	private Integer investmentPoints;

	/**
	 * 投注类型
	 **/
	private String investmentProject;

	/**
	 * 投注入口
	 **/
	private Boolean isOpen;

	/**
	 * 比赛赔率
	 **/
	private String odds;

}
