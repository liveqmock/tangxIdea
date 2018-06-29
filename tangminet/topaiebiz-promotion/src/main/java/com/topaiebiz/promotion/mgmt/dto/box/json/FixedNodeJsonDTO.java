package com.topaiebiz.promotion.mgmt.dto.box.json;

import lombok.Data;

/**
 * 固定节点（产生宝箱）
 */
@Data
public class FixedNodeJsonDTO {
    /**
     * 节点类型（0-时间节点，1-登录，2-分享，3-支付）
     */
    private Integer nodeType;
    /**
     * 概率
     */
    private Double rate;
    /**
     * 限制数量
     */
    private Integer limited;
    /**
     * 状态（0-可用，1-不可用）
     */
    private Integer state;
}
