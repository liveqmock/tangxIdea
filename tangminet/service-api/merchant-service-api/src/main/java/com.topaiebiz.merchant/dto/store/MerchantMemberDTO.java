package com.topaiebiz.merchant.dto.store;

import lombok.Data;

import java.io.Serializable;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/20 0020 下午 2:34
 */
@Data
public class MerchantMemberDTO  implements Serializable {

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     *会员id
     */
    private Long  memberId;

}
