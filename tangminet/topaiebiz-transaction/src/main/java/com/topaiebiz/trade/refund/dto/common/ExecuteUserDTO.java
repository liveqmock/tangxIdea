package com.topaiebiz.trade.refund.dto.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Description 审核人信息
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/2 11:47
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
@NoArgsConstructor
public class ExecuteUserDTO implements Serializable {

    private static final long serialVersionUID = -7353653543910862452L;

    /**
     * 操作人ID
     */
    private Long memberId;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 是否为平台角色
     */
    private boolean isFromPlatform = false;

    public ExecuteUserDTO(Long memberId) {
        this.memberId = memberId;
    }
}
