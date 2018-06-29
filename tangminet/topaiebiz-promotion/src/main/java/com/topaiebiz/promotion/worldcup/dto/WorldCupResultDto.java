package com.topaiebiz.promotion.worldcup.dto;

import lombok.Data;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 9:07 2018/5/30
 * @Modified by:
 */
@Data
public class WorldCupResultDto {

	private Integer resultSize;

	public Integer getResultSize() {
		return resultSize;
	}

	public void setResultSize(Integer resultSize) {
		this.resultSize = resultSize;
	}

}
