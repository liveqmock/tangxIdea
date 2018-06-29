package com.topaiebiz.giftcard.controller.app;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.giftcard.controller.AbstractController;
import com.topaiebiz.giftcard.enums.BindWayEnum;
import com.topaiebiz.giftcard.service.GiftcardLogService;
import com.topaiebiz.giftcard.service.GiftcardUnitService;
import com.topaiebiz.giftcard.vo.CardBindVO;
import com.topaiebiz.giftcard.vo.MyGiftcardLogReq;
import com.topaiebiz.giftcard.vo.MyGiftcardReq;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @description: 我的美礼卡
 * @author: Jeff Chen
 * @date: created in 下午7:20 2018/1/23
 */
@RestController
@RequestMapping("/app/giftcard/mine")
public class MyGiftcardController extends AbstractController {

    @Autowired
    private GiftcardUnitService giftcardUnitService;

    @Autowired
    private GiftcardLogService giftcardLogService;
    /**
     * 可以礼卡总额
     * @return
     */
    @RequestMapping("/totalBalance")
    @MemberLogin
    public ResponseInfo totalBalance() {
        return new ResponseInfo(giftcardUnitService.totalBalance(MemberContext.getMemberId()));
    }

    /**
     * 我的可用和不可用礼卡列表
     *
     * @return
     */
    @RequestMapping("/cardList")
    @MemberLogin
    public ResponseInfo cardList(@RequestBody MyGiftcardReq giftcardReq) {
        if (null == giftcardReq || null == giftcardReq.getCategory()) {
            return paramError();
        }
        giftcardReq.setBindingMember(MemberContext.getMemberId());

        return new ResponseInfo(giftcardUnitService.getMyGiftcardBycategory(giftcardReq));
    }

    /**
     * 消费记录
     *
     * @return
     */
    @RequestMapping("/consumeLog")
    @MemberLogin
    public ResponseInfo consumeLog(@RequestBody MyGiftcardLogReq myGiftcardLogReq) {
        if (null == myGiftcardLogReq) {
            return paramError();
        }
        myGiftcardLogReq.setMemberId(MemberContext.getMemberId());
        return new ResponseInfo(giftcardLogService.queryMyGiftcardLog(myGiftcardLogReq));
    }

    /**
     * 绑定
     *
     * @param cardBindVO
     * @return
     */
    @RequestMapping("/bindCard")
    @MemberLogin
    public ResponseInfo bindCard(@Valid @RequestBody CardBindVO cardBindVO, BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        cardBindVO.setMemberId(MemberContext.getMemberId());
        cardBindVO.setBindWay(BindWayEnum.CARD_PWD.getWayId());
        return new ResponseInfo(giftcardUnitService.bindCard(cardBindVO));
    }
}
