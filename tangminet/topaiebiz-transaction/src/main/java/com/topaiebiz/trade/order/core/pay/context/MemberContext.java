package com.topaiebiz.trade.order.core.pay.context;

import com.topaiebiz.member.dto.member.MemberDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberContext {

    private static final ThreadLocal<MemberDto> context = new ThreadLocal<>();

    public static void set(MemberDto data) {
        context.set(data);
    }

    public static MemberDto get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}