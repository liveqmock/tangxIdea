package com.topaiebiz.giftcard.controller.app;

import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.giftcard.controller.AbstractController;
import com.topaiebiz.giftcard.entity.GiftcardGiven;
import com.topaiebiz.giftcard.enums.GiftcardExceptionEnum;
import com.topaiebiz.giftcard.service.GiftcardGivenService;
import com.topaiebiz.giftcard.vo.GivenInfoVO;
import com.topaiebiz.giftcard.vo.GivenReceiveReq;
import com.topaiebiz.member.api.MemberApi;
import com.topaiebiz.member.dto.member.LoginOrRegiseterDto;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.message.api.CaptchaApi;
import com.topaiebiz.message.util.CaptchaType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @description: 礼卡转赠
 * @author: Jeff Chen
 * @date: created in 下午3:57 2018/1/19
 */
@RestController
@RequestMapping(value = "/app/giftcard/given")
public class GiftcardGivenController extends AbstractController {

    @Autowired
    private GiftcardGivenService giftcardGivenService;

    @Autowired
    private CaptchaApi captchaApi;

    @Autowired
    private MemberApi memberApi;

    /**
     * 发送验证码
     * @param givenReceiveReq
     * @return
     */
    @RequestMapping("/captcha")
    public ResponseInfo sendCaptcha(HttpServletRequest request, @RequestBody GivenReceiveReq givenReceiveReq) {
        if (null == givenReceiveReq || StringUtils.isBlank(givenReceiveReq.getPhone())) {
            throw new GlobalException(GiftcardExceptionEnum.PHONE_ERROR);
        }
        return new ResponseInfo(captchaApi.sendCaptcha(givenReceiveReq.getPhone(), CaptchaType.UPDATE_PAY_PWD, getIp(request)));
    }

    /**
     * 进入转赠页面
     *
     * @return
     */
    @RequestMapping(value = "/go/{cardNo}",method = RequestMethod.POST)
    @MemberLogin
    public ResponseInfo go(@PathVariable String cardNo) {
        return new ResponseInfo(giftcardGivenService.getGiftcard4Given(cardNo, MemberContext.getMemberId()));
    }

    /**
     * 返回转赠链接id
     * @param givenInfoVO
     * @return
     */
    @RequestMapping(value = "/generate",method = RequestMethod.POST)
    @MemberLogin
    public ResponseInfo generate(@Valid @RequestBody GivenInfoVO givenInfoVO, BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        MemberTokenDto memberTokenDto = MemberContext.getCurrentMemberToken();
        GiftcardGiven giftcardGiven = new GiftcardGiven();
        BeanCopyUtil.copy(givenInfoVO, giftcardGiven);
        giftcardGiven.setMemberId(memberTokenDto.getMemberId());
        giftcardGiven.setMemberName(memberTokenDto.getUserName());

        GiftcardGiven given = giftcardGivenService.generate(giftcardGiven);
        if (null == given) {
            throw new GlobalException(GiftcardExceptionEnum.GIVEN_ERROR);
        }
        return new ResponseInfo(given.getLinkId());
    }

    /**
     * 取消转赠
     * @param linkId
     * @return
     */
    @RequestMapping("/cancel/{linkId}")
    @MemberLogin
    public ResponseInfo cancel(@PathVariable String linkId) {
        MemberTokenDto memberTokenDto = MemberContext.getCurrentMemberToken();
        GiftcardGiven giftcardGiven = new GiftcardGiven();
        giftcardGiven.setLinkId(linkId);
        giftcardGiven.setMemberId(memberTokenDto.getMemberId());
        return new ResponseInfo(giftcardGivenService.cancle(giftcardGiven));
    }

    /**
     * 进入转赠领取页
     * @param linkId
     * @return
     */
    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    public ResponseInfo detail(String linkId) {
        if (StringUtils.isBlank(linkId)) {
            return paramError();
        }
        return new ResponseInfo(giftcardGivenService.getByLinkId(linkId));
    }

    /**
     * 根据手机领取
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public ResponseInfo get(HttpServletRequest request, @Valid @RequestBody GivenReceiveReq givenReceiveReq, BindingResult result) {

        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        if (!captchaApi.verifyCaptcha(givenReceiveReq.getPhone(), givenReceiveReq.getValidCode(), CaptchaType.UPDATE_PAY_PWD)) {
            throw new GlobalException(GiftcardExceptionEnum.CAPTCHA_ERROR);
        }
        LoginOrRegiseterDto loginOrRegiseterDto = new LoginOrRegiseterDto();
        loginOrRegiseterDto.setIp(getIp(request));
        loginOrRegiseterDto.setMemberFrom("card");
        loginOrRegiseterDto.setTelephone(givenReceiveReq.getPhone());
        MemberTokenDto memberTokenDto = memberApi.doLoginOrRegiseter(loginOrRegiseterDto);
        if (null == memberTokenDto) {
            throw new GlobalException(GiftcardExceptionEnum.PHONE_ERROR);
        }
        GiftcardGiven given = new GiftcardGiven();
        given.setMemberId(memberTokenDto.getMemberId());
        given.setLinkId(givenReceiveReq.getLinkId());
        given.setDoneePhone(givenReceiveReq.getPhone());
        return new ResponseInfo(giftcardGivenService.getTheGiven(given));
    }
}
