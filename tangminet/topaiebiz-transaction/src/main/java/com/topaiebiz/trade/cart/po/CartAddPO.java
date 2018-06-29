package com.topaiebiz.trade.cart.po;

import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/***
 * @author yfeng
 * @date 2017-12-30 17:09
 */
@Data
public class CartAddPO implements Serializable {
    private static final long serialVersionUID = 2378574364285909770L;

    /**
     * 商品ID
     */
    @Min(value = 1, message = "${validation.cart.goods.id}")
    private Long goodsId;

    /**
     * 商品数量
     */
    @Min(value = 1, message = "${validation.cart.goods.num}")
    private Long num;
}