package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

@TableName("t_pro_promotion_coupon_config")
@Data
public class PromotionCouponConfigEntity extends BaseBizEntity<Long>{
    /**
     * id
     * 全局唯一标识符
     */
    private Long id;

    /**
     * promotionId
     * 分享活动ID。
     */
    private Long promotionId;

    /**
     * couponPromotionId
     * 优惠券ID。
     */
    private Long couponPromotionId;

    /**
     * 单个礼包优惠券发放总数
     */
    private Integer totalNum;


    /**
     * remainderNum
     * 优惠券剩余数量
     */
    private Integer remainderNum;

    /**
     * 优惠券发放总数
     */
    private Integer amount;

    /**
     * 优惠券领取总数
     */
    private Integer remainderAmount;


    /**
     * memo
     * 备注
     */
    private String memo;

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

    /**
     * 是否为发布老数据
     */
    private Byte isReleaseData;



}