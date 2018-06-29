package com.topaiebiz.trade.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description 订单发票
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/12 17:49
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderInvoiceDTO implements Serializable{

    private static final long serialVersionUID = 2498501211477642541L;

    /** 订单id*/
    private Long orderId;

    /** 店铺id*/
    private Long storeId;

    /** 发票类型。 1 普通 2电子 3增值税*/
    private Short invoiceType;

    /** 发票抬头。*/
    private String title;

    /** 发票内容。*/
    private String text;

    /** 纳税人识别号。*/
    private String taxpayerNo;

    /** 增值税发票专用。1 订单完成后开票*/
    private Short modeType;

    /** 增值税发票专用。*/
    private String name;

    /** 开票金额。*/
    private BigDecimal sum;

    /** 地址电话。*/
    private String addressTel;

    /** 开户行及账号。*/
    private String account;

    /** 状态。1 已开 2未开*/
    private Integer state;

    /** 电子发票路径。 */
    private String invoiceImage;

    /** 发票代码。 */
    private String invoiceCode;

    /** 发票号码。 */
    private String invoiceNum;

}
