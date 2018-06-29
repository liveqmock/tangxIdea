package com.topaiebiz.merchant.store.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/20 0020 上午 10:25
 */
@TableName("t_mem_merchant_member")
@Data
public class MerchantMemberEntity  extends BaseBizEntity<Long> {
    /**
     * 版本序列化
     */
    private static final long serialVersionUID = -9016636675413808094L;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 店铺
     */
    private Long storeId;
}
