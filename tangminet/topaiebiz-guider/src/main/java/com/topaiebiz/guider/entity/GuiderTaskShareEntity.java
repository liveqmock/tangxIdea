package com.topaiebiz.guider.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * Created by admin on 2018/5/30.
 * 任务分享表
 */
@Data
@TableName("t_guider_task_share")
public class GuiderTaskShareEntity extends BaseBizEntity<Long> {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 会员ID。
     */
    private Long memberId;

    /**
     * 分享code值
     */
    private String shareCode;

    /**
     * 分享状态  0，有效；1，失效；
     */
    private Integer shareStatus;

}
