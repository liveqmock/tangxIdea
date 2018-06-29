package com.topaiebiz.giftcard.vo;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

/**
 * @description: 礼卡精选查询请求
 * @author: Jeff Chen
 * @date: created in 下午6:57 2018/1/18
 */
@Data
public class GiftcardSelectReq extends PagePO{

    /**
     * 批次号
     */
    private String batchNo;
    /**
     * 礼卡名称
     */
    private String cardName;
    /**
     * 标签id
     */
    private Long labelId;
}
