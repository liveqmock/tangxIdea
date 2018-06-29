package com.topaiebiz.member.member.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.dto.member.*;
import com.topaiebiz.member.po.*;
import com.topaiebiz.member.vo.CheckinLogReturnVo;
import com.topaiebiz.member.vo.LoginReturnVo;

import java.util.HashMap;
import java.util.List;

public interface MemberService {

    PageInfo<MemberMgmtDto> getMemberList(PagePO pagePo, MemberFilterPo memberFilterPo);

    MemberDto getMemberDto(Long memberId);

    MemberMgmtDto getMemberMgmtDto(Long memberId);

    MemberMgmtDto getMemberMgmtDto(Long storeId, Long memberId);

    MemberAccountDto getAccountInfo(Long memberId);

    boolean forbiddenMember(Long memberId);

    LoginReturnVo doQuickLogin(LoginOrRegiseterPo loginOrRegiseterPo);

    LoginReturnVo doAcountLogin(AccountLoginPo memberLoginPo, String ip);

    LoginReturnVo doTelephoneLogin(TelephoneLoginPo TelephoneLoginPo);


    LoginReturnVo doRegisterMember(MemberRegisterPo memberRegisterPo, String ip);


    Boolean modifyMemberInfo(MemberChangePo memberChangePo, Long memberId);

    /**
     * Description： 根据id解除冻结会员状态
     * <p>
     * Author Scott.Yang
     *
     * @param memberId 会员编号ID
     * @throws GlobalException
     */
    boolean relieveMember(Long memberId) throws GlobalException;

    /**
     * Description：  会员增值情况视图(按年展示)
     * <p>
     * Author Scott.Yang
     *
     * @param memberStatisticsDto
     * @return
     * @throws Exception
     */
    List<MemberStatisticsDto> listMemberRecordByYear(MemberStatisticsDto memberStatisticsDto) throws Exception;

    /**
     * Description： 会员增值情况视图(按月展示)
     * <p>
     * Author Scott.Yang
     *
     * @param memberStatisticsDto
     * @return
     * @throws Exception
     */
    List<MemberStatisticsDto> listMemberRecordByMonths(MemberStatisticsDto memberStatisticsDto) throws Exception;

    /**
     * Description：验证手机验证码
     * <p>
     * Author Scott.Yang
     *
     * @param verifyCaptchaPo
     * @return
     * @throws GlobalException
     */
    String verifyCaptcha(VerifyCaptchaPo verifyCaptchaPo, String ip);

    LoginReturnVo bindTelphoneNew(MemberTokenDto currentMember, BindTelephonePo bindTelephonePo, String ip);

    /**
     * Description：绑定手机号（1.未登录成功后新绑定 2.登录成功后更换手机 3.登录成功后新绑定手机）
     * <p>
     * Author Scott.Yang
     *
     * @param currentMember
     * @param bindTelephonePo
     * @return
     * @throws GlobalException
     */
    LoginReturnVo bindTelphone(MemberTokenDto currentMember, BindTelephonePo bindTelephonePo);


    MemberDto getMemberDto(String telephone);

    /**
     * Description： 设置支付密码
     * <p>
     * Author Scott.Yang
     *
     * @param initPayPwdPo
     * @return
     */
    Boolean setPayPassword(Long memberId, InitPayPwdPo initPayPwdPo);

    /**
     * Description： 更改支付密码
     * <p>
     * Author Scott.Yang
     *
     * @return
     */
    Boolean resetPayPassword(Long memberId, ResetPayPwdPo resetPayPwdPo);

    Boolean resetMemberPassword(Long memberId, ResetMemberPwdPo resetMemberPwdPo);

    /**
     * Description： 会员增值情况视图(按年展示商家端)
     * <p>
     * Author Scott.Yang
     *
     * @param memberStatisticsDto
     * @return
     * @throws Exception
     */
    List<MemberStatisticsDto> listMemberRecordByYearOfBusiness(MemberStatisticsDto memberStatisticsDto) throws Exception;

    /**
     * Description： 会员增值情况视图(按月展示商家端)
     * <p>
     * Author Scott.Yang
     *
     * @param memberStatisticsDto
     * @return
     * @throws Exception
     */
    List<MemberStatisticsDto> listMemberRecordByMonthsOfBusiness(MemberStatisticsDto memberStatisticsDto) throws Exception;

    /**
     * Description: 绑定微信用户
     * <p>
     * Author: hxpeng
     * createTime: 2017/11/18
     *
     * @param:
     **/
    void bindWechat(String openId, Long memberId, String ip);

    MemberTokenDto getMemberToken(String sessionId);

    boolean hasPayPwd(Long memberId);

    boolean validatePayPwd(Long memberId, String payPwd);


    LoginReturnVo setMemberPwd(InitMemberPwdPo initMemberPwdPo);

    MemberDto getMemberByNameOrPhone(String var);


    /**
     * Description：下面为新写的接口实现
     */


    MemberDto getValidatedMember(String loginName, String memberPwd);

    List<MemberDto> getMemberList(List<Long> memberIds);

    HashMap<Long, MemberDto> getMemberMap(List<Long> memberIds);


    boolean sendCaptcha(String mobile, String captchaType, String ip);

    boolean sendCaptcha(MemberTokenDto memberTokenDto, SendCaptchaPo sendCaptchaPo, String ip);


    MemberCenterDto getMemberCenter(Long memberId);

    Boolean updateAccountState(Long memberId, Integer accountState);

    MemberTokenDto doLoginOrRegiseter(LoginOrRegiseterDto loginOrRegiseterDto, Boolean noSessionId);

    String validateAllPwd(Long memberId, ValidatePasswordPo validatePasswordPo);

    MemberInfoDto getMemberInfo(Long memberId);

    Boolean doLogout(String sessionId);

    String getWechatH5AuthUrl();

    String getWechatH5AuthUrl(String redirectType);

    LoginReturnVo doWechatH5Login(String code, String ip);

    LoginReturnVo doWechatAppLogin(String accessToken, String openId, String ip);

    LoginReturnVo doWechatAppLogin(WechatUserPo wechatUserPo, String ip);

    LoginReturnVo doWechatAppLogin(String code, String ip);

    Boolean doCheckin(MemberTokenDto memberTokenDto);

    Boolean validateCheckin(Long memberId);

    CheckinLogReturnVo queryCheckinLog(Long memberId, Long lastLogId);

    Boolean doWechatH5Bind(Long memberId, String code, String ip);

    Boolean doWechatH5Unbind(Long memberId, String code, String ip);

    Boolean doWechatAppBind(Long memberId, WechatUserPo wechatUserPo, String ip);

    Boolean doWechatAppUnbind(Long memberId, WechatUserPo wechatUserPo, String ip);

    Boolean checkTelephone(String telephone, String ip);
}

