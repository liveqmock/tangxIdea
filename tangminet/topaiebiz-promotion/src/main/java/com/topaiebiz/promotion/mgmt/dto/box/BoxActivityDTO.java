package com.topaiebiz.promotion.mgmt.dto.box;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 活动开宝箱配置
 */
@Data
public class BoxActivityDTO implements Serializable {
    private static final long serialVersionUID = -6041232769628966086L;
    /**
     * ID
     */
    private Long id;
    /**
     * 营销活动ID
     */
    private Long promotionId;
    /**
     * 指定出现开始时间
     */
    private LocalDateTime startTime;
    /**
     * 指定出现结束时间
     */
    private LocalDateTime endTime;
    /**
     * 固定触发节点(JSON串)。登录、支付、分享c
     */
    private String fixedNode;
    /**
     * 时间触发节点(JSON串)。具体的时间点
     */
    private String timeNode;
    /**
     * 奖池配置(JSON串)。优惠券、美礼卡、实物奖
     */
    private String awardPool;
    /**
     * 奖品出现率(JSON串)。优惠券、美礼卡、实物奖、总概率
     */
    private String rate;
    /**
     * 宝箱排序号
     */
    private Integer sortNumber;
    /**
     * 备注
     */
    private String memo;
}