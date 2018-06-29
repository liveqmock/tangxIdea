package com.topaiebiz.trade.order.po.ordersubmit;

import lombok.Data;

import java.io.Serializable;

/***
 * @author yfeng
 * @date 2018-01-09 15:28
 */
@Data
public class OrderRequestInvoice implements Serializable {
    private static final long serialVersionUID = 1L;

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

    /** 地址电话。*/
    private String addressTel;

    /** 开户行及账号。*/
    private String account;

    /** 电子发票路径。 */
    private String invoiceImage;

    /** 发票代码。 */
    private String invoiceCode;

    /** 发票号码。 */
    private String invoiceNum;
}