package com.topaiebiz.trade.order.util;

import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.trade.order.po.common.BuyerBO;

/***
 * @author yfeng
 * @date 2018-01-18 15:50
 */
public class BuyerBOUtil {

    public static BuyerBO buildBuyerBO(MemberTokenDto memberTokenDto) {
        BuyerBO buyerBO = new BuyerBO();
        buyerBO.setMemberId(memberTokenDto.getMemberId());
        buyerBO.setMobile(memberTokenDto.getTelephone());
        return buyerBO;
    }
}
