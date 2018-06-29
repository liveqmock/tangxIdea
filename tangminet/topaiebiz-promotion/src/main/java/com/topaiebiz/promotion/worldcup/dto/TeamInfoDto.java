package com.topaiebiz.promotion.worldcup.dto;

import com.topaiebiz.promotion.worldcup.entity.WorldCupTeamEntity;
import lombok.Data;

import java.util.List;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 19:52 2018/5/29
 * @Modified by:
 */
@Data
public class TeamInfoDto {

	private List<WorldCupTeamEntity> teamList;

	private Boolean isOpen;

	private String investmentTeamId;
}
