package com.topaiebiz.member.address.api.impl;


import com.topaiebiz.member.address.service.MemberAddressService;
import com.topaiebiz.member.api.AddressApi;
import com.topaiebiz.member.dto.address.MemberAddressDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by ward on 2018-01-03.
 */
@Component
@Slf4j
public class AddressApiImpl implements AddressApi {


    @Autowired
    private MemberAddressService memberAddressService;

    @Override
    public MemberAddressDto queryMemberAddress(Long memberId, Long addressId) {
        return memberAddressService.findMemberAddress(memberId, addressId);
    }

    @Override
    public MemberAddressDto queryDefaultAddress(Long memberId) {
        return memberAddressService.getDefaultAddress(memberId);
    }



}
