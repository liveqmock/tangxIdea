package com.topaiebiz.goods.category.backend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by dell on 2018/1/10.
 */
@Data
public class MerchantStoreDto implements Serializable{
    /** 所属商家。 */
    private Long merchantId;

    /** 所属店铺。 */
    private Long storeId;

}
