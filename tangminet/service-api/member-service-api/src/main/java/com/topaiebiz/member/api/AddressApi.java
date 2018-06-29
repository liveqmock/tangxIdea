package com.topaiebiz.member.api;

import com.topaiebiz.member.dto.address.MemberAddressDto;

/**
 * Created by ward on 2018-01-03.
 */
public interface AddressApi {

     MemberAddressDto queryMemberAddress(Long memberId, Long addressId);

     MemberAddressDto queryDefaultAddress(Long memberId);
}
