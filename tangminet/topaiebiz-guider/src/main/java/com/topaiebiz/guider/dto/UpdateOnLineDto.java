package com.topaiebiz.guider.dto;

import lombok.Data;

/**
 * Created by admin on 2018/6/11.
 */
@Data
public class UpdateOnLineDto {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 是否上线
     */
    private Integer isOnLine;

}
