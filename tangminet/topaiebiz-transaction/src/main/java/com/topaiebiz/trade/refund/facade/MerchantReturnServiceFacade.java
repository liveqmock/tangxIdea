package com.topaiebiz.trade.refund.facade;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.api.MerchantReturnApi;
import com.topaiebiz.merchant.dto.merchantReturn.MerchantReturnDTO;
import com.topaiebiz.trade.refund.exception.RefundOrderExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description 商家收货地址
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/1 20:11
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class MerchantReturnServiceFacade {

    @Autowired
    private MerchantReturnApi merchantReturnApi;

    public MerchantReturnDTO getStoreReturnAddress(Long storeId) {
        MerchantReturnDTO merchantReturnDTO = merchantReturnApi.getMerchantReturnInfo(storeId);
        if (null == merchantReturnDTO) {
            throw new GlobalException(RefundOrderExceptionEnum.MERCHANT_RETURN_ADDRESS_IS_NULL);
        }
        return merchantReturnDTO;
    }
}
