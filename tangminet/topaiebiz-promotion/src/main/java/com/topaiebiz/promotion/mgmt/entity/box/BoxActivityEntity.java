package com.topaiebiz.promotion.mgmt.entity.box;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.time.LocalDateTime;


@TableName("t_pro_promotion_box")
@Data
public class BoxActivityEntity extends BaseBizEntity<Long> {
    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 8177760792416391527L;
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
