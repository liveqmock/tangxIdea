package com.topaiebiz.promotion.mgmt.dto.box;

import lombok.Data;

import java.io.Serializable;

/**
 * 会员宝箱（C端）
 */
@Data
public class MemberBoxDTO implements Serializable {
    private static final long serialVersionUID = -4050009746938557473L;
    /**
     * 活动开宝箱编号
     */
    private Long promotionId;
    /**
     * 宝箱数量
     */
    private Integer awardCount;
}
