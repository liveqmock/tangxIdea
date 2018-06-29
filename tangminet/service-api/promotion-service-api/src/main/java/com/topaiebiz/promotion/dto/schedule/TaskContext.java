package com.topaiebiz.promotion.dto.schedule;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-06-28 17:41
 */
public class TaskContext {
    /**
     * 单品营销活动ID集合
     */
    @Getter
    private List<Long> singlePromotionIds = new ArrayList<>();
}
