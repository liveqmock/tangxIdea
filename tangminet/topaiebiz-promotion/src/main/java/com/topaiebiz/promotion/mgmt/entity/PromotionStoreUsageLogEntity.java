package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Description 店铺活动使用记录
 * <p>
 * <p>
 * Author Joe
 * <p>
 * Date 2017年9月28日 上午9:51:50
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_pro_promotion_store_usage_log")
@Data
public class PromotionStoreUsageLogEntity extends BaseEntity<Long> {

    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 1653505660417794936L;

    /**
     * 商家订单编号
     */
    private Long orderId;

    /**
     * 营销活动
     */
    private Long promotionId;

    /**
     * 会员编号
     */
    private Long memberId;

    /**
     * 所属商家
     */
    private Long storeId;

    /**
     * 优惠金额
     */
    private BigDecimal price;

    /**
     * 备注
     */
    private String memo;

    private Long creatorId;
    private Date createdTime = new Date();

    public void clearInit() {
        this.createdTime = null;
        super.cleanInit();
    }
}
