package com.topaiebiz.guider.dto;

import lombok.Data;

/**
 * Created by admin on 2018/6/5.
 */
@Data
public class GuiderTaskLevelPrizeDto {


    /**
     * 任务id
     */
    private Long taskId;

    /**
     *阶梯id
     */
    private Long levelId;

    /**
     * 奖励配置id
     */
    private Long prizeId;

    /**
     *奖励类型 1订单比例，2优惠券，3美礼卡，4现金，5积分，6实物奖
     */
    private Integer prizeObjType;

    /**
     *奖励内容
     */
    private String prizeObjValue;

    /**
     *奖励名称
     */
    private String prizeObjTitle;
}
