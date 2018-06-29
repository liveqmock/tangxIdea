package com.topaiebiz.promotion.mgmt.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.util.Date;

/**
 * Created by Joe on 2018/1/26.
 */
@Data
public class MemberCouponDto extends PagePO {

    /**
     * id
     */
    private Long id;

    /**
     * 会员编号
     */
    private Long memberId;

    /**
     * 使用状态
     */
    private Integer usageState;

    /**
     * 优惠券id
     */
    private Long couponId;

    /**
     * 优惠券的所属店铺
     */
    private Long storeId;

    /**
     * 领取时间
     */
    private Date receiverTime;

    /**
     * 使用订单编号
     */
    private Long orderId;

    /**
     * 备注
     */
    private String memo;

    /**
     * 逻辑删除标识
     */
    private byte deletedFlag;

}
