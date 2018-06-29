package com.topaiebiz.merchant.store.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/19 0019 上午 10:27
 */
@Data
public class MerchantFollowDto extends PagePO implements Serializable {

    /**
     * 商家等级的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。
     */
    private Long id;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 店铺
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺Log
     */
    private String images;

    /**
     * 店铺状态 2冻结
     */
    private Integer changeState;

}
