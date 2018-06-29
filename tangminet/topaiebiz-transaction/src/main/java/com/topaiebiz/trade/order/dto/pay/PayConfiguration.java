package com.topaiebiz.trade.order.dto.pay;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/***
 * @author yfeng
 * @date 2018-01-19 12:46
 */
@Data
public class PayConfiguration {

    /**
     * 礼卡支付店铺黑名单
     */
    Set<Long> cardStoreBlackList = new HashSet<>();
}