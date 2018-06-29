package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * 会员优惠券
 * Created by Joe on 2018/1/6.
 */
@TableName("t_pro_member_coupon")
@Data
public class MemberCouponEntity extends BaseBizEntity<Long> {

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

    public void clearInit() {
        this.setCreatedTime(null);
        this.setVersion((Long) null);
    }

}
