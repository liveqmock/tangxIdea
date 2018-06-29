package com.topaiebiz.guider.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2018/6/4.
 */
@Data
public class GuiderTaskInfoDto  extends PagePO implements Serializable {


    /**
     * 活动名称
     */
    private String taskName;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 活动开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 任务状态 1未开始，2进行中，0结束中
     */
    private Integer taskStatus;

    /**
     * 任务阶梯集合
     */
    private List<GuiderTaskLevelDto> guiderTaskLevelDtos;

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
