package com.topaiebiz.trade.refund.dto.page;

import com.topaiebiz.trade.refund.dto.common.RefundGoodDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Description 用户售后订单DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/8 12:33
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class CustomerRefundPageDTO implements Serializable {

    private static final long serialVersionUID = 1145212711086693417L;

    /**
     * 售后订单ID
     */
    private Long id;

    /**
     * 申请时间
     */
    private Date refundTime;

    /**
     * 退款类型（0:仅退款 / 1:退货退款）
     */
    private Integer refundType;

    /**
     * 售后状态值
     */
    private Integer refundState;

    /**
     * 售后的商品数量
     */
    private Integer refundGoodsNum;

    /**
     * 售后申请退款的价格
     */
    private BigDecimal refundPrice;

    /**
     * 售后中商品的总运费
     */
    private BigDecimal refundFreight;

    /**
     * 售后商品集合
     */
    private List<RefundGoodDTO> refundGoodDtos;

}
