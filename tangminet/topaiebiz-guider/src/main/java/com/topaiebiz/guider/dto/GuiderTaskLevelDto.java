package com.topaiebiz.guider.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by admin on 2018/6/5.
 */
@Data
public class GuiderTaskLevelDto {

    /**
     * 活动id
     */
    private  Long taskId;

    /**
     *左闭合区间
     */
    private Integer leftInclusiveInterval;

    /**
     *右开区间
     */
    private Integer rightOpenInterval;

    /**
     * 阶梯奖励类型 1拉新,2订单
     */
    private Integer  levelType;

    /**
     *阶梯id
     */
    private Long levelId;

    /**
     * 奖励配置设置集合
     */
    private List<GuiderTaskLevelPrizeDto> guiderTaskLevelPrizeDtos;



}
