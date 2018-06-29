package com.topaiebiz.giftcard.vo;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

/**
 * @description: 礼卡实体查询请求
 * @author: Jeff Chen
 * @date: created in 上午9:10 2018/1/17
 */
@Data
public class GiftcardUnitReq extends PagePO{

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 卡号起始
     */
    private String cardNoStart;
    /**
     * 卡号结束
     */
    private String cardNoEnd;
    /**
     * 批次号 统一为批次表中的主键id，batchId
     */
    private String batchNo;

    /**
     * 卡片状态
     */
    private Integer cardStatus;
    /**
     * 标签id
     */
    private Long labelId;
    /**
     * 0实体卡 4电子卡
     */
    private Integer medium;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
}
