package com.topaiebiz.transaction.order.merchant.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Description 订单明细实体类
 *
 * @author zhushuyong
 * @data 2017年8月31日 上午8:57:41
 */
@Data
@TableName("t_tsa_order_detail")
public class OrderDetailEntity extends BaseBizEntity<Long> {

    /**
     * 序列化版本号
     */
    @TableField(exist = false)
    private static final long serialVersionUID = -6142594948012770402L;

    /**
     * 订单id
     */
    private Long orderId;
    private Long memberId;
    /**
     * 订单状态
     */
    private Integer orderState;
    /**
     * 商品id
     */
    private Long itemId;

    /**
     * skuId
     */
    private Long skuId;

    /**
     * spuId
     */
    private Long spuId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品条形码
     */
    private String barCode;

    /**
     * 商品属性集
     */
    private String fieldValue;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 商品原单价
     */
    private BigDecimal goodsPrice;

    /**
     * 商品数量
     */
    private Long goodsNum;

    /**
     * 商品原总价
     */
    private BigDecimal totalPrice;

    /**
     * 所使用的营销活动
     */
    private Long promotionId;

    /**
     * 优惠金额
     */
    private BigDecimal discount;

    /**
     * 优惠数据详情(含店铺、平台、单品优惠详情)
     */
    private String promotionDetail;

    /**
     * 实际运费
     */
    private BigDecimal freight;

    /**
     * 优惠后应支付金额
     */
    private BigDecimal payPrice;

    /**
     * 支付详情(站内支付+站外支付详情)
     */
    private String payDetail;

    /**
     * 商品货号
     */
    private String goodsSerial;
    /**
     * 物流公司ID
     */
    private Long expressComId;

    /**
     * 物流公司名称
     */
    private String expressComName;

    /**
     * 单种商品的物流编号，如果分批次发可记录多个
     */
    private String expressNo;

    /**
     * 商品sku的基本属性
     */
    @TableField(exist = false)
    private String baseFieldValue;

    /**
     * 备注
     */
    private String memo;

    @TableField(exist = false)
    private String replyText;

    /** 佣金比例。小数形式。平台收取商家的佣金。*/
    private BigDecimal brokerageRatio;
    /**
     * 订单支付比例限制
     */
    private BigDecimal scoreRate;

    private BigDecimal taxRate;

    private String itemCode;

    /**
     * 发货时间
     */
    private Date shipmentTime;

    /**
     * 收货时间
     */
    private Date receiveTime;

    /**
     * 售后状态
     */
    private Integer refundState;

}