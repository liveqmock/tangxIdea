package com.topaiebiz.promotion.mgmt.dto.sec.kill;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Auther: xuyuhua
 * @Date: 2018/6/11 09:24
 * @Description:
 */
@Data
public class PromotionGoodsDTO implements Serializable {

    private static final long serialVersionUID = -7663520630032270082L;

    /**
     * 所属商品
     */
    private Long itemId;

    /**
     * 活动数量
     */
    private Integer promotionNum;

    /**
     * 活动价格
     */
    private BigDecimal promotionPrice;

    /**
     * 活动销量
     */
    private Integer quantitySales;

}
