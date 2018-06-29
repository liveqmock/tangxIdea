package com.topaiebiz.openapi.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_openapi_order_pay")
public class OpenApiOrderPayEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = -2898211595123855088L;

    private Long storeId;
    private Long orderId;
    private Long payId;
    private Integer state;
    private Integer pushCount;
    private String errorMessage;
    private Date pushTime;
    private Date createdTime;

    /**
     * 推送海关结果
     */
    private Integer pushPaymentStatus;
    private String pushPaymentRes;

    public OpenApiOrderPayEntity(Long payId) {
        this.payId = payId;
        this.state = 0;
        this.pushCount = 0;
        createdTime = new Date();
    }
}
