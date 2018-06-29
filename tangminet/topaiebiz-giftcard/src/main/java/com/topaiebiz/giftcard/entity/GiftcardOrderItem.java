package com.topaiebiz.giftcard.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 礼卡订单包含的礼卡数据
 * </p>
 *
 * @author Jeff Chen123
 * @since 2018-01-25
 */
@TableName("t_giftcard_order_item")
@Data
public class GiftcardOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 关联订单id
     */
    private Long orderId;

    /**
     * 关联卡批次id
     */
    private Long batchId;
    /**
     * 卡名称
     */
    private String cardName;
    /**
     * 该卡的标签id
     */
    private Long labelId;
    /**
     * 封面
     */
    private String cover;
    /**
     * 卡片数量
     */
    private Integer cardNum;
    /**
     * 面值
     */
    private BigDecimal faceValue;
    /**
     * 售价
     */
    private BigDecimal salePrice;
    /**
     * 逗号分隔的卡号串
     */
    private String cardNoList;
    /**
     * 添加时间
     */
    private Date createdTime;

}
