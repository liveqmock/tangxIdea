package com.topaiebiz.dec.dto;


import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 标题商品详情表
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@Data
public class TitleGoodsDto {


    /**
     * 标题ID。
     */
    private Long titleId;
    /**
     * 商品信息
     */
    List<GoodsInfoDto> goodsInfoDetail;

    /**
     * 备注。
     */
    private String memo;

    /**
     * 分页
     */
    private PagePO pagePO;
}
