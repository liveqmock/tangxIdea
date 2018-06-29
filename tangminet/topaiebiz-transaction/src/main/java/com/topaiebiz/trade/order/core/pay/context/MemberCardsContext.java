package com.topaiebiz.trade.order.core.pay.context;

import com.topaiebiz.card.dto.MemberCardDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberCardsContext {

    private static final ThreadLocal<MemberCardDTO> context = new ThreadLocal<>();

    public static void set(MemberCardDTO data) {
        context.set(data);
    }

    public static MemberCardDTO get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}