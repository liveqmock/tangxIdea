package com.topaiebiz.decorate.component;

import lombok.Data;

import java.util.List;

/**
 * 商品集合VO
 *
 * @author huzhenjia
 * @since 2018/03/29
 */
@Data
public class ItemListVO {

    private List<ItemVO> itemVOS;

    /**
     * 展示元素:0为不展示，1为展示
     **/
    private Integer price;//售价

    private Integer evaluations;//评价数量

    private Integer originalPrice;//原价

    private Integer sales;//销量

    private Integer title;//标题

    private Integer integralDiscount;//积分折扣
}
