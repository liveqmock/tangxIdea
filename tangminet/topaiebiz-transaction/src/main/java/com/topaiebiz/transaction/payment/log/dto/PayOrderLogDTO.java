package com.topaiebiz.transaction.payment.log.dto;

import lombok.Data;

import java.util.Date;

/***
 * @author yfeng
 * @date 2018-01-03 12:12
 */
@Data
public class PayOrderLogDTO {
    /**
     * ID
     */
    private Long id;

    private Long creatorId;
    private Date createdTime;
    private Long lastModifierId;
    private Date lastModifiedTime;

    /**
     * 支付订单号
     */
    private Long payOrderId;

    /**
     * 1微信 2支付宝 3等 可以写到数据字典里
     */
    private String payType;

    /**
     * 是否成功
     */
    private Integer successState;

    /**
     * 金额
     */
    private Double money;

    /**
     * 对方账号
     */
    private String otherAccount;
}
