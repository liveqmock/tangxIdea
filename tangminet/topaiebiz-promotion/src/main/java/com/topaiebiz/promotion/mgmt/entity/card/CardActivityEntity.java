package com.topaiebiz.promotion.mgmt.entity.card;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.time.LocalTime;

@TableName("t_pro_promotion_card")
@Data
public class CardActivityEntity extends BaseBizEntity<Long> {
    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 3448523493763554615L;
    /**
     * ID
     */
    private Long id;
    /**
     * 活动的Id
     */
    private Long promotionId;
    /**
     * 秒杀开始时间
     */
    private LocalTime startTime;
    /**
     * 结束时间
     */
    private LocalTime endTime;
    /**
     * 每个Id限制购买次数
     */
    private Integer buyLimit;
    /**
     * 活动备注
     */
    private String memo;

}
