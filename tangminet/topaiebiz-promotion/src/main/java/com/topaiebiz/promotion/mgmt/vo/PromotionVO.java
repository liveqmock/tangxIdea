package com.topaiebiz.promotion.mgmt.vo;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

/**
 * 活动请求参数
 */
@Data
public class PromotionVO extends PagePO {
    /**
     * 活动ID
     */
    private Long promotionId;

}
