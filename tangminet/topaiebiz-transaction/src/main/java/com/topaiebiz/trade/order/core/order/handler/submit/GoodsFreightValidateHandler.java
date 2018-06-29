package com.topaiebiz.trade.order.core.order.handler.submit;

import com.topaiebiz.trade.order.core.order.handler.common.GoodsFreightHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-15 19:04
 */
@Component("goodsFreightValidateHandler")
@Slf4j
public class GoodsFreightValidateHandler extends GoodsFreightHandler {

    @Override
    public boolean needValidateAddresSupport() {
        return true;
    }
}