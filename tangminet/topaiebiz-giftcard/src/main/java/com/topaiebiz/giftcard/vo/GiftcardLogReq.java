package com.topaiebiz.giftcard.vo;

import com.nebulapaas.base.po.PagePO;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 下午7:25 2018/1/17
 */
public class GiftcardLogReq extends PagePO {

    /**
     * 卡单元id
     */
    @NotNull(message = "卡单元id不能为空")
    private Long unitId;

    /**
     * 日志类型：1-消费，2-退款，3-绑定，4-冻结，5-解冻，6-续期
     */
    private Integer logType;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
