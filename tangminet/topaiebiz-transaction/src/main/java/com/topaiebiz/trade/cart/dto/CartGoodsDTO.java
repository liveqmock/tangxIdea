package com.topaiebiz.trade.cart.dto;

import com.topaiebiz.trade.constants.CartStatus;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/***
 *
 * @author yfeng
 * @date 2017-12-30 16:27
 */
@Data
public class CartGoodsDTO implements Serializable {
    private static final long serialVersionUID = 2378574364285909770L;
    /**
     * 购物车ID
     */
    private Long cartId;

    private Long itemId;

    /**
     * 商品ID
     */
    private Long goodsId;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品图片
     */
    private String goodsImg;
    /**
     * 商品价格
     */
    private BigDecimal price;
    /**
     * 商品数量
     */
    private Long num;
    /**
     * 库存
     */
    private Long storage;

    private String saleFieldValue;

    private Integer status;

    /**
     * 营销活动
     */
    private List<CardPromotionDTO> promotions = new ArrayList<>();

    public boolean valid(){
        return CartStatus.GoodsStatus.NORMAL.equals(this.getStatus());
    }
}