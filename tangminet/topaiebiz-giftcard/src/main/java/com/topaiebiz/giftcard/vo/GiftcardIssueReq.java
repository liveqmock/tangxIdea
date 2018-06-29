package com.topaiebiz.giftcard.vo;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

/**
 * @description: 礼卡批次查询参数
 * @author: Jeff Chen
 * @date: created in 上午11:09 2018/1/16
 */
@Data
public class GiftcardIssueReq extends PagePO {
    /**
     * 批次号 统一为批次表中的主键id，batchId
     */
    private String batchNo;

    /**
     * 礼卡名称
     */
    private String cardName;

    /**
     * 卡介质
     */
    private Integer medium;

    /**
     * 标签id
     */
    private Long labelId;
    /**
     * 发行状态
     */
    private Integer issueStatus;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 卡片属性
     */
    private Integer cardAttr;
}
