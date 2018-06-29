package com.topaiebiz.payment.controller;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BindResultUtil;
import com.nebulapaas.common.ServletRequestUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.payment.constants.PaymentConstants;
import com.topaiebiz.payment.dto.WeixinPayParamDTO;
import com.topaiebiz.payment.exception.PaymentExceptionEnum;
import com.topaiebiz.payment.service.WechatPayService;
import com.topaiebiz.payment.util.WeChatPayHelper;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.util.SecurityContextUtils;
import com.topaiebiz.thirdparty.config.WeChatConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Description 微信支付控制层
 * <p>
 * Author hxpeng
 * <p>
 */
@Slf4j
@Controller
@RequestMapping(value = "/payment/wechat")
public class WechatPayController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping(value = "/toPayByWeixinJSAPI/{orderPayId}", method = RequestMethod.GET)
    public void toPayByWeixinJSAPI(@PathVariable String orderPayId) throws IOException {
        response.setCharacterEncoding("UTF-8");
        String targetUrl;
        if (orderPayId.indexOf(PaymentConstants.UNDER_LINE) > 0){
            String[] cardOrderIdStr = orderPayId.split(PaymentConstants.UNDER_LINE);
            String suffix = cardOrderIdStr[1];
            if (StringUtils.isNotBlank(suffix) && suffix.equals(Constants.Order.ORDER_TYPE_CARD)){
                String orderId = cardOrderIdStr[0];
                targetUrl = WeChatConfig.buildRedirectUrl(orderId, WeChatConfig.CARD_PAY_AUTH_CALLBACK_URL);
                log.info("redirect {}", targetUrl);
                response.sendRedirect(targetUrl);
                return;
            }
        }
        targetUrl = WeChatConfig.buildRedirectUrl(orderPayId, WeChatConfig.PAY_AUTH_CALLBACK_URL);
        log.info("redirect {}", targetUrl);
        response.sendRedirect(targetUrl);
    }


    /**
     * Description: 拼装微信接口预支付参数
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/16
     **/
    @MemberLogin
    @ResponseBody
    @RequestMapping(value = "/buildPrepPaySign", method = RequestMethod.POST)
    public ResponseInfo buildPrepPaySign(@RequestBody @Valid WeixinPayParamDTO paramDTO, BindingResult bindingResult) {
        BindResultUtil.dealBindResult(bindingResult);
        if (StringUtils.isBlank(paramDTO.getCode())) {
            throw new GlobalException(PaymentExceptionEnum.UNABLE_TO_GET_USER_OPENID);
        }

        String openId = WeChatConfig.getOpenId(paramDTO.getCode());
        if (StringUtils.isBlank(openId)){
            throw new GlobalException(PaymentExceptionEnum.UNABLE_TO_GET_USER_OPENID);
        }
        paramDTO.setOpenId(openId);
        paramDTO.setIp(ServletRequestUtil.getIpAddress(request));
        return new ResponseInfo(wechatPayService.buildPrepPaySign(paramDTO));
    }

    /**
     * Description: 微信支付异步通知
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/16
     *
     * @param:
     **/
    @RequestMapping(value = "/payNotice", method = RequestMethod.POST)
    public void payNotice() throws IOException {
        log.info("----------request into wechat pay notice");
        String noticeXml = "";

        request.setCharacterEncoding("UTF-8");
        try (InputStream inputStream = request.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuilder buffer = new StringBuilder();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            noticeXml = buffer.toString();
            log.info("----------wechat pay notice :{}", noticeXml);
            Boolean result = wechatPayService.payNotice(noticeXml);
            log.info("----------wechat pay notice result:{}", result);
            response.getWriter().write(WeChatPayHelper.replynNotice(result));
            response.getWriter().flush();
        } catch (Exception e) {
            log.error("----------错误：{},接受异步通知失败，参数：{}", e.getMessage(), noticeXml);
        }
    }


    @ResponseBody
    @RequestMapping(value = "/refundResult", method = RequestMethod.POST)
    @PermissionController(value = PermitType.PLATFORM, operationName = "售后结果")
    public ResponseInfo refundResult(Long refundId){
        if (refundId == null){
            return null;
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        log.info(">>>>>>>>>>member：{}, has been request this path!", JSON.toJSONString(currentUserDto));
        return new ResponseInfo(wechatPayService.refundResult(refundId));
    }

}
