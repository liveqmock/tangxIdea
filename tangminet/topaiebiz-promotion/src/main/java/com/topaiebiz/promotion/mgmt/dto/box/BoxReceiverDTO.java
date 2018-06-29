package com.topaiebiz.promotion.mgmt.dto.box;

import lombok.Data;

/**
 * 实物宝箱领奖人信息（C端）
 */
@Data
public class BoxReceiverDTO {
    /**
     * 会员ID
     */
    private Long memberId;
    /**
     * 宝箱记录ID
     */
    private Long boxRecordId;
    /**
     * 领奖人姓名
     */
    private String name;
    /**
     * 领奖人手机号
     */
    private String mobile;
    /**
     * 领奖人地址
     */
    private String address;
}
