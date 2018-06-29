package com.topaiebiz.member.member.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.member.member.service.MemberService;
import com.topaiebiz.member.member.utils.HttpUtil;
import com.topaiebiz.member.member.utils.IpUtil;
import com.topaiebiz.member.po.*;
import com.topaiebiz.member.vo.LoginReturnVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * Description：会员管理控制层
 * <p>
 * Author Scott.Yang
 * <p>
 * Date 2017年9月26日 下午4:53:39
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping("/member")
public class MemberMobileController {

    @Autowired
    private MemberService memberService;

    /**
     * Description： 快速登录会员信息
     * <p>
     * Author Scott.Yang
     *
     * @param loginOrRegiseterPo 会员信息Dto
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/quickLogin", method = RequestMethod.POST)
    public ResponseInfo quickLogin(@RequestBody LoginOrRegiseterPo loginOrRegiseterPo, HttpServletRequest request) throws GlobalException {
        loginOrRegiseterPo.setIp(IpUtil.getIpAddr(request));
        return new ResponseInfo(memberService.doQuickLogin(loginOrRegiseterPo));
    }

    @RequestMapping(path = "/wechatH5Auth", method = RequestMethod.GET)
    public ResponseInfo wechatH5Auth(HttpServletResponse response) throws IOException {
        response.sendRedirect(memberService.getWechatH5AuthUrl());
        return new ResponseInfo();
    }

    @RequestMapping(path = "/wechatH5Auth/{redirectType}", method = RequestMethod.GET)
    public void wechatH5Auth(@PathVariable("redirectType") String redirectType, HttpServletResponse response) throws IOException {
        response.sendRedirect(memberService.getWechatH5AuthUrl(redirectType));
    }


    @RequestMapping(value = "/wechatH5Login/{code}", method = RequestMethod.GET)
    public ResponseInfo wechatH5Login(@PathVariable("code") String code, HttpServletRequest request) {
        LoginReturnVo loginReturnVo = memberService.doWechatH5Login(code, HttpUtil.getIp(request));
        return new ResponseInfo(loginReturnVo);
    }


    @MemberLogin
    @RequestMapping(value = "/wechatH5Bind/{code}", method = RequestMethod.GET)
    public ResponseInfo wechatH5Bind(@PathVariable("code") String code, HttpServletRequest request) {
        Boolean bindRet = memberService.doWechatH5Bind(MemberContext.getMemberId(), code, HttpUtil.getIp(request));
        return new ResponseInfo(bindRet);
    }

    @MemberLogin
    @RequestMapping(value = "/wechatH5Unbind/{code}", method = RequestMethod.GET)
    public ResponseInfo wechatH5Unbind(@PathVariable("code") String code, HttpServletRequest request) {
        Boolean unbindRet = memberService.doWechatH5Unbind(MemberContext.getMemberId(), code, HttpUtil.getIp(request));
        return new ResponseInfo(unbindRet);
    }

    @MemberLogin
    @RequestMapping(value = "/wechatAppBind", method = RequestMethod.POST)
    public ResponseInfo wechatAppBind(@RequestBody WechatUserPo wechatUserPo, HttpServletRequest request) {
        Boolean bindRet = memberService.doWechatAppBind(MemberContext.getMemberId(), wechatUserPo, HttpUtil.getIp(request));
        return new ResponseInfo(bindRet);
    }

    @MemberLogin
    @RequestMapping(value = "/wechatAppUnbind", method = RequestMethod.POST)
    public ResponseInfo wechatAppUnbind(@RequestBody WechatUserPo wechatUserPo, HttpServletRequest request) {
        Boolean unbindRet = memberService.doWechatAppUnbind(MemberContext.getMemberId(), wechatUserPo, HttpUtil.getIp(request));
        return new ResponseInfo(unbindRet);
    }


    /**
     * @此方法暂未使用
     */
    @RequestMapping(value = "/wechatAppLogin/{code}", method = RequestMethod.GET)
    public ResponseInfo wechatAppLogin(@PathVariable("code") String code, HttpServletRequest request) {
        LoginReturnVo loginReturnVo = memberService.doWechatAppLogin(code, HttpUtil.getIp(request));
        return new ResponseInfo(loginReturnVo);
    }

    /**
     * @此方法暂未使用
     */
    @RequestMapping(value = "/wechatAppLogin/{accessToken}/{openId}", method = RequestMethod.GET)
    public ResponseInfo wechatAppLogin(@PathVariable("accessToken") String accessToken, @PathVariable("openId") String openId, HttpServletRequest request) {
        LoginReturnVo loginReturnVo = memberService.doWechatAppLogin(accessToken, openId, HttpUtil.getIp(request));
        return new ResponseInfo(loginReturnVo);
    }

    @RequestMapping(value = "/wechatAppLogin", method = RequestMethod.POST)
    public ResponseInfo wechatAppLogin(@RequestBody WechatUserPo wechatUserPo, HttpServletRequest request) {
        LoginReturnVo loginReturnVo = memberService.doWechatAppLogin(wechatUserPo, HttpUtil.getIp(request));
        return new ResponseInfo(loginReturnVo);
    }


    /**
     * Description： 会员注册
     * <p>
     * Author Ward.Wang
     *
     * @param memberRegisterPo 会员信息Dto
     * @param result           错误结果
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ResponseInfo register(@RequestBody @Valid MemberRegisterPo memberRegisterPo, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        return new ResponseInfo(memberService.doRegisterMember(memberRegisterPo, IpUtil.getIpAddr(request)));
    }

    /**
     * Description：账号密码登录
     * Author Ward.Wang
     *
     * @param accountLoginPo
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/accountLogin", method = RequestMethod.POST)
    public ResponseInfo accountLogin(@RequestBody AccountLoginPo accountLoginPo, HttpServletRequest request) {
        LoginReturnVo loginReturnVo = memberService.doAcountLogin(accountLoginPo, HttpUtil.getIp(request));
        return new ResponseInfo(loginReturnVo);
    }


    /**
     * @param TelephoneLoginPo
     * @return
     * @throws GlobalException Description：手机验证码登录
     *                         Author Ward.Wang
     * @此方法暂未使用
     */
    @RequestMapping(path = "/telephoneLogin", method = RequestMethod.POST)
    public ResponseInfo telephoneLogin(@RequestBody TelephoneLoginPo TelephoneLoginPo, HttpServletRequest request) {
        TelephoneLoginPo.setIp(IpUtil.getIpAddr(request));
        LoginReturnVo loginReturnVo = memberService.doTelephoneLogin(TelephoneLoginPo);
        return new ResponseInfo(loginReturnVo);
    }

    /**
     * Description：登出
     * Author Ward.Wang
     *
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public ResponseInfo logout() {
        return new ResponseInfo(memberService.doLogout(MemberContext.getSessionId()));
    }

    /**
     * Description：签到
     * Author Ward.Wang
     *
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/checkin", method = RequestMethod.POST)
    public ResponseInfo checkin() {
        return new ResponseInfo(memberService.doCheckin(MemberContext.getCurrentMemberToken()));
    }

    /**
     * Description：检查是否签过到
     * Author Ward.Wang
     *
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/validateCheckin", method = RequestMethod.POST)
    public ResponseInfo validateCheckin() {
        return new ResponseInfo(memberService.validateCheckin(MemberContext.getMemberId()));
    }

    /**
     * Description：签到记录
     * Author Ward.Wang
     *
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/checkinLog/{lastLogId}", method = RequestMethod.POST)
    public ResponseInfo queryCheckinLog(@PathVariable Long lastLogId) {
        return new ResponseInfo(memberService.queryCheckinLog(MemberContext.getMemberId(), lastLogId));
    }


    /**
     * Description：发送会员模块相关的短信验证码
     * Author Ward.Wang
     *
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/captcha", method = RequestMethod.POST)
    public ResponseInfo sendCaptcha(@RequestBody SendCaptchaPo sendCaptchaPo, HttpServletRequest request) {
        if (StringUtils.isBlank(sendCaptchaPo.getTelephone())) {
            MemberTokenDto memberTokenDto = MemberContext.tryGetCurrentMemberToken();
            if (null != memberTokenDto) {
                sendCaptchaPo.setTelephone(memberTokenDto.getTelephone());
            }
        }
        return new ResponseInfo(memberService.sendCaptcha(sendCaptchaPo.getTelephone(), sendCaptchaPo.getType(), IpUtil.getIpAddr(request)));
    }


    /**
     * Description：验证手机验证码（用于 找回密码、重置手机号 等 会员模块相关的操作）
     * <p>
     * Author Scott.Yang
     *
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/verifyCaptcha", method = RequestMethod.POST)
    public ResponseInfo verifyCaptcha(@RequestBody VerifyCaptchaPo verifyCaptchaPo, HttpServletRequest request) {
        verifyCaptchaPo.setMemberId(MemberContext.tryGetMemberId());
        if (null == verifyCaptchaPo.getTelephone()) {
            MemberTokenDto memberTokenDto = MemberContext.tryGetCurrentMemberToken();
            if (null != memberTokenDto) {
                verifyCaptchaPo.setTelephone(memberTokenDto.getTelephone());
            }
        }
        return new ResponseInfo(memberService.verifyCaptcha(verifyCaptchaPo, IpUtil.getIpAddr(request)));
    }

    /**
     * Description：获得用户账号相关信息
     * Author Ward.Wang
     *
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/account", method = RequestMethod.POST)
    public ResponseInfo getMemberAccount() {
        return new ResponseInfo(memberService.getAccountInfo(MemberContext.getMemberId()));
    }

    /**
     * Description：获得用户个人中心相关信息
     * Author Ward.Wang
     *
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/center")
    public ResponseInfo getMemberCenter() {
        return new ResponseInfo(memberService.getMemberCenter(MemberContext.getMemberId()));
    }

    /**
     * Description：获得用户剔除账号相关信息
     * Author Ward.Wang
     *
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/info")
    public ResponseInfo getMemberInfo() {
        return new ResponseInfo(memberService.getMemberInfo(MemberContext.getMemberId()));
    }

    /**
     * Description： 修改会员信息（剔除密码等账号相关信息 例如：头像、昵称,）
     * <p>
     * Author Scott.Yang
     *
     * @param memberChangePo 会员信息Dto
     * @param result         错误结果
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public ResponseInfo updateMemberInfo(@RequestBody @Valid MemberChangePo memberChangePo, BindingResult result) {
        if (result.hasErrors()) {
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        return new ResponseInfo(memberService.modifyMemberInfo(memberChangePo, MemberContext.getMemberId()));
    }


    /**
     * Description：绑定手机号
     * <p> 1.账号登录时未绑定手机号  2.微信一键注册（未绑定手机号 也未设置密码） 3.登录后账号中心 更换手机号
     * Author Scott.Yang
     *
     * @param bindTelephonePo 会员信息Dto
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/bindTelephone", method = RequestMethod.POST)
    public ResponseInfo bindTelephone(@RequestBody @Valid BindTelephonePo bindTelephonePo, HttpServletRequest request) {
        // 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象。
        //return new ResponseInfo(memberService.bindTelphone(MemberContext.tryGetCurrentMemberToken(), bindTelephonePo));
        return new ResponseInfo(memberService.bindTelphoneNew(MemberContext.tryGetCurrentMemberToken(), bindTelephonePo,
                IpUtil.getIpAddr(request)));
    }

    /**
     * Description：重置登录密码
     * <p>
     * Author Scott.Yang
     *
     * @param resetMemberPwdPo 会员信息Dto
     * @param result           错误结果
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/resetMemberPwd", method = RequestMethod.POST)
    public ResponseInfo resetMemberPwd(@RequestBody @Valid ResetMemberPwdPo resetMemberPwdPo, BindingResult result) {
        Long memberId = MemberContext.tryGetMemberId();
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        return new ResponseInfo(memberService.resetMemberPassword(memberId, resetMemberPwdPo));
    }


    /**
     * Description：重置支付密码
     * <p>
     * Author Scott.Yang
     *
     * @param resetPayPwdPo 会员信息Dto
     * @param result        错误结果
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/resetPayPwd", method = RequestMethod.POST)
    public ResponseInfo resetPayPwd(@RequestBody @Valid ResetPayPwdPo resetPayPwdPo, BindingResult result) {
        if (result.hasErrors()) {
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        resetPayPwdPo.setTelephone(MemberContext.getCurrentMemberToken().getTelephone());
        return new ResponseInfo(memberService.resetPayPassword(MemberContext.getMemberId(), resetPayPwdPo));
    }

    /**
     * Description： 初始设置会员密码
     * <p>
     * Author Scott.Yang
     *
     * @param initMemberPwdPo 会员信息Dto
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/setMemberPwd", method = RequestMethod.POST)
    public ResponseInfo setMemberPwd(@RequestBody InitMemberPwdPo initMemberPwdPo) {
        return new ResponseInfo(memberService.setMemberPwd(initMemberPwdPo));

    }

    /**
     * Description：初始设置支付密码
     * <p>
     * Author Scott.Yang
     *
     * @param initPayPwdPo 会员信息Dto
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/setPayPwd", method = RequestMethod.POST)
    public ResponseInfo setPayPassword(@RequestBody InitPayPwdPo initPayPwdPo) {
        return new ResponseInfo(memberService.setPayPassword(MemberContext.getMemberId(), initPayPwdPo));
    }

    /**
     * 验证登录密码或者支付密码 用于后续的相关操作（账号安全的相关操作）
     *
     * !!!! 2018-03-13 注释掉
     *
     * @param validatePasswordPo
     * @return
     */
   /* @MemberLogin
    @RequestMapping(path = "/validatePwd", method = RequestMethod.POST)
    public ResponseInfo validatePwd(@RequestBody ValidatePasswordPo validatePasswordPo) {
        return new ResponseInfo(memberService.validateAllPwd(MemberContext.getMemberId(), validatePasswordPo));
    }*/


    @RequestMapping(path = "/checkTel/{telephone}", method = RequestMethod.POST)
    public ResponseInfo checkTelephone(@PathVariable("telephone") String telephone, HttpServletRequest request) {
        Boolean checkRet = memberService.checkTelephone(telephone, HttpUtil.getIp(request));
        return new ResponseInfo(checkRet);
    }

}
