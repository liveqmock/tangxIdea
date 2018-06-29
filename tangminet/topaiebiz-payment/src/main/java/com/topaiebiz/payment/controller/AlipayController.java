package com.topaiebiz.payment.controller;

import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BindResultUtil;
import com.nebulapaas.common.ServletRequestUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.api.MemberApi;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.payment.config.AlipayConfig;
import com.topaiebiz.payment.constants.PaymentConstants;
import com.topaiebiz.payment.dto.PayParamDTO;
import com.topaiebiz.payment.exception.PaymentExceptionEnum;
import com.topaiebiz.payment.service.AlipayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Description 支付宝支付 控制层
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/15 16:30
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Controller
@RequestMapping(value = "/payment/alipay")
public class AlipayController {

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private MemberApi memberApi;

    /**
     * Description: 去支付
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/15
     *
     * @param:
     **/
    @RequestMapping(value = "/toPay")
    public void buildPayForm(@Valid PayParamDTO paramDTO, BindingResult bindingResult) throws IOException {
        MemberTokenDto memberTokenDto = null;
        if (StringUtils.isNotBlank(paramDTO.getSessionId())) {
            memberTokenDto = memberApi.getMemberToken(paramDTO.getSessionId());
        }
        if (null == memberTokenDto) {
            throw new GlobalException(PaymentExceptionEnum.FAILED_TO_BUILD_PREPPAY_SIGN);
        }
        BindResultUtil.dealBindResult(bindingResult);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        paramDTO.setIp(ServletRequestUtil.getIpAddress(request));

        PrintWriter pw = response.getWriter();
        pw.write(alipayService.buildPayForm(memberTokenDto.getMemberId(), paramDTO));
        pw.flush();
        pw.close();
    }

    /**
     * Description: APP - 去支付
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/23
     *
     * @param:
     **/
    @ResponseBody
    @RequestMapping(value = "/payForApp")
    public ResponseInfo payForApp(@Valid PayParamDTO paramDTO, BindingResult bindingResult) {
        MemberTokenDto memberTokenDto = null;
        if (StringUtils.isNotBlank(paramDTO.getSessionId())) {
            memberTokenDto = memberApi.getMemberToken(paramDTO.getSessionId());
        }
        if (null == memberTokenDto) {
            throw new GlobalException(PaymentExceptionEnum.FAILED_TO_BUILD_PREPPAY_SIGN);
        }
        BindResultUtil.dealBindResult(bindingResult);
        paramDTO.setIp(ServletRequestUtil.getIpAddress(request));
        return new ResponseInfo(alipayService.buildPaySignForApp(memberTokenDto.getMemberId(), paramDTO));
    }


    /**
     * Description: 支付宝同步通知
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/15
     **/
    @RequestMapping(value = "/paySyncNotice")
    public void paySyncNotice() throws IOException {
        boolean result = alipayService.payNotice(request);
        if (result) {
            //区分订单类型，设置不同的跳转路径
            String tradeNo = request.getParameter("out_trade_no");
            String suffix = tradeNo.split(PaymentConstants.UNDER_LINE)[1];
            String redirectUrl;
            if (suffix.equals(Constants.Order.ORDER_TYPE_CARD)) {
                redirectUrl = AlipayConfig.CARD_PAY_SUCCESS_URL;
            } else {
                redirectUrl = AlipayConfig.PAY_SUCCESS_URL;
            }
            response.sendRedirect(StringUtils.join(redirectUrl, "/", tradeNo.split(PaymentConstants.UNDER_LINE)[0]));
            return;
        }
        log.error("----------Alipay processing synchronization notification failed");
    }


    /**
     * Description: 支付宝异步通知
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/15
     **/
    @RequestMapping(value = "/payAsynNotice")
    public void payAsynNotice() throws IOException {
        PrintWriter pw = response.getWriter();
        boolean result = alipayService.payNotice(request);
        if (result) {
            pw.write("success");
        } else {
            pw.write("fail");
        }
        pw.flush();
        pw.close();
    }
}
