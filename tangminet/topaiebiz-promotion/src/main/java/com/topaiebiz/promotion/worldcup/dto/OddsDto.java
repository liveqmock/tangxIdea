package com.topaiebiz.promotion.worldcup.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 16:35 2018/5/31
 * @Modified by:
 */
@Data
public class OddsDto {

	private BigDecimal win;

	private BigDecimal draw;

	private BigDecimal lose;
}
