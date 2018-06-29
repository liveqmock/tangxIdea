package com.topaiebiz.dec.dto;


import lombok.Data;

import java.util.List;

/**
 * <p>
 * 模块商品详情表
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@Data
public class ModuleGoodsDto {


    /**
     * 模块ID。
     */
	private Long moduleId;
    /**
     * 商品详情
     */
	private List<GoodsInfoDto> goodsInfoDetail;


    /**
     * 图片URL
     */
  //  private String image;
}
