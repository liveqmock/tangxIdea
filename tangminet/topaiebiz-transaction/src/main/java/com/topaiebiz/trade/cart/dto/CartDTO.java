package com.topaiebiz.trade.cart.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-05 16:59
 */
@Data
public class CartDTO implements Serializable{

    private static final long serialVersionUID = 2378574364285909770L;

    /**
     * 店铺节点
     */
    private List<CartShopDTO> shops = new ArrayList<>();

    /**
     * 不可用商品列表
     */
    private List<CartGoodsDTO> unusefulGoods = new ArrayList<>();
}
