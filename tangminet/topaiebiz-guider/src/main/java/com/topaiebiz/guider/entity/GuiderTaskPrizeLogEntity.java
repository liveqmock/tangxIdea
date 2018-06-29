package com.topaiebiz.guider.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * Created by admin on 2018/6/5.
 */
@Data
@TableName("t_guider_task_prize_log")
public class GuiderTaskPrizeLogEntity  extends BaseBizEntity<Long> {

    /**
     *会员id
     */
    private Long memberId;

    /**
     *任务id
     */
    private Long taskId;

    /**
     *  阶梯奖励类型 1拉新,2订单
     */
    private Integer levelType;

    /**
     *  奖励发放状态  0成功，1失败
     */
    private Long  sendStatus;

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

    /**
     *奖励阶梯id
     */
    private  Long prizeLevelId;

    /**
     *奖励发送原因
     */
    private String sendMsg;

}
