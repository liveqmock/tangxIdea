package com.topaiebiz.guider.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created by admin on 2018/5/30.
 * 任务信息表
 */
@Data
@TableName("t_guider_task_info")
public class GuiderTaskInfoEntity  extends BaseBizEntity<Long> {

    /**
     * 活动名称
     */
    private String taskName;

    /**
     * 活动开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 任务描述
     */
    private String taskDescription;

    /**
     * 奖励时长
     */
    private Integer prizeDuration;

    /**
     * 是否注册  0已经注册  1没有注册
     */
    private Integer isRegister;

    /**
     * 是否登录  0已经登录 1没有登录
     */
    private Integer isAppLogin;

    /**
     * 是否关注公众号  0已经关注，1没有关注
     */
    private Integer isSubsribeWx;

    /**
     * 是否上线
     */
    private Integer isOnLine;






}
