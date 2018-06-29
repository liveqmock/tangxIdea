package com.topaiebiz.promotion.mgmt.dto.card;

import lombok.Data;

import java.util.List;

/**
 * 礼卡秒杀（C端）
 */
@Data
public class CardSecKillDTO implements Comparable<CardSecKillDTO> {
    /**
     * 礼卡秒杀开始时间
     */
    private String startTime;
    /**
     * 状态(0-即将开始，1-进行中，2-已抢光，3-已结束)
     */
    private Integer state;
    /**
     * 礼卡详情列表
     */
    private List<CardSecKillItemDTO> itemList;

    @Override
    public int compareTo(CardSecKillDTO o) {
        if (o.startTime != null) {
            if (this.startTime.compareTo(o.startTime) > 0) {
                return 1;
            } else if (this.startTime.compareTo(o.startTime) < 0) {
                return -1;
            }
        }
        return 0;
    }
}
