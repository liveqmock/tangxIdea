package com.topaiebiz.guider.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created by admin on 2018/5/30.
 * 任务拉新新用户
 */
@Data
@TableName("t_guider_task_new_user")
public class GuiderTaskNewUserEntity extends BaseBizEntity<Long> {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 会员ID。
     */
    private Long memberId;

    /**
     * 来源id
     */
    private Long srcMemberId;

    /**
     * 导购推广用户排序ID
     */
    private Integer sortingId;

    /**
     * 分享id
     */
    private Long shareId;

    /**
     * 分享code值
     */
    private String shareCode;

    /**
     * 分享状态  0，有效；1，失效；
     */
    private Byte shareStatus;

    /**
     * 是否关注微信  0是，1否
     */
    private Integer isSubscribeWeixin;

    /**
     * 是否ios登陆   1是，0否
     */
    private Integer isIosLogin;

    /**
     * 是否安卓登陆  0是，1否
     */
    private Integer isAndroidLogin;

    private Boolean isCompleteTask;

    private Date completeTime;

    private Integer prizeDuration;
}
