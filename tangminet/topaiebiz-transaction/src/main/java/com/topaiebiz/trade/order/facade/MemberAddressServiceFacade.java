package com.topaiebiz.trade.order.facade;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.member.api.AddressApi;
import com.topaiebiz.member.dto.address.MemberAddressDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-10 14:23
 */
@Component
@Slf4j
public class MemberAddressServiceFacade {

    @Autowired
    private AddressApi addressApi;

    public MemberAddressDto queryDefaultAddress(Long memberId) {
        MemberAddressDto addressDto = addressApi.queryDefaultAddress(memberId);
        log.info("addressApi.queryDefaultAddress({}) return:{}", memberId, JSON.toJSONString(addressDto));
        return addressDto;
    }

    public MemberAddressDto queryMemberAddress(Long memberId, Long addressId) {
        MemberAddressDto addressDto = addressApi.queryMemberAddress(memberId, addressId);
        log.info("addressApi.queryMemberAddress({}, {}) return:{}", memberId, JSON.toJSONString(addressDto));
        return addressDto;
    }
}