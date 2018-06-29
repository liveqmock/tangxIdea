package com.topaiebiz.trade.order.core.order.context;

import com.topaiebiz.member.dto.address.MemberAddressDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressContext {

    private static final ThreadLocal<MemberAddressDto> context = new ThreadLocal<>();

    public static void set(MemberAddressDto addressDto) {
        context.set(addressDto);
    }

    public static MemberAddressDto get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}