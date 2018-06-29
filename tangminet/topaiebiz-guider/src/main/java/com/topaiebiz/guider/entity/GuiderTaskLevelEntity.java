package com.topaiebiz.guider.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * Created by admin on 2018/6/5.
 */
@Data
@TableName("t_guider_task_level")
public class GuiderTaskLevelEntity extends BaseBizEntity<Long> {


    /**
     * 活动id
     */
    private Long taskId;

    /**
     * 左闭合区间
     */
    private Integer leftInclusiveInterval;

    /**
     * 右开区间
     */
    private Integer rightOpenInterval;

    /**
     * 阶梯奖励类型 1拉新,2订单
     */
    private Integer levelType;
}
