package com.topaiebiz.guider.dto;

import lombok.Data;
import sun.util.resources.ga.LocaleNames_ga;

import java.util.List;

/**
 * Created by admin on 2018/6/5.
 */
@Data
public class GuiderTaskLevelsDto {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 任务阶梯集合
     */
    private List<GuiderTaskLevelDto> guiderTaskLevelDtos;
}
