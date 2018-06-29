package com.topaiebiz.elasticsearch.dto;

import lombok.Data;

@Data
public class GoodsDto {

    //主键
    private Long id;

    //商品名称
    private String name;

    //市场价
    private Double marketPrice;

    //默认价格
    private Double defaultPrice;

    //销量
    private Long salesVolome;

    //所属店铺
    private Long belongStore;

    //主图
    private String pictureName;

}
