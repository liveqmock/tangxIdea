package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_pro_share_coupon_config")
public class ShareCouponEntity extends BaseBizEntity<Long> {
    /**
     * id
     * 全局唯一标识符
     */
    private Long id;

    /**
     * shareId
     * 营销活动promotionId
     */
    private Long promotionId;

    /**
     * shareId
     * 分享礼包id
     */
    private Long shareId;

    /**
     * couponPromotionId
     * 优惠券ID。
     */
    private Long couponPromotionId;

    /**
     * totalNum
     * 总数量
     */
    private Integer totalNum;

    /**
     * remainderNum
     * 优惠券剩余数量
     */
    private Integer remainderNum;

    /**
     * creatorId
     * 创建人编号。取值为创建人的全局唯一主键标识符。
     */
    private Long creatorId;

    /**
     * createdTime
     * 创建时间。取值为系统的当前时间。
     */
    private Date createdTime;

    /**
     * lastModifierId
     * 最后修改人编号。取值为最后修改人的全局唯一主键标识符。
     */
    private Long lastModifierId;

    /**
     * lastModifiedTime
     * 最后修改时间。取值为系统的当前时间。
     */
    private Date lastModifiedTime;



}