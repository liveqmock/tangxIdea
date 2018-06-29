package com.topaiebiz.promotion.worldcup.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 10:12 2018/5/29
 * @Modified by:
 */
@Data
public class WorldCupPageDto {

	private List<WorldCupTeamAgainstDto> teamAgainstList;

	private List<WorldCupTeamAgainstResultDto> teamAgainstResultList;
}
