package com.topaiebiz.openapi.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/2 13:53
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_openapi_order_refund")
public class OpenApiOrderRefundEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = -2898211595123855088L;

    private Long storeId;

    private Long orderId;

    private Integer state;

    private Integer pushCount;

    private String errorMessage;

    private Date pushTime;

    private Date createdTime;

    public OpenApiOrderRefundEntity() {
        this.state = 0;
        this.pushCount = 0;
        createdTime = new Date();
    }
}
