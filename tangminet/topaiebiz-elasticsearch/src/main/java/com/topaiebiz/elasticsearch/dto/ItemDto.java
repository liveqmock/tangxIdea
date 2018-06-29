package com.topaiebiz.elasticsearch.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.topaiebiz.elasticsearch.serialize.ItemDtoSerialize;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author tangx.w
 * @Description: 搜索引擎传参dto
 * @Date: Create in 19:01 2018/6/20
 * @Modified by:
 */
@Data
@JsonSerialize(using = ItemDtoSerialize.class)
public class ItemDto {

	private Long id;

	private String name;

	private BigDecimal marketPrice;

	private BigDecimal defaultPrice;

	private Long belongStore;

	private String brandName;

	private String backName;

	private Long salesVolume;

	private String pictureName;

	private Byte deletedFlag;

	private Integer status;

	private Integer frozenFlag;

}
