package com.topaiebiz.member.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.MD5Util;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.nebulapaas.common.redis.aop.JedisOperation;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.basic.dto.DistrictDto;
import com.topaiebiz.card.dto.CardBalanceDTO;
import com.topaiebiz.member.constants.PointOperateType;
import com.topaiebiz.member.dto.grade.MemberGradeDto;
import com.topaiebiz.member.dto.member.*;
import com.topaiebiz.member.dto.point.MemberAssetDto;
import com.topaiebiz.member.dto.point.PointChangeDto;
import com.topaiebiz.member.exception.MemberExceptionEnum;
import com.topaiebiz.member.grade.service.MemberGradeService;
import com.topaiebiz.member.member.bo.WechatUserBo;
import com.topaiebiz.member.member.constants.DependOperateType;
import com.topaiebiz.member.member.constants.MemberCacheKey;
import com.topaiebiz.member.member.constants.ThirdAccountType;
import com.topaiebiz.member.member.dao.MemberCheckinDao;
import com.topaiebiz.member.member.dao.MemberMgmtDao;
import com.topaiebiz.member.member.entity.MemberCheckinEntity;
import com.topaiebiz.member.member.entity.MemberEntity;
import com.topaiebiz.member.member.facade.MemberServiceFacade;
import com.topaiebiz.member.member.service.MemberService;
import com.topaiebiz.member.member.third.WechatAppService;
import com.topaiebiz.member.member.third.WechatH5Service;
import com.topaiebiz.member.member.utils.IdGeneratorUtil;
import com.topaiebiz.member.member.utils.MemberUtil;
import com.topaiebiz.member.member.utils.RegexUtil;
import com.topaiebiz.member.po.*;
import com.topaiebiz.member.point.service.MemberPointService;
import com.topaiebiz.member.reserve.dao.MemberBindAccountDao;
import com.topaiebiz.member.reserve.entity.MemberBindAccountEntity;
import com.topaiebiz.member.vo.CheckinLogReturnVo;
import com.topaiebiz.member.vo.LoginReturnVo;
import com.topaiebiz.message.util.CaptchaType;
import com.topaiebiz.system.util.SecurityContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.*;

/*import com.topaiebiz.system.moble.security.dto.TokenDto;
import com.topaiebiz.system.moble.security.service.TokenService;
import com.topaiebiz.system.moble.security.util.MD5Util;
import com.topaiebiz.system.moble.security.util.TokenUtil;
import com.topaiebiz.system.security.dao.SystemUserDao;
import com.topaiebiz.system.security.entity.SystemUserEntity;
import com.topaiebiz.system.security.util.SecurityContextUtils;*/


/**
 * Description： 会员信息实现类
 * <p>
 * <p>
 * Author Scott.Yang
 * <p>
 * Date 2017年9月26日 下午8:11:51
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Service
@Transactional
public class MemberServiceImpl extends ServiceImpl<MemberMgmtDao, MemberEntity> implements MemberService {

    @Value("${point.add.register}")
    private Integer registerAddPoint = 1;
    @Value("${point.add.checkin}")
    private Integer checkinAddPoint = 1;

    private Integer captchaErrLimit = 4;
    private Integer loginErrLimit = 4;
    private Integer payPwdErrLimit = 4;
    private Integer memberPwdErrLimit = 4;

    private static final Long DEFAULT_GRADE_ID = 1L;
    private static final Long DEFAULT_UPGRADE_SCORE = 0L;
    private static final Integer DEFAULT_ACCOUNT_STATE = 0;
    private static final Integer ACCOUNT_STATE_LOCK = 1;
    private static final Integer ACCOUNT_STATE_NORMAL = 0;
    private static final String DEFAULT_PASSWORD = " ";//注意 由于框架因素 ""无法插入数据 只能改为" "
    private static final String DEPEND_OPERATE_SET_PWD = "SET_PWD";
    private static final Integer LOGIN_SESSION_EXPIRE = 86400 * 7;

    @Autowired
    private MemberCheckinDao memberCheckinDao;
    @Autowired
    private MemberMgmtDao memberMgmtDao;
    @Autowired
    private MemberServiceFacade memberServiceFacade;
    @Autowired
    private MemberBindAccountDao memberBindAccountDao;
    @Autowired
    private MemberPointService memberPointService;
    @Autowired
    private MemberGradeService memberGradeService;


    @Override
    public PageInfo<MemberMgmtDto> getMemberList(PagePO pagePo, MemberFilterPo memberFilterPo) {
        Page<MemberMgmtDto> page = PageDataUtil.buildPageParam(pagePo);
        List<MemberMgmtDto> memberMgmtDtoList = memberMgmtDao.selectMemberMgmt(page, memberFilterPo);
        if (CollectionUtils.isEmpty(memberMgmtDtoList)) {
            page.setRecords(memberMgmtDtoList);
            PageInfo<MemberMgmtDto> pageInfo = PageDataUtil.copyPageInfo(page);
            return pageInfo;
        }
        List<Long> memberIdList = MemberUtil.extractMemberIdList(memberMgmtDtoList);
        Map<Long, MemberAssetDto> memberAssetDtoMap = memberPointService.getMemberAssetMap(memberIdList);
        List<Long> gradeIdList = MemberUtil.extractGradeIdList(memberMgmtDtoList);
        Map<Long, MemberGradeDto> memberGradeDtoMap = memberGradeService.getMemberGradeMap(gradeIdList);
        List<MemberMgmtDto> returntList = new ArrayList<>();
        for (MemberMgmtDto memberMgmtDto : memberMgmtDtoList) {
            CardBalanceDTO cardBalanceDto = memberServiceFacade.getCardBalance(memberMgmtDto.getId());
            MemberAssetDto memberAssetDto = (null == memberAssetDtoMap) ? null : memberAssetDtoMap.get(memberMgmtDto.getId());
            MemberGradeDto memberGradeDto = (null == memberGradeDtoMap) ? null : memberGradeDtoMap.get(memberMgmtDto.getGradeId());
            returntList.add(MemberUtil.packageMemberMgmt(memberMgmtDto, memberAssetDto, cardBalanceDto, memberGradeDto));
        }
        page.setRecords(returntList);
        PageInfo<MemberMgmtDto> pageInfo = PageDataUtil.copyPageInfo(page);
        return pageInfo;
    }

    @Override
    public MemberMgmtDto getMemberMgmtDto(Long memberId) {
        MemberMgmtDto memberMgmtDto = new MemberMgmtDto();
        MemberEntity memberEntity = getMember(memberId);
        if (null == memberEntity) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        BeanCopyUtil.copy(memberEntity, memberMgmtDto);
        CardBalanceDTO cardBalanceDto = memberServiceFacade.getCardBalance(memberMgmtDto.getId());
        MemberAssetDto memberAssetDto = memberPointService.getMemberAsset(memberMgmtDto.getId());
        MemberGradeDto memberGradeDto = memberGradeService.getMemberGrade(memberMgmtDto.getGradeId());
        /**根据区id查询市名称*/
        if (memberMgmtDto.getDistrictId() > 0) {
            DistrictDto districtDto = memberServiceFacade.getDistrict(memberMgmtDto.getDistrictId());
            DistrictDto cityDistricDto = memberServiceFacade.getDistrict(districtDto.getParentDistrictId());
            memberMgmtDto.setAddress(cityDistricDto.getParentDistrictName()
                    + cityDistricDto.getFullName() + districtDto.getFullName() + memberMgmtDto.getAddress());
        }
        //红包数量
        memberMgmtDto.setCouponNum(memberServiceFacade.getAllCouponNum(memberId));
        return MemberUtil.packageMemberMgmt(memberMgmtDto, memberAssetDto, cardBalanceDto, memberGradeDto);
    }

    @Override
    public MemberMgmtDto getMemberMgmtDto(Long storeId, Long memberId) {
        if (null == storeId || storeId <= 0) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        //TODO  验证该店铺是否有权限查看 该用户信息

        MemberMgmtDto memberMgmtDto = new MemberMgmtDto();
        MemberEntity memberEntity = getMember(memberId);
        if (null == memberEntity) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        BeanCopyUtil.copy(memberEntity, memberMgmtDto);

        /**根据区id查询市名称*/
        if (memberMgmtDto.getDistrictId() > 0) {
            DistrictDto districtDto = memberServiceFacade.getDistrict(memberMgmtDto.getDistrictId());
            DistrictDto cityDistricDto = memberServiceFacade.getDistrict(districtDto.getParentDistrictId());
            memberMgmtDto.setAddress(cityDistricDto.getParentDistrictName()
                    + cityDistricDto.getFullName() + districtDto.getFullName() + memberMgmtDto.getAddress());
        }
        //红包数量
        memberMgmtDto.setCouponNum(memberServiceFacade.getStoreCouponNum(storeId, memberId));
        return memberMgmtDto;
    }

    @Override
    public MemberDto getMemberDto(Long memberId) {
        MemberEntity memberEntity = getMember(memberId);
        if (null == memberEntity) {
            return null;
        }
        MemberDto memberDto = new MemberDto();
        BeanCopyUtil.copy(memberEntity, memberDto);
        return memberDto;
    }


    private static final Integer ACCOUNT_TYPE_WX = 1;

    @Override
    public MemberAccountDto getAccountInfo(Long memberId) {
        MemberEntity memberEntity = getMember(memberId);
        if (null == memberEntity) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        MemberBindAccountEntity wxAccount = getMemberBindAccount(ThirdAccountType.WX, memberId);
        MemberBindAccountEntity qqAccount = getMemberBindAccount(ThirdAccountType.QQ, memberId);
        MemberAccountDto memberAccountDto = MemberUtil.packageMemberAccount(memberEntity,
                wxAccount, qqAccount);
        return memberAccountDto;
    }

    @Override
    @JedisOperation
    public boolean forbiddenMember(Long memberId) throws GlobalException {
        return updateAccountState(memberId, ACCOUNT_STATE_LOCK);
    }

    @Override
    @JedisOperation
    public LoginReturnVo doQuickLogin(LoginOrRegiseterPo loginOrRegiseterPo) {
        //1.考虑三种情况 （注册、登录已设置密码、登录未设置密码）
        String telephone = loginOrRegiseterPo.getTelephone();
        String ip = loginOrRegiseterPo.getIp();
        tryRedisLock(telephone);
        MemberEntity memberEntity = getMember(telephone);
        Boolean isRegister;
        Boolean hasSetPwd = false;
        if (null != memberEntity) {
            isRegister = false;
            //TODO ?? 待处理？？
            hasSetPwd = StringUtils.isNotBlank(memberEntity.getPassword());
        } else {
            isRegister = true;
        }
        //校验短信验证码
        CaptchaType captchaType = isRegister ? CaptchaType.REGISTER : CaptchaType.LOGIN;
        checkCaptchaErrCount(telephone, captchaType.getType(), ip);
        try {
            if (!memberServiceFacade.verifyCaptcha(telephone, loginOrRegiseterPo.getCaptcha(), captchaType)) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_CAPTCHA_ERR);
            }
        } catch (GlobalException e) {
            incrCaptchaErrCount(telephone, captchaType.getType());
            throw e;
        }
        LoginOrRegiseterDto loginOrRegiseterDto = new LoginOrRegiseterDto();
        BeanCopyUtil.copy(loginOrRegiseterPo, loginOrRegiseterDto);
        Boolean noSessionId = !hasSetPwd;
        MemberTokenDto memberTokenDto = doLoginOrRegiseter(loginOrRegiseterDto, noSessionId);
        String setPwdCode = "";
        if (noSessionId) {
            setPwdCode = IdGeneratorUtil.generatorUUID();
            setDependOperateCode(telephone, DependOperateType.SET_PWD.name() + setPwdCode);
        } else {
            //TODO
        }
        LoginReturnVo loginReturnVo = new LoginReturnVo();
        loginReturnVo.setSessionId(memberTokenDto.getSessionId());
        loginReturnVo.setIsRegister(isRegister);
        loginReturnVo.setHasSetPwd(hasSetPwd);
        loginReturnVo.setSetPwdCode(setPwdCode);
        return loginReturnVo;
    }

    public void setDependOperateCode(String key, String value) {
        Jedis jedis = JedisContext.getJedis();
        jedis.setex(MemberCacheKey.MEMBER_DEPEND_OPERATE_PREFIX + key, 9000, value);
    }

    private Boolean checkDependOperateCode(String key, String value) {
        Jedis jedis = JedisContext.getJedis();
        return value.equals(jedis.get(MemberCacheKey.MEMBER_DEPEND_OPERATE_PREFIX + key));
    }

    private static String getUsername(int len) {
        // 字符源，可以根据需要删减
        String generateSource = "0123456789abcdefghigklmnopqrstuvwxyz_";
        String rtnStr = "";
        for (int i = 0; i < len; i++) {
            // 循环随机获得当次字符，并移走选出的字符
            String nowStr = String
                    .valueOf(generateSource.charAt((int) Math.floor(Math.random() * generateSource.length())));
            rtnStr += nowStr;
            generateSource = generateSource.replaceAll(nowStr, "");
        }
        return rtnStr;
    }


    @Override
    @JedisOperation
    public LoginReturnVo doRegisterMember(MemberRegisterPo memberRegisterPo, String ip) {
        String telephone = memberRegisterPo.getTelephone();
        String password = memberRegisterPo.getPassword();
        if (StringUtils.isBlank(telephone)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_USERNAME_NOT_NULL);
        }
        if (StringUtils.isBlank(password)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_PASSWORD_NOT_NULL);
        }
        MemberEntity member = getMemberByMobileOrName(telephone);
        if (null != member) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_PHONENUMBER_EXIST);
        }
        //memberServiceFacade.verifyCaptcha(telephone, memberRegisterPo.getCaptcha(), CaptchaType.REGISTER);
        MemberEntity memberEntity = register(telephone, password, ip);
        return doLoginCheckMember(memberEntity, ip);
    }

    @Override
    public Boolean modifyMemberInfo(MemberChangePo memberChangePo, Long memberId) {
        MemberEntity param = new MemberEntity();
        BeanCopyUtil.copy(memberChangePo, param);
        param.cleanInit();
        param.setId(memberId);
        param.setLastModifierId(memberId);
        param.setLastModifiedTime(new Date());
        return memberMgmtDao.updateById(param) > 0;
    }

    @Override
    @JedisOperation
    public boolean relieveMember(Long memberId) {
        return updateAccountState(memberId, ACCOUNT_STATE_NORMAL);
    }

    @Override
    public List<MemberStatisticsDto> listMemberRecordByYear(MemberStatisticsDto memberStatisticsDto) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (memberStatisticsDto.getRegisterTimeStr() == null) {
            memberStatisticsDto.setRegisterTime(sdf.parse("2017-01-01"));
        } else {
            memberStatisticsDto.setRegisterTime(sdf.parse(memberStatisticsDto.getRegisterTimeStr()));
        }
        return memberMgmtDao.selectMemberRecordByYear(memberStatisticsDto);
    }

    @Override
    public List<MemberStatisticsDto> listMemberRecordByMonths(MemberStatisticsDto memberStatisticsDto) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        memberStatisticsDto.setRegisterTime(sdf.parse(memberStatisticsDto.getRegisterTimeStr()));
        return memberMgmtDao.selectMemberRecordByMonths(memberStatisticsDto);
    }


    @Autowired
    private RedisCache redisCache;

    private void checkPaypwdErrCount(String memberId) {
        String key = MemberCacheKey.MEMBER_PAYPWD_ERR_COUNT_PREFIX + memberId;
        String captchaErrCount = redisCache.get(key);
        if (null != captchaErrCount) {
            if (Integer.parseInt(captchaErrCount) >= payPwdErrLimit) {
                log.error("checkPaypwdErrCount:当日支付密码错误次数超限：memberId={}", memberId);
                throw new GlobalException(MemberExceptionEnum.MEMBER_PAYPWD_ERR_LIMIT);
            }
        }
    }

    private void incrPaypwdErrCount(String memberId) {
        Integer restSecond = MemberUtil.getResetSecond();
        String key = MemberCacheKey.MEMBER_PAYPWD_ERR_COUNT_PREFIX + memberId;
        String captchaErrCount = redisCache.get(key);
        if (null == captchaErrCount) {
            redisCache.set(key, 1);
            redisCache.expire(key, restSecond);
        } else {
            redisCache.incr(key);
        }
    }

    private void checkMemberpwdErrCount(String memberId) {
        String key = MemberCacheKey.MEMBER_MEMBERPWD_ERR_COUNT_PREFIX + memberId;
        String captchaErrCount = redisCache.get(key);
        if (null != captchaErrCount) {
            if (Integer.parseInt(captchaErrCount) >= memberPwdErrLimit) {
                log.error("checkMemberpwdErrCount:当日会员密码错误次数超限：memberId={}", memberId);
                throw new GlobalException(MemberExceptionEnum.MEMBER_MEMBERPWD_ERR_LIMIT);
            }
        }
    }

    private void incrMemberpwdErrCount(String memberId) {
        Integer restSecond = MemberUtil.getResetSecond();
        String key = MemberCacheKey.MEMBER_MEMBERPWD_ERR_COUNT_PREFIX + memberId;
        String captchaErrCount = redisCache.get(key);
        if (null == captchaErrCount) {
            redisCache.set(key, 1);
            redisCache.expire(key, restSecond);
        } else {
            redisCache.incr(key);
        }
    }


    private void checkLoginErrCount(String loginName, String ip) {
        String key = MemberCacheKey.MEMBER_LOGIN_ERR_COUNT_PREFIX + loginName;
        String captchaErrCount = redisCache.get(key);
        if (null != captchaErrCount) {
            if (Integer.parseInt(captchaErrCount) >= loginErrLimit) {
                log.error("checkLoginErrCount:当日密码错误次数超限：loginName={},ip={}", loginName, ip);
                throw new GlobalException(MemberExceptionEnum.MEMBER_LOGIN_ERR_LIMIT);
            }
        }
    }

    private void incrLoginErrCount(String loginName) {
        Integer restSecond = MemberUtil.getResetSecond();
        String key = MemberCacheKey.MEMBER_LOGIN_ERR_COUNT_PREFIX + loginName;
        String captchaErrCount = redisCache.get(key);
        if (null == captchaErrCount) {
            redisCache.set(key, 1);
            redisCache.expire(key, restSecond);
        } else {
            redisCache.incr(key);
        }
    }


    private void checkCaptchaErrCount(String subKey, String captchaType, String ip) {
        String key = MemberCacheKey.MEMBER_CAPTCHA_ERR_COUNT_PREFIX + subKey + "_" + captchaType;
        String captchaErrCount = redisCache.get(key);
        if (null != captchaErrCount) {
            if (Integer.parseInt(captchaErrCount) >= captchaErrLimit) {
                log.error("checkCaptchaErrCount:当日验证码错误次数超限：memberId or telephone={},captchaType={},ip={}", subKey, captchaType, ip);
                throw new GlobalException(MemberExceptionEnum.MEMBER_CAPTCHA_ERR_LIMIT);
            }
        }
    }


    private void incrCaptchaErrCount(String subKey, String captchaType) {
        Integer restSecond = MemberUtil.getResetSecond();
        String key = MemberCacheKey.MEMBER_CAPTCHA_ERR_COUNT_PREFIX + subKey + "_" + captchaType;
        String captchaErrCount = redisCache.get(key);
        if (null == captchaErrCount) {
            redisCache.set(key, 1);
            redisCache.expire(key, restSecond);
        } else {
            redisCache.incr(key);
        }
    }


    /**
     * 会员模块--统一验证手机验证码接口（仅验证操作和业务操作不在同一个页面进行的）
     * <p>
     * <p>
     * 不支持：1注册 2登录 4绑定手机
     * 仅支持：3找回密码（目前仅该操作不用登录）  5更新会员密码 6更新支付密码 8更换手机前通知
     *
     * @param verifyCaptchaPo
     * @return
     */
    @Override
    @JedisOperation
    public String verifyCaptcha(VerifyCaptchaPo verifyCaptchaPo, String ip) {
        String captcha = verifyCaptchaPo.getCaptcha();
        String type = verifyCaptchaPo.getType();
        String telephone = verifyCaptchaPo.getTelephone();
        Long memberId = verifyCaptchaPo.getMemberId();
        if (StringUtils.isEmpty(captcha)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_CAPTCHA_NOT_NULL);
        }
        if (!RegexUtil.checkMobile(telephone)) {
            throw new GlobalException(MemberExceptionEnum.MB_MOBILE_ERR);
        }
        tryRedisLock(telephone);
        if (null != memberId) {
            checkCaptchaErrCount(memberId.toString(), type, ip);
        }
        checkCaptchaErrCount(telephone.toString(), type, ip);

        try {
            Boolean verifyCaptchaRet = memberServiceFacade.verifyCaptcha(telephone, captcha, CaptchaType.get(type));
            if (!verifyCaptchaRet) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_CAPTCHA_ERR);
            }
        } catch (GlobalException e) {
            incrCaptchaErrCount(telephone.toString(), type);
            if (null != memberId) {
                incrCaptchaErrCount(memberId.toString(), type);
            }
            throw e;
        }
        String operateCode = IdGeneratorUtil.generatorUUID();
        if (type.equals(CaptchaType.FIND_PWD.getType())) {
            setDependOperateCode(telephone, DependOperateType.get(type).name() + operateCode);
        } else {

            if (null == memberId) {
                throw new GlobalException(MemberExceptionEnum.NOT_LOGIN);
            }
            setDependOperateCode(memberId.toString(), DependOperateType.get(type).name() + operateCode);
        }
        return operateCode;
    }

    @Override
    @JedisOperation
    public String validateAllPwd(Long memberId, ValidatePasswordPo validatePasswordPo) {
        MemberEntity memberEntity = getMember(memberId);
        if (null == memberEntity) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        //优先判断登录密码，如果登录密码为空  则判断支付密码
        if (StringUtils.isBlank(validatePasswordPo.getMemberPwd())) {
            if (StringUtils.isBlank(validatePasswordPo.getPayPwd())) {
                //会员密码 和登录密码 均为空
                throw new GlobalException(MemberExceptionEnum.MEMBER_PASSWORD_PARAM_ERR);
            }
            checkPaypwdErrCount(memberId.toString());
            String payPwd = validatePasswordPo.getPayPwd();
            if (!MD5Util.encode(payPwd).equals(memberEntity.getPayPassword())) {
                //TODO 需要增加 错误次数限制，防止暴力破解
                incrPaypwdErrCount(memberId.toString());
                throw new GlobalException(MemberExceptionEnum.MEMBER_PAYPASSWORD_ERR);
            }
        } else {
            checkMemberpwdErrCount(memberId.toString());
            String memberPwd = validatePasswordPo.getMemberPwd();
            if (!MD5Util.encode(memberPwd).equals(memberEntity.getPassword())) {
                //TODO 需要增加 错误次数限制，防止暴力破解
                incrMemberpwdErrCount(memberId.toString());
                throw new GlobalException(MemberExceptionEnum.MEMBER_PASSWORD_ERROR);
            }
        }
        String operateCode = IdGeneratorUtil.generatorUUID();
        String value = DependOperateType.get(validatePasswordPo.getType()).name() + operateCode;
        setDependOperateCode(memberId.toString(), value);
        return operateCode;
    }

    /**
     * 绑定手机号(重构)  三种类型：
     * --第一种类型
     * 1.1微信临时登录 后绑定手机操作（重构后的微信登录时 在member表中不产生信息，只有在后续的绑定手机操作时才 在member表中产生信息）
     * --第二种类型
     * 未真正登录类型的绑手机  此种类型存在两种场景
     * 2.1.账号登录未绑定手机
     * 2.2.微信登录历史遗留问题（微信登录后，已经在member表中产生了信息但是未绑定手机）
     * --第三种类型
     * 3.1登录后在个人中心 更换手机号的绑定手机
     *
     * @param currentMember
     * @param bindTelephonePo
     * @return
     */
    @Override
    @JedisOperation
    public LoginReturnVo bindTelphoneNew(MemberTokenDto currentMember, BindTelephonePo bindTelephonePo, String ip) {
        //验证 新手机号是否已经使用
        String newTelophone = bindTelephonePo.getNewTelephone();
        if (!RegexUtil.checkMobile(newTelophone)) {
            throw new GlobalException(MemberExceptionEnum.MB_MOBILE_ERR);
        }
        MemberEntity member = getMemberByMobileOrName(newTelophone);
        if (null != member) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_PHONENUMBER_EXIST);
        }

        // 判断验证码是否一致
        if (!memberServiceFacade.verifyCaptcha(newTelophone, bindTelephonePo.getCaptcha(), CaptchaType.BIND_PHONE)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_CAPTCHA_ERR);
        }
        Long memberId;
        if (null == currentMember) {
            //此处的loginName 可以为 用户名（手机号）、第三方登录的thirdUnionid
            String loginName = bindTelephonePo.getLoginName();
            if (StringUtils.isBlank(loginName)) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_BIND_PARM_ERR);
            }
            MemberEntity memberEntity = getMemberByMobileOrName(loginName);
            if (null == memberEntity) {
                String thirdUnionid = loginName;
                MemberBindAccountEntity bindAccountEntity = getBindAccount(ACCOUNT_TYPE_WX, thirdUnionid);
                if (null == bindAccountEntity) {
                    throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
                } else {
                    //第一种
                    // 微信临时登录 后绑定手机操作
                    // （重构后的微信登录时 在member表中不产生信息，只有在后续的绑定手机操作时才 在member表中产生信息）
                    if (!checkDependOperateCode(thirdUnionid, DependOperateType.BIND_PHONE.name() + bindTelephonePo.getOperateCode())) {
                        throw new GlobalException(MemberExceptionEnum.MEMBER_OPERATE_CODE_ERR);
                    }
                    memberEntity = register(newTelophone, null, ip);
                    updateBindAccount(bindAccountEntity, memberEntity, ip);
                    return doLoginCheckMember(memberEntity, ip);
                }
            } else {
                //第二种类型
                // 未真正登录类型的绑手机  此种类型存在两种场景
                // 1.账号登录未绑定手机
                // 2.微信登录历史遗留问题（微信登录后，已经在member表中产生了信息但是未绑定手机）
                memberId = memberEntity.getId();
                if (!checkDependOperateCode(memberId.toString(), DependOperateType.BIND_PHONE.name() + bindTelephonePo.getOperateCode())) {
                    throw new GlobalException(MemberExceptionEnum.MEMBER_OPERATE_CODE_ERR);
                }
                if (!updateTelephone(newTelophone, memberId)) {
                    throw new GlobalException(MemberExceptionEnum.MEMBER_BIND_TELEPHONE_FAIL);
                }
                memberEntity.setTelephone(newTelophone);
                return doLoginCheckMember(memberEntity, ip);
            }
        } else {
            //第三种类型  登录后在个人中心 更换手机号的绑定手机
            memberId = currentMember.getMemberId();
            if (!checkDependOperateCode(memberId.toString(), DependOperateType.CHANGE_MOBILE.name() + bindTelephonePo.getOperateCode())) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_OPERATE_CODE_ERR);
            }
            if (!updateTelephone(newTelophone, memberId)) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_BIND_TELEPHONE_FAIL);
            }
            // 更换完手机号 需要同步到用户
            if (StringUtils.isNotBlank(currentMember.getTelephone())) {
                memberServiceFacade.editSysUserTelephone(currentMember.getTelephone(), newTelophone);
            }
            //登录状态下 更换手机号成功时 更新redis 但是返回空对象
            currentMember.setTelephone(newTelophone);
            loginOperateRedis(memberId, currentMember.getSessionId(), currentMember);
            return new LoginReturnVo();
        }
    }

    /**
     * 绑定手机号  （1已经登录：之前未绑定  2.已经登录：更换绑定手机 3未登录：绑定手机号）
     *
     * @param currentMember
     * @param bindTelephonePo
     * @return
     */
    @Override
    @JedisOperation
    public LoginReturnVo bindTelphone(MemberTokenDto currentMember, BindTelephonePo bindTelephonePo) {
        //验证 新手机号是否已经使用
        String newTelophone = bindTelephonePo.getNewTelephone();
        if (!RegexUtil.checkMobile(newTelophone)) {
            throw new GlobalException(MemberExceptionEnum.MB_MOBILE_ERR);
        }
        MemberEntity member = getMemberByMobileOrName(newTelophone);
        if (null != member) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_PHONENUMBER_EXIST);
        }

        Long memberId;
        Boolean generateSessionIdFlag = false;
        Boolean hasSetPwd = true;
        MemberEntity memberEntity = null;
        if (null == currentMember) {
            if (StringUtils.isBlank(bindTelephonePo.getLoginName())) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_BIND_PARM_ERR);
            }
            memberEntity = getMemberByMobileOrName(bindTelephonePo.getLoginName());
            if (null == memberEntity) {
                throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
            }
            memberId = memberEntity.getId();
            if (!checkDependOperateCode(memberId.toString(),
                    DependOperateType.BIND_PHONE.name() + bindTelephonePo.getOperateCode())) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_OPERATE_CODE_ERR);
            }
            if (StringUtils.isBlank(memberEntity.getPassword())) {
                hasSetPwd = false;
            } else {
                generateSessionIdFlag = true;
            }
        } else {
            memberId = currentMember.getMemberId();
            //判断当前手机号是否为空，不为空则是 更换手机
            if (StringUtils.isNotBlank(currentMember.getTelephone())) {
                if (!checkDependOperateCode(memberId.toString(),
                        DependOperateType.CHANGE_MOBILE.name() + bindTelephonePo.getOperateCode())) {
                    throw new GlobalException(MemberExceptionEnum.MEMBER_OPERATE_CODE_ERR);
                }
            }
        }

        /** 判断验证码是否一致*/
        if (!memberServiceFacade.verifyCaptcha(newTelophone, bindTelephonePo.getCaptcha(), CaptchaType.BIND_PHONE)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_CAPTCHA_ERR);
        }

        if (!updateTelephone(newTelophone, memberId)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_BIND_TELEPHONE_FAIL);
        }

        // 更换完手机号 需要同步到用户
        if (null != currentMember && StringUtils.isNotBlank(currentMember.getTelephone())) {
            memberServiceFacade.editSysUserTelephone(currentMember.getTelephone(), newTelophone);
        }
        //更换完手机号 需要更新redis缓存


        if (generateSessionIdFlag) {
            //未登录状态下 绑定手机号，如果已经设置密码，则返回sessionId
            String sessionId = IdGeneratorUtil.generatorUUID();
            MemberTokenDto memberTokenDto = MemberUtil.packageMemberToken(sessionId, memberEntity);
            loginOperateRedis(memberEntity.getId(), sessionId, memberTokenDto);
            LoginReturnVo loginReturnVo = new LoginReturnVo();
            loginReturnVo.setSessionId(sessionId);
            loginReturnVo.setHasBindTel(true);
            return loginReturnVo;
        } else if (!hasSetPwd) {
            //微信、qq一键登录操作 绑定手机号，未设置密码，则返回setPwdCode
            String setPwdCode = IdGeneratorUtil.generatorUUID();
            setDependOperateCode(newTelophone, DependOperateType.SET_PWD.name() + setPwdCode);
            LoginReturnVo loginReturnVo = new LoginReturnVo();
            loginReturnVo.setHasSetPwd(hasSetPwd);
            loginReturnVo.setSetPwdCode(setPwdCode);
            loginReturnVo.setTelephone(newTelophone);
            loginReturnVo.setUserName(memberEntity.getUserName());
            return loginReturnVo;
        } else {
            //登录状态下 更换手机号成功时 更新redis 但是返回空对象
            currentMember.setTelephone(newTelophone);
            loginOperateRedis(memberId, currentMember.getSessionId(), currentMember);
            return new LoginReturnVo();
        }
    }

    private Boolean updateTelephone(String newTelophone, Long memberId) {
        MemberEntity param = new MemberEntity();
        param.cleanInit();
        param.setId(memberId);
        param.setTelephone(newTelophone);
        param.setLastModifierId(memberId);
        param.setLastModifiedTime(new Date());
        return memberMgmtDao.updateById(param) > 0;
    }


    private MemberEntity getMember(Long memberId) {
        if (memberId <= 0) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_ID_NOT_NULL);
        }
        MemberEntity memberEntity = memberMgmtDao.selectById(memberId);
        if (null == memberEntity ||
                memberEntity.getDeletedFlag() == Constants.DeletedFlag.DELETED_YES) {
            return null;
        }
        return memberEntity;
    }


    private MemberEntity getMember(String mobile) {
        if (!RegexUtil.checkMobile(mobile)) {
            throw new GlobalException(MemberExceptionEnum.MB_MOBILE_ERR);
        }
        MemberEntity memberEntity = null;
        try {
            MemberEntity param = new MemberEntity();
            param.cleanInit();
            param.setTelephone(mobile);
            param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            memberEntity = memberMgmtDao.selectOne(param);
        } catch (Exception e) {
            log.error("会员信息异常,该手机号存在多条会员信息mobile={}", mobile);
            throw new GlobalException(MemberExceptionEnum.MEMBER_INFO_MULTI);
        }
        return memberEntity;
    }


    private MemberEntity getMemberByMobileOrName(String nameOrMobile) {
        MemberEntity memberEntity = null;
        try {
            memberEntity = memberMgmtDao.getMemberByMobileOrName(nameOrMobile);
        } catch (Exception e) {
            log.error("会员信息异常,该手机号或用户名存在多条会员信息nameOrMobile={}", nameOrMobile);
            throw new GlobalException(MemberExceptionEnum.MEMBER_INFO_MULTI);
        }
        return memberEntity;
    }


    @Override
    public MemberDto getMemberDto(String telephone) {
        MemberEntity memberEntity = getMember(telephone);
        if (null == memberEntity) {
            return null;
        }
        MemberDto memberDto = new MemberDto();
        BeanCopyUtil.copy(memberEntity, memberDto);
        return memberDto;
    }

    @Override
    @JedisOperation
    public Boolean setPayPassword(Long memberId, InitPayPwdPo initPayPwdPo) {
        String payPassword = initPayPwdPo.getPayPassword();
        if (!RegexUtil.checkPaypwd(payPassword)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_PAYPASSWORD_FORMAT_ERR);
        }
        MemberEntity memberEntity = getMember(memberId);
        if (null == memberEntity) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        if (StringUtils.isNotBlank(memberEntity.getPayPassword())) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_PAYPASSWORD_EXIT);
        }
        if (!checkDependOperateCode(memberId.toString(),
                DependOperateType.UPDATE_PAY_PWD.name() + initPayPwdPo.getOperateCode())) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_OPERATE_CODE_ERR);
        }
        return updatePayPwd(memberEntity, payPassword);
    }


    @Override
    public List<MemberStatisticsDto> listMemberRecordByYearOfBusiness(MemberStatisticsDto memberStatisticsDto) throws Exception {
        //Long storeId = SecurityContextUtils.getCurrentSystemUser().getStoreId();
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        memberStatisticsDto.setStoreId(storeId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (memberStatisticsDto.getRegisterTimeStr() == null) {
            memberStatisticsDto.setRegisterTime(sdf.parse("2017-01-01"));
        } else {
            memberStatisticsDto.setRegisterTime(sdf.parse(memberStatisticsDto.getRegisterTimeStr()));
        }
        return memberMgmtDao.selectMemberRecordByYearOfBusiness(memberStatisticsDto);
    }

    @Override
    public List<MemberStatisticsDto> listMemberRecordByMonthsOfBusiness(MemberStatisticsDto memberStatisticsDto) throws Exception {
        // Long storeId = SecurityContextUtils.getCurrentSystemUser().getStoreId();
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        memberStatisticsDto.setStoreId(storeId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        memberStatisticsDto.setRegisterTime(sdf.parse(memberStatisticsDto.getRegisterTimeStr()));
        return memberMgmtDao.selectMemberRecordByMonthsOfBusiness(memberStatisticsDto);
    }


    @Override
    public void bindWechat(String openId, Long memberId, String ip) {
        MemberEntity memberEntity = memberMgmtDao.selectById(memberId);
        if (memberEntity == null) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_USER_NO_EXISTENCE);
        }
        MemberBindAccountEntity memberBindAccountEntity = new MemberBindAccountEntity();
        memberBindAccountEntity.setMemberId(memberId);
        memberBindAccountEntity.setAccountType(1);
        memberBindAccountEntity.setPlatformAccount(openId);
        memberBindAccountEntity.setTelephone(memberEntity.getTelephone());
        memberBindAccountEntity.setBindingTime(new Date());
        memberBindAccountEntity.setBindingIp(ip);
        memberBindAccountDao.insert(memberBindAccountEntity);

    }


    /**
     * --------------------------------新增或者重构之后的方法--------------------------------------------------------------
     */
    @Override
    @JedisOperation
    public LoginReturnVo doAcountLogin(AccountLoginPo accountLoginPo, String ip) {
        String loginName = accountLoginPo.getLoginName();
        /** 验证用户名是否为空 */
        if (StringUtils.isBlank(loginName)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_USERNAME_NOT_NULL);
        }
        /** 验证密码是否为空 */
        if (StringUtils.isBlank(accountLoginPo.getPassword())) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_PASSWORD_NOT_NULL);
        }
        checkLoginErrCount(loginName, ip);
        String password = MD5Util.encode(accountLoginPo.getPassword());

        MemberEntity memberEntity = getMemberByMobileOrName(loginName);
        if (null == memberEntity) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        if (ACCOUNT_STATE_LOCK.equals(memberEntity.getAccountState())) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_DISABLED);
        }
        if (!password.equals(memberEntity.getPassword())) {
            incrLoginErrCount(loginName);
            throw new GlobalException(MemberExceptionEnum.MB_NAME_PWD_ERROR);
        }
        String sessionId = "";
        String bindTelCode = "";
        Boolean hasBindTel;
        if (StringUtils.isNotBlank(memberEntity.getTelephone())) {
            sessionId = IdGeneratorUtil.generatorUUID();
            MemberTokenDto memberTokenDto = MemberUtil.packageMemberToken(sessionId, memberEntity);
            loginOperateRedis(memberEntity.getId(), sessionId, memberTokenDto);
            hasBindTel = true;
        } else {
            bindTelCode = IdGeneratorUtil.generatorUUID();
            setDependOperateCode(memberEntity.getId().toString(), DependOperateType.BIND_PHONE.name() + bindTelCode);
            hasBindTel = false;
        }
        updateLoginInfo(memberEntity, ip);
        LoginReturnVo loginReturnVo = new LoginReturnVo();
        loginReturnVo.setSessionId(sessionId);
        loginReturnVo.setHasBindTel(hasBindTel);
        loginReturnVo.setBindTelCode(bindTelCode);
        loginReturnVo.setUserName(memberEntity.getUserName());
        loginReturnVo.setHasSetPwd(true);
        return loginReturnVo;
    }

    public Boolean lockAccountOperateRedis(Long memberId) {
        Jedis jedis = JedisContext.getJedis();
        String key = MemberCacheKey.MEMBER_ID_SESSION_PREFIX + memberId;
        Long len = jedis.llen(key);
        List<String> sessionIds = jedis.lrange(key, 0, len);
        if (CollectionUtils.isNotEmpty(sessionIds)) {
            for (String sessionId : sessionIds) {
                String sessionKey = MemberCacheKey.MEMBER_LOGIN_SESSION_PREFIX + sessionId;
                jedis.del(sessionKey);
            }
            jedis.del(key);
        }
        return true;
    }


    private Boolean loginOperateRedis(Long memberId, String sessionId, MemberTokenDto memberTokenDto) {
        try {

            Jedis jedis = JedisContext.getJedis();
            String key = MemberCacheKey.MEMBER_ID_SESSION_PREFIX + memberId;
            jedis.lpush(key, sessionId);
            jedis.expire(key, LOGIN_SESSION_EXPIRE);
           /* String originSessionId = jedis.get(MemberCacheKey.MEMBER_ID_SESSION_PREFIX + memberId);
            if (null != originSessionId) {
                jedis.del(MemberCacheKey.MEMBER_LOGIN_SESSION_PREFIX + originSessionId);
            }
            jedis.set(MemberCacheKey.MEMBER_LOGIN_SESSION_PREFIX + sessionId, JSON.toJSONString(memberTokenDto));
            jedis.set(MemberCacheKey.MEMBER_ID_SESSION_PREFIX + memberId, sessionId);*/
            jedis.setex(MemberCacheKey.MEMBER_LOGIN_SESSION_PREFIX + sessionId, LOGIN_SESSION_EXPIRE, JSON.toJSONString(memberTokenDto));
        } catch (Exception e) {

        } finally {
            return true;
        }
    }

    private Boolean tryRedisLock(String var) {
        Jedis jedis = JedisContext.getJedis();
        String ret = jedis.set(MemberCacheKey.MEMBER_LOCK_PREFIX + var, "lock", "nx", "ex", 2);
        if ("OK".equalsIgnoreCase(ret)) {
            return true;
        } else {
            throw new GlobalException(MemberExceptionEnum.MEMBER_OPERATE_LATER);
        }
    }


    @Override
    @JedisOperation
    public LoginReturnVo doTelephoneLogin(TelephoneLoginPo telephoneLoginPo) {
        //1.验证短信验证码
        //TODO
        if (!memberServiceFacade.verifyCaptcha(telephoneLoginPo.getTelephone(), telephoneLoginPo.getCaptcha(), CaptchaType.LOGIN)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_CAPTCHA_ERR);
        }

        MemberEntity memberEntity = getMemberByMobileOrName(telephoneLoginPo.getTelephone());
        if (null == memberEntity) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        if (ACCOUNT_STATE_LOCK == memberEntity.getAccountState()) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_DISABLED);
        }

        //TODO
        //更新登录信息到表中
        updateLoginInfo(memberEntity, telephoneLoginPo.getIp());

        String sessionId = IdGeneratorUtil.generatorUUID();
        MemberTokenDto memberTokenDto = MemberUtil.packageMemberToken(sessionId, memberEntity);
        //操作redis缓存
        loginOperateRedis(memberEntity.getId(), sessionId, memberTokenDto);

        LoginReturnVo loginReturnVo = new LoginReturnVo();
        loginReturnVo.setSessionId(sessionId);
        return loginReturnVo;

    }

    @Override
    @JedisOperation
    public Boolean doLogout(String sessionId) {
        Jedis jedis = JedisContext.getJedis();
        return jedis.del(MemberCacheKey.MEMBER_LOGIN_SESSION_PREFIX + sessionId) > 0;
    }


    private boolean updateLoginInfo(MemberEntity memberEntity, String ip) {
        MemberEntity param = new MemberEntity();
        param.cleanInit();
        param.setId(memberEntity.getId());
        param.setLastLoginIp(ip);
        param.setLastLoginTime(new Date());
        Long loginCount = (null == memberEntity.getLoginCount()) ? 0 : memberEntity.getLoginCount();
        param.setLoginCount(loginCount + 1);
        param.setVersion(memberEntity.getVersion());
        return memberMgmtDao.updateById(param) > 0;
    }


    @Override
    @JedisOperation
    public MemberTokenDto getMemberToken(String sessionId) {
        if (StringUtils.isBlank(sessionId)) {
            return null;
        }
        Jedis jedis = JedisContext.getJedis();
        String data = jedis.get(MemberCacheKey.MEMBER_LOGIN_SESSION_PREFIX + sessionId);
        if (StringUtils.isBlank(data)) {
            jedis.del(MemberCacheKey.MEMBER_LOGIN_SESSION_PREFIX + sessionId);
            return null;
        } else {
            String memberTokenJson = jedis.get(MemberCacheKey.MEMBER_LOGIN_SESSION_PREFIX + sessionId);
            MemberTokenDto memberTokenDto = JSON.parseObject(memberTokenJson, MemberTokenDto.class);
            if (null == memberTokenDto || null == memberTokenDto.getMemberId()) {
                jedis.del(MemberCacheKey.MEMBER_LOGIN_SESSION_PREFIX + sessionId);
                return null;
            } else {
                jedis.expire(MemberCacheKey.MEMBER_LOGIN_SESSION_PREFIX + sessionId, LOGIN_SESSION_EXPIRE);
                return memberTokenDto;
            }
        }
    }

    @Override
    public boolean hasPayPwd(Long memberId) {
        MemberEntity member = memberMgmtDao.selectById(memberId);
        if (null != member) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_ID_NOT_EXIST);
        }
        return StringUtils.isNotBlank(member.getPayPassword());
    }

    @Override
    public boolean validatePayPwd(Long memberId, String payPwd) {
        MemberEntity member = memberMgmtDao.selectById(memberId);
        if (null == member) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_ID_NOT_EXIST);
        }
        if (StringUtils.isBlank(member.getPayPassword())) {
            return false;
        }
        checkPaypwdErrCount(memberId.toString());
        if (member.getPayPassword().equals(MD5Util.encode(payPwd))) {
            return true;
        } else {
            incrPaypwdErrCount(memberId.toString());
            return false;
        }
    }


    @Override
    public MemberDto getValidatedMember(String loginName, String memberPwd) {
        //MemberEntity memberEntity = memberMgmtDao.selectMemberByTelephone(loginName);
        MemberEntity memberEntity = getMemberByMobileOrName(loginName);
        if (null == memberEntity) {
            return null;
        }
        String memberPwdEncode = MD5Util.encode(memberPwd);
        if (memberPwdEncode.equals(memberEntity.getPassword())) {
            MemberDto memberDto = new MemberDto();
            BeanUtils.copyProperties(memberEntity, memberDto);
            return memberDto;
        } else {
            return null;
        }
    }


    @Override
    public List<MemberDto> getMemberList(List<Long> memberIds) {
        if (CollectionUtils.isEmpty(memberIds)) {
            return null;
        }
        EntityWrapper<MemberEntity> condition = new EntityWrapper<>();
        condition.in("id", memberIds);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<MemberEntity> memberEntityList = memberMgmtDao.selectList(condition);
        if (null == memberEntityList) {
            return null;
        }
        List<MemberDto> memberDtoList = new ArrayList<>();
        for (MemberEntity memberEntity : memberEntityList) {
            MemberDto memberDto = new MemberDto();
            BeanCopyUtil.copy(memberEntity, memberDto);
            memberDtoList.add(memberDto);
        }
        return memberDtoList;
    }


    @Override
    public HashMap<Long, MemberDto> getMemberMap(List<Long> memberIds) {
        EntityWrapper<MemberEntity> condition = new EntityWrapper<>();
        condition.in("id", memberIds);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<MemberEntity> memberEntityList = memberMgmtDao.selectList(condition);
        if (null == memberEntityList) {
            return null;
        }
        HashMap<Long, MemberDto> memberDtoMap = new HashMap<>();
        for (MemberEntity memberEntity : memberEntityList) {
            MemberDto memberDto = new MemberDto();
            BeanCopyUtil.copy(memberEntity, memberDto);
            memberDtoMap.put(memberEntity.getId(), memberDto);
        }
        return memberDtoMap;
    }

    @Override
    @JedisOperation
    public boolean sendCaptcha(MemberTokenDto memberTokenDto, SendCaptchaPo sendCaptchaPo, String ip) {
        String type = sendCaptchaPo.getType();
        CaptchaType captchaType = CaptchaType.get(type);
        switch (captchaType) {
            case LOGIN_REGISTER:
            case LOGIN:
            case REGISTER:
            case BIND_PHONE_NOSESSION:
            case FIND_PWD:
                break;
            case BIND_PHONE:
            case UPDATE_PAY_PWD:
            case UPDATE_PWD:
            case UPDATE_WITHDRAW_PWD:
            case CHANGE_MOBILE:
                //如果该手机号已经存在 则不能被绑定 或 注册

            default:
                //TODO
                break;
        }
        return true;
    }

    @Override
    @JedisOperation
    public boolean sendCaptcha(String mobile, String type, String ip) {
        tryRedisLock(mobile);

        if (!RegexUtil.checkMobile(mobile)) {
            throw new GlobalException(MemberExceptionEnum.MB_MOBILE_ERR);
        }
        MemberEntity memberEntity = null;
        CaptchaType captchaType = CaptchaType.get(type);

        //登录或注册 的类型 需要分别转成具体的模块
        if (captchaType.equals(CaptchaType.LOGIN_REGISTER)) {
            memberEntity = getMember(mobile);
            if (null == memberEntity) {
                captchaType = CaptchaType.REGISTER;
            } else {
                captchaType = CaptchaType.LOGIN;
            }
        }

        switch (captchaType) {
            case LOGIN:
                memberEntity = getMemberByMobileOrName(mobile);
                if (null == memberEntity) {
                    throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
                }
                if (ACCOUNT_STATE_LOCK.equals(memberEntity.getAccountState())) {
                    throw new GlobalException(MemberExceptionEnum.MEMBER_FROZEN);
                }

                break;
            case REGISTER:
            case BIND_PHONE:
                //如果该手机号已经存在 则不能被绑定 或 注册
                memberEntity = getMemberByMobileOrName(mobile);
                if (null != memberEntity) {
                    throw new GlobalException(MemberExceptionEnum.MB_USER_EXIT);
                }
                break;


            default:
                //TODO
                break;
        }
        return memberServiceFacade.sendCaptcha(mobile, captchaType, ip, null == memberEntity ? 0L : memberEntity.getId());

    }


    @Override
    @JedisOperation
    public Boolean resetPayPassword(Long memberId, ResetPayPwdPo resetPayPwdPo) {
        //1.验证密码
        String payPassword = resetPayPwdPo.getPayPassword();
        if (!RegexUtil.checkPaypwd(payPassword)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_PAYPASSWORD_FORMAT_ERR);
        }
        //2.验证短信验证码
        String telephone = resetPayPwdPo.getTelephone();
        if (StringUtils.isNotBlank(resetPayPwdPo.getCaptcha())) {
            if (!memberServiceFacade.verifyCaptcha(telephone, resetPayPwdPo.getCaptcha(), CaptchaType.UPDATE_PAY_PWD)) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_CAPTCHA_ERR);
            }
        } else {
            if (!checkDependOperateCode(memberId.toString(),
                    DependOperateType.UPDATE_PAY_PWD.name() + resetPayPwdPo.getOperateCode())) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_OPERATE_CODE_ERR);
            }
        }


        MemberEntity memberEntity = getMember(memberId);
        if (null == memberEntity || !telephone.equals(memberEntity.getTelephone())) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        return updatePayPwd(memberEntity, payPassword);
    }

    private Boolean updatePayPwd(MemberEntity memberEntity, String payPassword) {
        MemberEntity param = new MemberEntity();
        param.cleanInit();
        param.setId(memberEntity.getId());
        param.setPayPassword(MD5Util.encode(payPassword));
        param.setLastModifierId(memberEntity.getId());
        param.setLastModifiedTime(new Date());
        param.setVersion(memberEntity.getVersion());
        return memberMgmtDao.updateById(param) > 0;
    }

    /**
     * 重置密码（找回密码不用登录，更新密码要登录）
     *
     * @param memberId
     * @param resetMemberPwdPo
     * @return
     */
    @Override
    @JedisOperation
    public Boolean resetMemberPassword(Long memberId, ResetMemberPwdPo resetMemberPwdPo) {
        //1.验证密码
        String password = resetMemberPwdPo.getPassword();
        if (!RegexUtil.checkPassword(password)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_PASSWORD_FORMAT_ERR);
        }
        //2.检验code
        String captchaType = resetMemberPwdPo.getType();
        String value = DependOperateType.get(captchaType).name() + resetMemberPwdPo.getOperateCode();
        MemberEntity memberEntity;
        if (captchaType.equals(DependOperateType.FIND_PWD.type)) {
            if (!checkDependOperateCode(resetMemberPwdPo.getTelephone(), value)) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_OPERATE_CODE_ERR);
            }
            memberEntity = getMember(resetMemberPwdPo.getTelephone());
        } else {
            if (!checkDependOperateCode(memberId.toString(), value)) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_OPERATE_CODE_ERR);
            }
            memberEntity = getMember(memberId);
        }
        if (null == memberEntity) {
            log.warn("重置会员密码异常:会员不存在,memberId={}", memberId);
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        if (!updatePwd(password, memberEntity)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_RESET_PASSWORD_FAIL);
        }
        if (StringUtils.isNotBlank(memberEntity.getTelephone())) {
            memberServiceFacade.editSysUserPassword(memberEntity.getTelephone(), MD5Util.encode(password));
        } else {
            log.warn("重置会员密码无法同步更新到商家后台手机号为空,memberId={}", memberId);
        }
        return true;
    }

    private Boolean updatePwd(String password, MemberEntity memberEntity) {
        MemberEntity param = new MemberEntity();
        param.cleanInit();
        param.setId(memberEntity.getId());
        param.setPassword(MD5Util.encode(password));
        param.setLastModifierId(memberEntity.getId());
        param.setLastModifiedTime(new Date());
        param.setVersion(memberEntity.getVersion());
        return memberMgmtDao.updateById(param) > 0;
    }


    @Override
    @JedisOperation
    public LoginReturnVo setMemberPwd(InitMemberPwdPo initMemberPwdPo) {
        String password = initMemberPwdPo.getPassword();
        String telephone = initMemberPwdPo.getTelephone();
        //1.验证密码
        if (!RegexUtil.checkPassword(password)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_PASSWORD_FORMAT_ERR);
        }
        MemberEntity memberEntity = getMember(telephone);
        if (null == memberEntity) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        if (StringUtils.isNotBlank(memberEntity.getPassword())) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_INIT_PASSWORD_DISABLED);
        }
        if (!checkDependOperateCode(telephone, DependOperateType.SET_PWD.name() + initMemberPwdPo.getSetPwdCode())) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_INIT_PASSWORD_CODE_ERR);
        }
        if (updatePwd(password, memberEntity)) {
            String sessionId = IdGeneratorUtil.generatorUUID();
            MemberTokenDto memberTokenDto = MemberUtil.packageMemberToken(sessionId, memberEntity);
            //操作redis缓存
            loginOperateRedis(memberEntity.getId(), sessionId, memberTokenDto);
            LoginReturnVo loginReturnVo = new LoginReturnVo();
            loginReturnVo.setSessionId(sessionId);
            return loginReturnVo;
        } else {
            throw new GlobalException(MemberExceptionEnum.MEMBER_INIT_PASSWORD_FAIL);
        }


    }


    @Override
    public MemberDto getMemberByNameOrPhone(String var) {
        MemberEntity memberEntity = getMemberByMobileOrName(var);
        if (null == memberEntity) {
            return null;
        }
        MemberDto memberDto = new MemberDto();
        BeanCopyUtil.copy(memberEntity, memberDto);
        return memberDto;
    }

    @Override
    public MemberCenterDto getMemberCenter(Long memberId) {
        MemberEntity memberEntity = getMember(memberId);
        if (null == memberEntity) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        MemberCenterDto memberCenterDto = memberServiceFacade.getMemberCenter(memberId);
        BeanCopyUtil.copy(memberEntity, memberCenterDto);
        memberCenterDto.setTelephone(MemberUtil.encryptTelephone(memberCenterDto.getTelephone()));
        MemberAssetDto memberAssetDto = memberPointService.getMemberAsset(memberId);
        if (null == memberAssetDto || null == memberAssetDto.getPoint()
                || memberAssetDto.getPoint() < 1) {
            memberCenterDto.setPoint(0);
        } else {
            memberCenterDto.setPoint(memberAssetDto.getPoint());
        }
        memberCenterDto.setBalance(memberAssetDto.getBalance());
        return memberCenterDto;
    }

    @Override
    public MemberInfoDto getMemberInfo(Long memberId) {
        MemberEntity memberEntity = getMember(memberId);
        if (null == memberEntity) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        MemberInfoDto memberInfoDto = new MemberInfoDto();
        BeanCopyUtil.copy(memberEntity, memberInfoDto);
        return memberInfoDto;
    }

    @Override
    @JedisOperation
    public Boolean updateAccountState(Long memberId, Integer accountState) {
        MemberEntity memberEntity = getMember(memberId);
        if (null == memberEntity) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        if (accountState.equals(memberEntity.getAccountState())) {
            return true;
        }

        /*Jedis jedis = JedisContext.getJedis();
        String originSessionId = jedis.get(MemberCacheKey.MEMBER_ID_SESSION_PREFIX + memberId);
        if (null != originSessionId && StringUtils.isNotBlank(originSessionId)) {
            jedis.del(MemberCacheKey.MEMBER_LOGIN_SESSION_PREFIX + originSessionId);
        }*/
        if (ACCOUNT_STATE_LOCK.equals(accountState)) {
            lockAccountOperateRedis(memberId);
        }
        MemberEntity param = new MemberEntity();
        param.cleanInit();
        param.setAccountState(accountState);
        param.setId(memberId);
        param.setLastModifiedTime(new Date());
        return memberMgmtDao.updateById(param) > 0;
    }

    @Override
    public MemberTokenDto doLoginOrRegiseter(LoginOrRegiseterDto loginOrRegiseterDto, Boolean noSessionId) {
        String telephone = loginOrRegiseterDto.getTelephone();
        String ip = loginOrRegiseterDto.getIp();
        MemberEntity memberEntity = getMemberByMobileOrName(loginOrRegiseterDto.getTelephone());
        if (null != memberEntity) {
            if (ACCOUNT_STATE_LOCK.equals(memberEntity.getAccountState())) {
                throw new GlobalException(MemberExceptionEnum.MB_MEMBER_DISABLED);
            }
        } else {
            register(telephone, null, ip);
            memberEntity = getMember(telephone);
        }
        if (null == memberEntity) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_REGISTER_FAIL);
        }
        String sessionId = IdGeneratorUtil.generatorUUID();
        MemberTokenDto memberTokenDto = MemberUtil.packageMemberToken(sessionId, memberEntity);
        if (noSessionId) {
            memberTokenDto.setSessionId("");
        } else {
            loginOperateRedis(memberEntity.getId(), sessionId, memberTokenDto);
            updateLoginInfo(memberEntity, ip);
        }
        return memberTokenDto;
    }

    private MemberEntity register(String telephone, String password, String ip) {
        String userName = getUsername(8);
        /** 对会员用户名进行重复验证 */
        while (true) {
            MemberEntity findMemberByuserName = getMemberByMobileOrName(userName);
            if (null != findMemberByuserName) {
                userName = getUsername(8);
            } else {
                break;
            }
        }
        MemberEntity param = new MemberEntity();
        param.setUserName(userName);
        if (StringUtils.isNotBlank(password)) {
            param.setPassword(MD5Util.encode(password));
        } else {
            param.setPassword(DEFAULT_PASSWORD);
        }
        param.setRegisterTime(new Date());
        param.setGradeId(DEFAULT_GRADE_ID);
        param.setUpgradeScore(DEFAULT_UPGRADE_SCORE);
        param.setAccountState(DEFAULT_ACCOUNT_STATE);
        param.setTelephone(telephone);
        param.setRegisterIp(ip);
        if (memberMgmtDao.insert(param) <= 0) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_REGISTER_FAIL);
        }
        //送积分
        PointChangeDto pointChangeDto = new PointChangeDto();
        pointChangeDto.setPoint(registerAddPoint);
        pointChangeDto.setMemberId(param.getId());
        pointChangeDto.setTelephone(param.getTelephone());
        pointChangeDto.setUserName(param.getUserName());
        pointChangeDto.setOperateType(PointOperateType.REGISTER);
        pointChangeDto.setOperateSn("register" + param.getId());
        memberPointService.addOrReducePoint(pointChangeDto);
        return param;
    }


    @Autowired
    private WechatH5Service wechatH5Service;

    @Autowired
    private WechatAppService wechatAppService;

    @Override
    public String getWechatH5AuthUrl() {
        return wechatH5Service.getAuthorizeUrl();
    }

    @Override
    public String getWechatH5AuthUrl(String redirectType) {
        return wechatH5Service.getAuthorizeUrl(redirectType);
    }

    private MemberBindAccountEntity getBindAccount(Integer accountType, String thirdUnionid) {
        try {
            MemberBindAccountEntity param = new MemberBindAccountEntity();
            param.cleanInit();
            param.setAccountType(accountType);
            param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            param.setThirdUnionid(thirdUnionid);
            return memberBindAccountDao.selectOne(param);
        } catch (Exception e) {
            log.error("第三方登录信息异常,该thirdUnionid存在多条信息accountType={},mobile={}", accountType, thirdUnionid);
            throw new GlobalException(MemberExceptionEnum.MEMBER_THIRDPARTY_MULTI);
        }
    }

    private MemberBindAccountEntity getMemberBindAccount(Integer accountType, Long memberId) {
        MemberBindAccountEntity param = new MemberBindAccountEntity();
        param.cleanInit();
        param.setAccountType(accountType);
        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        param.setMemberId(memberId);
        return memberBindAccountDao.selectOne(param);
    }

    private void insertBindAccount(WechatUserBo wechatUser, MemberEntity member, Integer accountType, String ip) {
        MemberBindAccountEntity memberBindAccountEntity = new MemberBindAccountEntity();
        memberBindAccountEntity.setThirdUnionid(wechatUser.getUnionid());
        memberBindAccountEntity.setThirdDesc(JSON.toJSONString(wechatUser));
        memberBindAccountEntity.setMemberId(member.getId());
        memberBindAccountEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        memberBindAccountEntity.setUserName(member.getUserName());
        memberBindAccountEntity.setAccountType(accountType);
        memberBindAccountEntity.setBindingTime(new Date());
        memberBindAccountEntity.setBindingIp(ip);
        if (memberBindAccountDao.insert(memberBindAccountEntity) <= 0) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_LOGIN_ERROR);
        }
    }

    private void updateBindAccount(MemberBindAccountEntity memberBindAccountEntity, MemberEntity memberEntity, String ip) {
        MemberBindAccountEntity param = new MemberBindAccountEntity();
        param.cleanInit();
        param.setId(memberBindAccountEntity.getId());
        param.setVersion(memberBindAccountEntity.getVersion());
        param.setUserName(memberEntity.getUserName());
        param.setMemberId(memberEntity.getId());
        param.setLastModifierId(memberEntity.getId());
        param.setLastModifiedTime(new Date());
        param.setBindingTime(new Date());
        param.setBindingIp(ip);
        if (memberBindAccountDao.updateById(param) <= 0) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_LOGIN_ERROR);
        }
    }


    @Override
    @JedisOperation
    public LoginReturnVo doWechatH5Login(String code, String ip) {
        WechatUserBo wechatUser = wechatH5Service.getUserInfo(wechatH5Service.getAccessToken(code));
        if (StringUtils.isBlank(wechatUser.getUnionid())) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_AUTH_ERR);
        }
        //return doWechatPublicLogin(ip, wechatUser);
        return doWechatPublicLoginNew(ip, wechatUser);
    }

    @Override
    @JedisOperation
    public LoginReturnVo doWechatAppLogin(String code, String ip) {
        WechatUserBo wechatUser = wechatAppService.getUserInfo(wechatAppService.getAccessToken(code));
        if (StringUtils.isBlank(wechatUser.getUnionid())) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_AUTH_ERR);
        }
        //return doWechatPublicLogin(ip, wechatUser);
        return doWechatPublicLoginNew(ip, wechatUser);
    }

    @Override
    @JedisOperation
    public LoginReturnVo doWechatAppLogin(String accessToken, String openId, String ip) {
        WechatUserBo wechatUser = wechatAppService.getUserInfo(accessToken, openId);
        if (StringUtils.isBlank(wechatUser.getUnionid())) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_AUTH_ERR);
        }
        //return doWechatPublicLogin(ip, wechatUser);
        return doWechatPublicLoginNew(ip, wechatUser);
    }

    @Override
    @JedisOperation
    public LoginReturnVo doWechatAppLogin(WechatUserPo wechatUserPo, String ip) {
        WechatUserBo wechatUser = new WechatUserBo();
        BeanCopyUtil.copy(wechatUserPo, wechatUser);
        if (StringUtils.isBlank(wechatUser.getUnionid())) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_AUTH_ERR);
        }
        //  return doWechatPublicLogin(ip, wechatUser);
        return doWechatPublicLoginNew(ip, wechatUser);
    }

    /**
     * 2118-05-19
     * 微信一键登录 通用接口重构
     *
     * @param wechatUser
     * @return
     */
    private LoginReturnVo doWechatPublicLoginNew(String ip, WechatUserBo wechatUser) {
        MemberBindAccountEntity memberBindAccount = getBindAccount(ThirdAccountType.WX, wechatUser.getUnionid());
        MemberEntity member = null;
        if (null == memberBindAccount) {
            // 不真注册,仅记录微信账号相关信息 产生临时code操作
            member = new MemberEntity();
            member.setId(0L);
            member.setUserName("");
            insertBindAccount(wechatUser, member, ThirdAccountType.WX, ip);
            return generateTempLoginCode(wechatUser.getUnionid());
        } else {
            if (null != memberBindAccount.getMemberId() && memberBindAccount.getMemberId() > 0) {
                member = getMember(memberBindAccount.getMemberId());
            }
            if (null != member) {
                // 检查是否绑定手机流程及是否设置密码流程
                return doLoginCheckMember(member, ip);
            } else {
                // 不真注册,产生临时code操作
                return generateTempLoginCode(wechatUser.getUnionid());
            }
        }
    }

    /**
     * 2118-05-19
     * 微信一键登录时，不真注册,产生临时code操作
     *
     * @param thirdUnionid
     * @return
     */
    private LoginReturnVo generateTempLoginCode(String thirdUnionid) {
        String dependOperateCode = IdGeneratorUtil.generatorUUID();
        setDependOperateCode(thirdUnionid, DependOperateType.BIND_PHONE.name() + dependOperateCode);
        LoginReturnVo loginReturnVo = new LoginReturnVo();
        loginReturnVo.setSessionId("");
        loginReturnVo.setUserName(thirdUnionid);
        loginReturnVo.setHasBindTel(false);
        loginReturnVo.setHasSetPwd(false);
        loginReturnVo.setBindTelCode(dependOperateCode);
        loginReturnVo.setSetPwdCode("");
        loginReturnVo.setTelephone("");
        return loginReturnVo;
    }

    /**
     * 2118-05-19
     * 检查是否绑定手机流程及是否设置密码流程
     *
     * @param memberEntity
     * @return
     */
    private LoginReturnVo doLoginCheckMember(MemberEntity memberEntity, String ip) {
        String dependOperateCode = "";
        String sessionId = "";
        //优先判断是否绑定手机 再判断是否
        String telephone = memberEntity.getTelephone();
        String memberPwd = memberEntity.getPassword();
        Long memberId = memberEntity.getId();
        if (StringUtils.isBlank(telephone)) {
            dependOperateCode = IdGeneratorUtil.generatorUUID();
            setDependOperateCode(memberId.toString(), DependOperateType.BIND_PHONE.name() + dependOperateCode);
        } else if (StringUtils.isBlank(memberPwd)) {
            dependOperateCode = IdGeneratorUtil.generatorUUID();
            //setDependOperateCode(member.getId().toString(), DependOperateType.SET_PWD.name() + dependOperateCode);
            setDependOperateCode(telephone, DependOperateType.SET_PWD.name() + dependOperateCode);
        } else {
            sessionId = IdGeneratorUtil.generatorUUID();
            loginOperateRedis(memberId, sessionId, MemberUtil.packageMemberToken(sessionId, memberEntity));
            updateLoginInfo(memberEntity, ip);
        }
        LoginReturnVo loginReturnVo = new LoginReturnVo();
        loginReturnVo.setSessionId(sessionId);
        loginReturnVo.setUserName(memberEntity.getUserName());
        loginReturnVo.setHasBindTel(StringUtils.isNotBlank(telephone));
        loginReturnVo.setHasSetPwd(StringUtils.isNotBlank(memberPwd));
        loginReturnVo.setBindTelCode(dependOperateCode);
        loginReturnVo.setSetPwdCode(dependOperateCode);
        loginReturnVo.setTelephone(telephone);
        return loginReturnVo;
    }


    /**
     * 微信一键登录 通用功能
     *
     * @param ip
     * @param wechatUser
     * @return
     */
    @Deprecated
    private LoginReturnVo doWechatPublicLogin(String ip, WechatUserBo wechatUser) {
        MemberBindAccountEntity memberBindAccount = getBindAccount(ThirdAccountType.WX, wechatUser.getUnionid());
        MemberEntity member = null;
        if (null != memberBindAccount) {
            if (null != memberBindAccount.getMemberId() || memberBindAccount.getMemberId() > 0) {
                member = getMember(memberBindAccount.getMemberId());
            }
            if (null == member) {
                member = register("", null, ip);
                updateBindAccount(memberBindAccount, member, ip);
            }
            if (ACCOUNT_STATE_LOCK.equals(member.getAccountState())) {
                throw new GlobalException(MemberExceptionEnum.MB_MEMBER_DISABLED);
            }
        } else {
            member = register("", null, ip);
            insertBindAccount(wechatUser, member, ThirdAccountType.WX, ip);
        }

        String dependOperateCode = "";
        String sessionId = "";
        //优先判断是否绑定手机 再判断是否
        if (StringUtils.isBlank(member.getTelephone())) {
            dependOperateCode = IdGeneratorUtil.generatorUUID();
            setDependOperateCode(member.getId().toString(), DependOperateType.BIND_PHONE.name() + dependOperateCode);
        } else if (StringUtils.isBlank(member.getPassword())) {
            dependOperateCode = IdGeneratorUtil.generatorUUID();
            //setDependOperateCode(member.getId().toString(), DependOperateType.SET_PWD.name() + dependOperateCode);
            setDependOperateCode(member.getTelephone(), DependOperateType.SET_PWD.name() + dependOperateCode);
        } else {
            sessionId = IdGeneratorUtil.generatorUUID();
            loginOperateRedis(member.getId(), sessionId, MemberUtil.packageMemberToken(sessionId, member));
            updateLoginInfo(member, ip);
        }

        LoginReturnVo loginReturnVo = new LoginReturnVo();
        loginReturnVo.setSessionId(sessionId);
        loginReturnVo.setUserName(member.getUserName());
        loginReturnVo.setHasBindTel(StringUtils.isNotBlank(member.getTelephone()));
        loginReturnVo.setHasSetPwd(StringUtils.isNotBlank(member.getPassword()));
        loginReturnVo.setBindTelCode(dependOperateCode);
        loginReturnVo.setSetPwdCode(dependOperateCode);
        loginReturnVo.setTelephone(member.getTelephone());
        return loginReturnVo;
    }


    private void doWechatPublicBind(Long memberId, String ip, WechatUserBo wechatUser) {
        if (StringUtils.isBlank(wechatUser.getUnionid())) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_AUTH_ERR);
        }
        MemberEntity member = getMember(memberId);
        if (null == member) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        MemberBindAccountEntity hadBindAccount = getMemberBindAccount(ThirdAccountType.WX, memberId);
        if (null != hadBindAccount) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_HAD_BIND_WECHAT);
        }
        MemberBindAccountEntity memberBindAccount = getBindAccount(ThirdAccountType.WX, wechatUser.getUnionid());
        if (null != memberBindAccount) {
            if (null != memberBindAccount.getMemberId() && memberBindAccount.getMemberId() > 0) {
                throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_HAD_BIND);
            } else {
                updateBindAccount(memberBindAccount, member, ip);
            }
        } else {
            insertBindAccount(wechatUser, member, ThirdAccountType.WX, ip);
        }
    }

    private void doWechatPublicUnbind(Long memberId, String ip, WechatUserBo wechatUser) {
        String wxUnionid = wechatUser.getUnionid();
        if (StringUtils.isBlank(wxUnionid)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_WECHAT_AUTH_ERR);
        }
        //判断该用户是否存在
        MemberEntity member = getMember(memberId);
        if (null == member) {
            throw new GlobalException(MemberExceptionEnum.MB_MEMBER_NULL);
        }
        //判断该用户是否绑定了微信账号
        MemberBindAccountEntity hadBindAccount = getMemberBindAccount(ThirdAccountType.WX, memberId);
        if (null == hadBindAccount) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_NOT_BIND_WECHAT);
        }
        if (!wxUnionid.equals(hadBindAccount.getThirdUnionid())) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_NOT_BIND_WECHAT);
        }
        member.setId(0L);
        member.setUserName("");
        updateBindAccount(hadBindAccount, member, ip);
    }


    @Override
    public Boolean doWechatH5Bind(Long memberId, String code, String ip) {
        WechatUserBo wechatUser = wechatH5Service.getUserInfo(wechatH5Service.getAccessToken(code));
        doWechatPublicBind(memberId, ip, wechatUser);
        return true;
    }

    @Override
    public Boolean doWechatAppBind(Long memberId, WechatUserPo wechatUserPo, String ip) {
        WechatUserBo wechatUser = new WechatUserBo();
        BeanCopyUtil.copy(wechatUserPo, wechatUser);
        doWechatPublicBind(memberId, ip, wechatUser);
        return true;
    }

    @Override
    public Boolean doWechatH5Unbind(Long memberId, String code, String ip) {
        WechatUserBo wechatUser = wechatH5Service.getUserInfo(wechatH5Service.getAccessToken(code));
        doWechatPublicUnbind(memberId, ip, wechatUser);
        return true;
    }

    @Override
    public Boolean doWechatAppUnbind(Long memberId, WechatUserPo wechatUserPo, String ip) {
        WechatUserBo wechatUser = new WechatUserBo();
        BeanCopyUtil.copy(wechatUserPo, wechatUser);
        doWechatPublicUnbind(memberId, ip, wechatUser);
        return true;
    }

    private Boolean hasCheckin(Long memberId, String checkinDate) {
        MemberCheckinEntity param = new MemberCheckinEntity();
        param.cleanInit();
        param.setMemberId(memberId);
        param.setCheckinDate(checkinDate);
        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        MemberCheckinEntity memberCheckinEntity = memberCheckinDao.selectOne(param);
        if (null == memberCheckinEntity) {
            return false;
        }
        return true;
    }

    @Override
    @JedisOperation
    public Boolean doCheckin(MemberTokenDto memberTokenDto) {
        // 防止重复提交
        tryRedisLock(memberTokenDto.getMemberId().toString());

        Long memberId = memberTokenDto.getMemberId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String checkinDate = simpleDateFormat.format(new Date());
        if (hasCheckin(memberId, checkinDate)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_REPEAT_CHECKIN);
        }

        MemberCheckinEntity insertParm = new MemberCheckinEntity();
        insertParm.setMemberId(memberId);
        insertParm.setCreatorId(memberId);
        insertParm.setPoint(checkinAddPoint);
        insertParm.setCheckinDate(checkinDate);
        if (memberCheckinDao.insert(insertParm) < 1) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_REPEAT_CHECKIN);
        }
        PointChangeDto pointChangeDto = new PointChangeDto();
        pointChangeDto.setMemberId(memberId);
        pointChangeDto.setTelephone(memberTokenDto.getTelephone());
        pointChangeDto.setUserName(memberTokenDto.getUserName());
        pointChangeDto.setPoint(checkinAddPoint);
        pointChangeDto.setOperateType(PointOperateType.CHCEKIN);
        pointChangeDto.setOperateSn("checkin" + insertParm.getId());
        return memberPointService.addOrReducePoint(pointChangeDto);
    }

    @Override
    public Boolean validateCheckin(Long memberId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String checkinDate = simpleDateFormat.format(new Date());
        if (hasCheckin(memberId, checkinDate)) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_REPEAT_CHECKIN);
        }
        return true;
    }

    @Override
    public CheckinLogReturnVo queryCheckinLog(Long memberId, Long lastLogId) {
        MemberCheckinEntity param = new MemberCheckinEntity();
        param.cleanInit();
        param.setMemberId(memberId);
        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        EntityWrapper<MemberCheckinEntity> wrapperParam = new EntityWrapper<>();
        wrapperParam.setEntity(param);
        if (null != lastLogId && lastLogId > 0) {
            wrapperParam.lt("id", lastLogId);
        }
        wrapperParam.last("limit 10");
        wrapperParam.orderBy("id", false);
        List<MemberCheckinEntity> memberCheckinEntityList = memberCheckinDao.selectList(wrapperParam);
        if (CollectionUtils.isEmpty(memberCheckinEntityList)) {
            CheckinLogReturnVo checkinLogReturnVo = new CheckinLogReturnVo();
            checkinLogReturnVo.setList(null);
            checkinLogReturnVo.setLastLogId(null);
            return checkinLogReturnVo;
        }
        List<MemberCheckinDto> memberCheckinDtoList = new ArrayList<>();
        for (MemberCheckinEntity memberCheckinEntity : memberCheckinEntityList) {
            MemberCheckinDto memberCheckinDto = new MemberCheckinDto();
            BeanCopyUtil.copy(memberCheckinEntity, memberCheckinDto);
            memberCheckinDtoList.add(memberCheckinDto);
        }
        CheckinLogReturnVo checkinLogReturnVo = new CheckinLogReturnVo();
        checkinLogReturnVo.setList(memberCheckinDtoList);
        checkinLogReturnVo.setLastLogId(memberCheckinDtoList.get(memberCheckinDtoList.size() - 1).getId());
        return checkinLogReturnVo;
    }

    @Override
    public Boolean checkTelephone(String telephone, String ip) {
        if (!RegexUtil.checkMobile(telephone)) {
            throw new GlobalException(MemberExceptionEnum.MB_MOBILE_ERR);
        }
        MemberEntity member = getMemberByMobileOrName(telephone);
        if (null != member) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_PHONENUMBER_EXIST);
        }
        return true;
    }

}
