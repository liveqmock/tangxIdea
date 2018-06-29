package com.topaiebiz.trade.order.dto.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Description 备注信息DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/17 15:41
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@NoArgsConstructor
public class OrderRemarkDTO implements Serializable {
    private static final long serialVersionUID = 6882326173286141320L;

    /**
     * 备注内容， 备注人名称， 操作时间
     */
    private String content;
    private String userName;
    private Date createTime;


    /**
    *
    * Description: 主要用于创建用户备注实体类的 构造器
    *
    * Author: hxpeng
    * createTime: 2018/4/17
    *
    * @param:
    **/
    public OrderRemarkDTO(String content, String userName, Date createTime) {
        this.content = content;
        this.userName = userName;
        this.createTime = createTime;
    }
}
