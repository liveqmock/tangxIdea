package com.topaiebiz.goods.category.backend.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * Description 商家可用后台类目
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年11月8日 下午7:43:20
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_goo_backend_merchant_category")
@Data
public class BackendMerchantCategoryEntity extends BaseBizEntity<Long> {

    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 5270397387870402848L;

    /**
     * 佣金比例。
     */
    private Double brokerageRatio;

    /**
     * 所属商家。
     */
    private Long merchantId;

    /**
     * 所属店铺。
     */
    private Long storeId;

    /**
     * 平台类目ID。
     */
    private Long categoryId;

    /**
     * 父级类目
     */
    private Long parentId;

    /**
     * 类目等级 (1 一级 2 二级 3 三级)。
     */
    private Integer level;

    /**
     * 商家类目审核是否通过，1为审核通过。0为待审核。
     */
    private Integer status;

    /**
     * 备注。
     */
    private String memo;

}
