package com.topaiebiz.promotion.worldcup.dto;

import lombok.Data;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 10:36 2018/5/30
 * @Modified by:
 */
@Data
public class MatchResultDto {

	private Long matchId;

	private Integer homeTeamScored;

	private Integer visitingTeamScored;

	private String championId;
}
