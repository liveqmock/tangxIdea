package com.topaiebiz.trade.dto.order.openapi;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.topaiebiz.trade.dto.order.OrderAddressDTO;
import com.topaiebiz.trade.dto.order.OrderDetailDTO;
import com.topaiebiz.trade.dto.order.OrderGoodsDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Description api 订单详情
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/25 16:06
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
@NoArgsConstructor
public class APIOrderDetailDTO implements Serializable {
    private static final long serialVersionUID = -587160993717500298L;

    /***
     * 2018-04-25
     * id->orderId,
     * orderGoodsDTOS -> orderSkus
     * payDTO删除
     * addressDTO删除
     * 美礼卡支付明细两个字段删除
     * goodsTotal -> goodsTotalPrice
     * freightTotal -> freightTotalPrice
     * actualFreight -> actualFreightPrice
     */

    /**
     * 订单号, 会员姓名, 会员手机号, 店铺ID, 店铺名称,
     */
    private Long orderId;
    private String memberName;
    private String memberTelephone;
    private Long storeId;
    private String storeName;

    /**
     * 支付订单号, 支付方式
     * 下单时间, 支付时间, 发货时间， 收货时间， 完成时间
     * 订单状态, 是否开具发票, 是否被锁定, 是否延长收货
     */
    private Long payId;
    private String payType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date orderTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date payTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date shipmentTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date receiveTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date completeTime;
    private Integer orderState;
    private Integer invoiceState;
    private Integer lockState;
    private Integer extendShip;
    private Integer refundState;

    /**
     * 店铺优惠金额, 店铺优惠券优惠金额, 平台优惠金额, 营销优惠总额(不算包邮优惠)
     */
    private BigDecimal storeDiscount;
    private BigDecimal storeCouponDiscount;
    private BigDecimal platformDiscount;
    private BigDecimal discountTotal;

    /**
     * 商品总价格, 总运费, 实际物流费用, 实际支付
     * 使用积分数量, 余额, 美礼卡, 第三方支付金额, 第三方交易号
     */
    private BigDecimal goodsTotalAmount;
    private BigDecimal freightTotalAmount;
    private BigDecimal actualFreightAmount;
    private BigDecimal payAmount;
    private Long scoreNum;
    private BigDecimal scoreAmount;
    private BigDecimal balance;
    private BigDecimal cardAmount;
    private BigDecimal thirdPaymentAmount;
    private String outerPaySn;

    /**
     * 是否海淘
     */
    private Integer haitao;

    /**
     * 商品详情DTO
     */
    private List<APIOrderSkuDTO> orderSkus;

    /**
     * 收货人姓名, 省, 市, 区, 收货人详细地址, 收货人姓名, 支付人身份证号, 支付人姓名
     */
    private String receiverName;
    private String receiverProvince;
    private String receiverCity;
    private String receiverCounty;
    private String receiverTelephone;
    private String receiverDetailAddress;
    private String buyerIdCard;
    private String buyerName;

    /**
     * 物流公司ID
     */
    private String expressComName;
    private String expressNo;

    /**
     * 备注
     */
    private String memo;

    public APIOrderDetailDTO(OrderDetailDTO orderDetailDTO) {
        this.orderId = orderDetailDTO.getId();
        this.memberName = orderDetailDTO.getMemberName();
        this.memberTelephone = orderDetailDTO.getMemberTelephone();
        this.storeId = orderDetailDTO.getStoreId();
        this.storeName = orderDetailDTO.getStoreName();
        this.payId = orderDetailDTO.getPayId();
        this.payType = orderDetailDTO.getPayType();
        this.orderTime = orderDetailDTO.getOrderTime();
        this.payTime = orderDetailDTO.getPayTime();
        this.shipmentTime = orderDetailDTO.getShipmentTime();
        this.receiveTime = orderDetailDTO.getReceiveTime();
        this.completeTime = orderDetailDTO.getCompleteTime();
        this.orderState = orderDetailDTO.getOrderState();
        this.invoiceState = orderDetailDTO.getInvoiceState();
        this.lockState = orderDetailDTO.getLockState();
        this.refundState = orderDetailDTO.getRefundState();
        this.extendShip = orderDetailDTO.getExtendShip();
        this.storeDiscount = orderDetailDTO.getStoreDiscount();
        this.storeCouponDiscount = orderDetailDTO.getStoreCouponDiscount();
        this.platformDiscount = orderDetailDTO.getPlatformDiscount();
        this.discountTotal = orderDetailDTO.getDiscountTotal();
        this.goodsTotalAmount = orderDetailDTO.getGoodsTotal();
        this.freightTotalAmount = orderDetailDTO.getFreightTotal();
        this.actualFreightAmount = orderDetailDTO.getActualFreight();
        this.payAmount = orderDetailDTO.getPayPrice();
        this.scoreNum = orderDetailDTO.getScoreNum();
        this.scoreAmount = orderDetailDTO.getScore();
        this.balance = orderDetailDTO.getBalance();
        this.cardAmount = orderDetailDTO.getCardPrice();
        this.thirdPaymentAmount = orderDetailDTO.getThirdPaymentAmount();
        this.outerPaySn = orderDetailDTO.getOuterPaySn();
        this.haitao = orderDetailDTO.getHaitao();

        // 订单明细
        List<APIOrderSkuDTO> apiOrderSkuDTOS;
        List<OrderGoodsDTO> orderGoodsDTOS = orderDetailDTO.getOrderGoodsDTOS();
        if (CollectionUtils.isEmpty(orderGoodsDTOS)) {
            apiOrderSkuDTOS = Collections.emptyList();
        } else {
            apiOrderSkuDTOS = new ArrayList<>(orderGoodsDTOS.size());
            for (OrderGoodsDTO orderGoodsDTO : orderGoodsDTOS) {
                apiOrderSkuDTOS.add(new APIOrderSkuDTO(orderGoodsDTO));
            }
        }
        this.orderSkus = apiOrderSkuDTOS;

        // 订单收货信息
        OrderAddressDTO orderAddressDTO = orderDetailDTO.getOrderAddressDTO();
        this.receiverName = orderAddressDTO.getName();
        this.receiverProvince = orderAddressDTO.getProvince();
        this.receiverCity = orderAddressDTO.getCity();
        this.receiverCounty = orderAddressDTO.getCounty();
        this.receiverTelephone = orderAddressDTO.getTelephone();
        this.receiverDetailAddress = orderAddressDTO.getDetailAddress();
        this.buyerIdCard = orderAddressDTO.getMemberIdCard();
        this.buyerName = orderAddressDTO.getBuyerName();

        // 物流信息
        if (CollectionUtils.isNotEmpty(orderGoodsDTOS)) {
            this.expressComName = orderGoodsDTOS.get(0).getExpressComName();
            this.expressNo = orderGoodsDTOS.get(0).getExpressNo();
        }
        this.memo = orderDetailDTO.getMemo();
    }

}
