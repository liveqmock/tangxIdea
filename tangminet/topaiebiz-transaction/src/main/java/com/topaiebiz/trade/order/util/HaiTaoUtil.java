package com.topaiebiz.trade.order.util;

import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.trade.constants.OrderConstants;

/***
 * @author yfeng
 * @date 2018-03-03 16:25
 */
public class HaiTaoUtil {
    public static boolean isHaitaoStore(StoreInfoDetailDTO store){
        return store != null && OrderConstants.HaitaoFlag.YES.equals(store.getHaitao());
    }

}
