package com.topaiebiz.transaction.cart.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * Description 购物车实体类
 * <p>
 * <p>
 * Author zhushuyong
 * <p>
 * Date 2017年9月7日 下午9:05:12
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@TableName("t_tsa_shopping_cart")
public class ShoppingCartEntity extends BaseEntity<Long> {

    /**
     * 版本化序列号
     */
    @TableField(exist = false)
    private static final long serialVersionUID = -4370849601660750182L;

    /**
     * 创建人编号。取值为创建人的全局唯一主键标识符。
     */
    private Long creatorId;

    /**
     * 创建时间。默认取值为系统的当前时间。
     */
    private Date createdTime = new Date();

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 商品sku表的id
     */
    private Long goodsId;

    /**
     * 数量
     */
    private Long goodsNum;

    /**
     * 店铺ID
     */
    private Long storeId;

    public void clearInit() {
        this.setCreatedTime(null);
        this.setVersion(null);
        this.setDeleteFlag(null);
    }
}
