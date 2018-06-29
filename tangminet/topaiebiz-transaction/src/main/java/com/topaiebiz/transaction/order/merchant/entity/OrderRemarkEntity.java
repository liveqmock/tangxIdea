package com.topaiebiz.transaction.order.merchant.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * Description TODO
 *
 * @Author hxpeng
 * <p>
 * Date 2018/5/25 14:02
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@TableName("t_tsa_order_remark")
public class OrderRemarkEntity extends BaseEntity<Long> {

    private Long orderId;
    private Long memberId;
    private String memberName;
    private String remark;
    private Date createTime;


}
