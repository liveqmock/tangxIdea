package com.topaiebiz.trade.order.core.order.handler.submit;

import com.topaiebiz.trade.order.core.order.handler.common.AddressLoadHanlder;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-10 16:27
 */
@Component("submitAddressValidateHandler")
public class AddressValidateHanlder extends AddressLoadHanlder {

    @Override
    protected boolean needValidate() {
        return true;
    }

}