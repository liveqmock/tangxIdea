package com.topaiebiz.trade.cart.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/***
 * 购物车基于店铺拆单的数据模型
 * @author yfeng
 * @date 2017-12-30 16:28
 */
@Data
public class CartShopDTO implements Serializable {
    private static final long serialVersionUID = 2378574364285909770L;

    /**
     * 店铺ID
     */
    private Long storeId;
    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 购物车里当前店铺商品总价
     */
    private BigDecimal goodsAmount;

    /**
     * 商品信息
     */
    private List<CartGoodsDTO> goodsList = new ArrayList<>();

    /**
     * 店铺营销活动
     */
    private List<CardPromotionDTO> promotions = new ArrayList<>();

    /**
     * 包邮活动
     */
    private CardPromotionDTO freightPromotion;

    public void caculateGoodsAmout(){
        BigDecimal val = BigDecimal.ZERO;
        for (CartGoodsDTO goods : goodsList){
            BigDecimal curGoodsAmount = goods.getPrice().multiply(new BigDecimal(goods.getNum()));
            val = val.add(curGoodsAmount);
        }
        this.goodsAmount = val;
    }
}