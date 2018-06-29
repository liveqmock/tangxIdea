package com.topaiebiz.trade.order.po.ordersubmit;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-09 11:08
 */
@Data
public class PageInitPO {
    private List<Long> cartIds;
    private Long goodsId;
    private Long goodsNum;

    public boolean cartMode() {
        return CollectionUtils.isNotEmpty(cartIds);
    }

    public boolean validGoodsMode() {
        return goodsId != null && goodsNum != null && goodsId > 0 && goodsNum > 0;
    }
}