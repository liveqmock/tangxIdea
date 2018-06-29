package com.topaiebiz.merchant.store.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/20 0020 上午 10:22
 */
@Data
public class MerchantMemberDto extends PagePO implements Serializable {

    /**
     * 商家等级的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。
     */
    private Long id;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     *会员id
     */
    private Long  memberId;

}
