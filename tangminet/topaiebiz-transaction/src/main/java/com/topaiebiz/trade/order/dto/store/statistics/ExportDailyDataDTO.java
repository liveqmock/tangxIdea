package com.topaiebiz.trade.order.dto.store.statistics;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/24 11:02
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class ExportDailyDataDTO implements Serializable{

    private String orderTime;
    private Long orderId;
    private String thirdCategory;
    private String secondCategory;
    private String firstCategory;

    private Long memberId;
    private String storeName;
    private Integer orderState;
    private String memberTelephone;
    private Long goodsSkuId;
    private Long goodsItemId;
    private String goodsName;
    private BigDecimal goodsPrice;

    private Integer goodsNum;
    private BigDecimal orderTotal;
    private BigDecimal platformDiscount;
    private BigDecimal storeDiscount;

    private BigDecimal balance;
    private BigDecimal cardPrice;
    private BigDecimal score;
    private BigDecimal payPrice;
}
