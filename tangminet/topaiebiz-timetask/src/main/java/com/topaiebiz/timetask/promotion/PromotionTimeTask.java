package com.topaiebiz.timetask.promotion;

import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.topaiebiz.promotion.api.BoxActivityApi;
import com.topaiebiz.promotion.api.CardActivityApi;
import com.topaiebiz.promotion.api.PromotionTaskApi;
import com.topaiebiz.promotion.dto.schedule.TaskContext;
import com.topaiebiz.promotion.mgmt.service.PromotionService;
import com.topaiebiz.timetask.quartzaop.aop.QuartzContextOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.nebulapaas.common.msg.dto.MessageTypeEnum.SINGLE_PROMOTION_UPDATE;

/**
 * Description 营销活动相关定时任务
 * <p>
 * <p>
 * Author Joe
 * <p>
 * Date 2017年11月22日 下午9:15:24
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Service
public class PromotionTimeTask {
    @Autowired
    private BoxActivityApi boxActivityApi;
    @Autowired
    private CardActivityApi cardActivityApi;
    @Autowired
    private PromotionService promotionService;
    @Autowired
    private PromotionTaskApi promotionTaskApi;
    @Autowired
    private MessageSender messageSender;

    /**
     * Description 开始活动/结束活动(每分钟一次)
     * <p>
     * Author Joe
     */
    @QuartzContextOperation
    @Scheduled(cron = "0 * * * * ?")
    public void timingTask() {
        log.warn(">>>>>>>>>>>>>>>>>>>> 活动自动 [ 开始 / 结束 ] 任务启动");
        TaskContext taskContext = new TaskContext();
        promotionTaskApi.promotionStartTask(taskContext);
        promotionTaskApi.promotionFinishTask(taskContext);

        List<Long> singlePromotionIds = taskContext.getSinglePromotionIds();
        if (CollectionUtils.isEmpty(singlePromotionIds)) {
            return;
        }

        //发布消息，更新所有的商品
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setType(SINGLE_PROMOTION_UPDATE);
        messageDTO.getParams().put("promotionIds", singlePromotionIds);
        messageSender.publicMessage(messageDTO);
    }

    /**
     * 活动统计
     */
    @QuartzContextOperation
    @Scheduled(cron = "0 0/30 * * * ?")
    public void promotionStatistical() {
        promotionService.promotionStatistical();
    }

    /**
     * 活动报名
     */
    @QuartzContextOperation
    @Scheduled(cron = "0 0/30 * * * ?")
    public void promotionApply() {
        promotionService.promotionApply();
    }


    /**
     * 初始化当前活动的礼卡剩余库存
     */
    @QuartzContextOperation
    @Scheduled(cron = "0 0 0 * * ?")
    public void initRestStorage() {
        log.info("----------initCardRestStorage starting...");
        cardActivityApi.initRestStorage();
        log.info("----------initCardRestStorage end...");

        log.info("----------initBoxRestStorage starting...");
//        boxActivityApi.initRestStorage();
        log.info("----------initBoxRestStorage end...");
    }

}
