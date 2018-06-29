package com.topaiebiz.goods.dto.category.backend;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-03-26 14:44
 */
@Data
public class MerchantCategoryCommissionDTO implements Serializable {
    private static final long serialVersionUID = -5246313191983118447L;
    /**
     * 平台类目ID。
     */
    private Long categoryId;
    /**
     * 所属商家。
     */
    private Long merchantId;

    /**
     * 所属店铺。
     */
    private Long storeId;
    /**
     * 佣金比例。
     */
    private BigDecimal commissionRatio;
}