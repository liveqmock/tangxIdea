package com.topaiebiz.promotion.api;

import com.topaiebiz.promotion.dto.schedule.TaskContext;

/***
 * @author yfeng
 * @date 2018-06-28 17:29
 */
public interface PromotionTaskApi {
    void promotionStartTask(TaskContext taskContext);

    void promotionFinishTask(TaskContext taskContext);
}