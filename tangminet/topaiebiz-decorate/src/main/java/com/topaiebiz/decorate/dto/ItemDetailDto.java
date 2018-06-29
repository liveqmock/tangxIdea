package com.topaiebiz.decorate.dto;

import com.topaiebiz.goods.dto.sku.GoodsDecorateDTO;
import lombok.Data;

import java.util.List;

/**
 * 商品详情DTO 用来做缓存处理
 *
 * @author huzhenjia
 * @since 2018/04/02
 */
@Data
public class ItemDetailDto {

    private List<GoodsDecorateDTO> goodsDecorateDTOS;

    private Integer price;

    private Integer evaluations;

    private Integer originalPrice;

    private Integer sales;

    private Integer title;

    private Integer integralDiscount;
}
