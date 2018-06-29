package com.topaiebiz.trade.dto.order;

import lombok.Data;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/16 19:24
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class PushOrderParamsDTO implements Serializable {
    private static final long serialVersionUID = -7972168320156728440L;
    private Long payId;
    /**
     * key：店铺ID, value 订单集合
     */
    private Map<Long, Set<Long>> orderIds;
    /**
     * 是否海淘
     */
    private boolean isHaiTao;
}