package com.topaiebiz.sms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.sms.context.LimitIpInfoContext;
import com.topaiebiz.sms.context.SmsSendInfoContext;
import com.topaiebiz.sms.dao.MessageLogDao;
import com.topaiebiz.sms.dao.MessageTemplateDao;
import com.topaiebiz.sms.dto.CaptchaDto;
import com.topaiebiz.sms.dto.SmsLimitIpInfoDTO;
import com.topaiebiz.sms.dto.SmsSendInfoDTO;
import com.topaiebiz.sms.entity.MessageLogEntity;
import com.topaiebiz.sms.entity.MessageTemplateEntity;
import com.topaiebiz.sms.exception.MessageExceptionEnum;
import com.topaiebiz.sms.service.MessageService;
import com.topaiebiz.sms.third.alisms.util.AliSMSUtil;
import com.topaiebiz.sms.util.SMSRedisCacheKeys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    /**
     * 验证码失效时间 毫秒
     */
    @Value("${alisms.failure_time}")
    private Integer failureTime;
    /**
     * 间隔发送时间 单位毫秒
     */
    @Value("${alisms.interval_time}")
    private Long captchaIntervalTime;
    /**
     * 每个IP每天限制条数
     */
    @Value("${alisms.limit_ip}")
    private Integer ipRequestCountLimit;
    /**
     * 每个手机号每天限制？条
     */
    @Value("${alisms.limit_phone}")
    private Integer sendPhoneCountLimit;
    /**
     * 发送限制是否开启
     */
    @Value("${alisms.enable_flag}")
    private Boolean sentLimitSwitch;

    @Autowired
    private MessageLogDao messageLogDao;

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Autowired
    private DistLockSservice distLockSservice;

    @Autowired
    private RedisCache redisCache;

    @Override
    public Boolean sendCaptcha(CaptchaDto captchaDto) {
        // 参数校验
        if (StringUtils.isBlank(captchaDto.getTelephone())) {
            throw new GlobalException(MessageExceptionEnum.TELEPHONE_IS_NULL);
        }
        if (null == captchaDto.getType()) {
            throw new GlobalException(MessageExceptionEnum.TYPE_IS_NULL);
        }
        if (StringUtils.isBlank(captchaDto.getIp())) {
            throw new GlobalException(MessageExceptionEnum.IP_IS_NULL);
        }
        LockResult operationLock = null;
        try {
            operationLock = distLockSservice.tryLock(SMSRedisCacheKeys.CAPTCHA_OPERATION_LOCK_BY_TELEPHONE, captchaDto.getTelephone());
            if (!operationLock.isSuccess()) {
                throw new GlobalException(MessageExceptionEnum.CAPTCHA_OPERATION_LOCKING);
            }
            String sendInfo = redisCache.get(SMSRedisCacheKeys.CAPTCHA_SEND_INFO_BY_USER + captchaDto.getTelephone());
            if (StringUtils.isNotBlank(sendInfo)) {
                SmsSendInfoDTO smsSendInfoDTO = JSON.parseObject(sendInfo, SmsSendInfoDTO.class);
                SmsSendInfoContext.set(smsSendInfoDTO);
                // 发送校验
                this.checkSendingBefore(captchaDto);
            }
            // 发送短信
            this.sendMessage(captchaDto);
            return true;
        } finally {
            SmsSendInfoContext.remove();
            LimitIpInfoContext.remove();
            distLockSservice.unlock(operationLock);
        }
    }

    @Override
    public Boolean verifyCaptcha(CaptchaDto captchaDto) {
        String telephone = captchaDto.getTelephone();
        // 参数校验
        if (StringUtils.isBlank(telephone)) {
            throw new GlobalException(MessageExceptionEnum.TELEPHONE_IS_NULL);
        }
        if (null == captchaDto.getType()) {
            throw new GlobalException(MessageExceptionEnum.TYPE_IS_NULL);
        }
        if (StringUtils.isBlank(captchaDto.getCaptcha())) {
            throw new GlobalException(MessageExceptionEnum.CAPTCHA_IS_NULL);
        }
        String captchaType = captchaDto.getType().getType();
        LockResult operationLock = null;
        try {
            operationLock = distLockSservice.tryLock(SMSRedisCacheKeys.CAPTCHA_OPERATION_LOCK_BY_TELEPHONE, telephone);
            if (!operationLock.isSuccess()) {
                throw new GlobalException(MessageExceptionEnum.CAPTCHA_OPERATION_LOCKING);
            }
            // 获取缓存中的验证码信息
            String sendInfo = redisCache.get(SMSRedisCacheKeys.CAPTCHA_SEND_INFO_BY_USER + telephone);
            if (StringUtils.isBlank(sendInfo)) {
                throw new GlobalException(MessageExceptionEnum.VERIFY_FAIL_SEND_CAPTCHA_INFO_IS_NULL);
            }
            SmsSendInfoDTO smsSendInfoDTO = JSON.parseObject(sendInfo, SmsSendInfoDTO.class);
            // 判断当前验证的验证码类型  与 缓存中的当前验证码类型是否一致
            if (captchaType.equals(smsSendInfoDTO.getCurrentCaptchaType())) {
                if (smsSendInfoDTO.getCurrentVerifyFailCount().compareTo(Constants.SMS.SMS_VERIFY_FAIL_SEND_AGAIN_NUM) >= 0) {
                    Map<String, Integer> sendCountMap = smsSendInfoDTO.getSendCountGroupByType();
                    if (null != sendCountMap) {
                        Integer sendCount = sendCountMap.get(captchaType);
                        if (null != sendCount && Constants.SMS.SEND_CAPTCHA_MAX_COUNT_GROUP_BY_TYPE.compareTo(sendCount) <= 0) {
                            throw new GlobalException(MessageExceptionEnum.CAPTCHA_SEND_IS_PROHIBIT);
                        }
                    }
                    // 连续错误三次以上,不允许在校验,提示重新发送验证校验
                    throw new GlobalException(MessageExceptionEnum.VERIFY_FAIL_NUM_IS_TO_MUCH_SEND_AGAIN);
                }
            } else {
                smsSendInfoDTO.setCurrentVerifyFailCount(0);
                smsSendInfoDTO.setCurrentCaptchaType(captchaType);
            }
            String captcha = smsSendInfoDTO.getCurrentCaptcha();
            if (StringUtils.isBlank(captcha)) {
                throw new GlobalException(MessageExceptionEnum.CURRENT_CAPTCHA_IS_NULL);
            }
            // 校验验证码
            if (captcha.equalsIgnoreCase(captchaDto.getCaptcha())) {
                // 校验成功，则置空当前需要验证的验证码 和 当前验证码的错误次数
                smsSendInfoDTO.setCurrentCaptcha(null);
                smsSendInfoDTO.setCurrentVerifyFailCount(0);
                smsSendInfoDTO.setCurrentCaptchaType(null);
                redisCache.set(SMSRedisCacheKeys.CAPTCHA_SEND_INFO_BY_USER + telephone, JSON.toJSONString(smsSendInfoDTO), this.getResetSecond().intValue());
                return true;
            }
            // 校验失败， 更新缓存
            smsSendInfoDTO.setCurrentVerifyFailCount(smsSendInfoDTO.getCurrentVerifyFailCount() + 1);
            if (smsSendInfoDTO.getCurrentVerifyFailCount().compareTo(Constants.SMS.SMS_VERIFY_FAIL_SEND_AGAIN_NUM) >= 0) {
                // 连续错误达到三次,当前验证码失效并且设置发送短信的时间冷却15
                smsSendInfoDTO.getAllowToBeSendTime().put(captchaType, Instant.now().toEpochMilli() + Constants.SMS.VERIFY_FAIL_TO_MUCH_SEND_AGAIN_COOLING_TIME);
                smsSendInfoDTO.setCurrentCaptcha(null);
            }
            redisCache.set(SMSRedisCacheKeys.CAPTCHA_SEND_INFO_BY_USER + telephone, JSON.toJSONString(smsSendInfoDTO), this.getResetSecond().intValue());
            throw new GlobalException(MessageExceptionEnum.VERIFY_FAIL_CAPTCHA_NOT_EQUALS);
        } finally {
            distLockSservice.unlock(operationLock);
        }
    }

    /**
     * Description: 发送短信
     *
     * @Author: hxpeng
     * createTime: 2018/6/11
     * @param:
     **/
    private void sendMessage(CaptchaDto captchaDto) {
        MessageTemplateEntity messageTemplateEntity = this.findTemplate(Integer.valueOf(captchaDto.getType().getType()));
        String content = messageTemplateEntity.getContent();
        Map<String, Object> jsonMap = new HashMap<>();
        String randNum = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (content.contains(Constants.SMS.SMS_TEMPLATE_DATE_TIME)) {
            String formatDate = df.format(new Date());
            jsonMap.put("date_time", formatDate);
            content = content.replace(Constants.SMS.SMS_TEMPLATE_DATE_TIME, formatDate);
        }
        if (content.contains(Constants.SMS.SMS_TEMPLATE_CAPTCHA)) {
            jsonMap.put("captcha", randNum);
            content = content.replace(Constants.SMS.SMS_TEMPLATE_CAPTCHA, randNum);
        }
        String json = JSONObject.toJSONString(jsonMap);
        log.info("发送短信手机号：{}, 参数json值为: {}", captchaDto.getTelephone(), json);
        String res = AliSMSUtil.sendSMS(captchaDto.getTelephone(), messageTemplateEntity.getSignName(), messageTemplateEntity.getTemplateCode(), json, null);
        if (Constants.SMS.SEND_SMS_SUCCESS_CODE.equalsIgnoreCase(res)) {
            this.saveLogAndRefreshCache(captchaDto, content, randNum);
        }
    }

    /**
     * Description: 保存发送日志， 并更新缓存
     *
     * @Author: hxpeng
     * createTime: 2018/6/11
     * @param:
     **/
    private void saveLogAndRefreshCache(CaptchaDto captchaDto, String content, String randNum) {
        String captchaType = captchaDto.getType().getType();
        Long currentMilli = Instant.now().toEpochMilli();
        //保存记录
        MessageLogEntity messageLogEntity = new MessageLogEntity();
        messageLogEntity.setTelephone(captchaDto.getTelephone());
        messageLogEntity.setCaptcha(randNum);
        messageLogEntity.setSendIp(captchaDto.getIp());
        messageLogEntity.setContent(content);
        messageLogEntity.setSendType(Integer.valueOf(captchaType));
        messageLogEntity.setSendTime(new Date());
        messageLogEntity.setMemberId(captchaDto.getMemberId());
        messageLogDao.insert(messageLogEntity);

        SmsSendInfoDTO smsSendInfoDTO = SmsSendInfoContext.get();
        if (null == smsSendInfoDTO) {
            smsSendInfoDTO = new SmsSendInfoDTO().firstSend(captchaDto.getTelephone(), captchaType, randNum);
        } else {
            //改类型，最后一次发送短信时间
            smsSendInfoDTO.setCurrentCaptcha(randNum);
            smsSendInfoDTO.setSendCount(smsSendInfoDTO.getSendCount() + 1);
            smsSendInfoDTO.setCurrentVerifyFailCount(0);
            // 发送成功后，置空允许发送时间
            smsSendInfoDTO.getAllowToBeSendTime().put(captchaType, null);
            if (sendPhoneCountLimit.compareTo(smsSendInfoDTO.getSendCount()) < 0) {
                //发送次数 已加达到每日限制，设置当日已禁用
                smsSendInfoDTO.setProhibit(true);
            }
        }
        // 更新手机号 各验证类型获取次数
        Map<String, Integer> sendCountGroupByType = smsSendInfoDTO.getSendCountGroupByType();
        Integer sendCount = sendCountGroupByType.get(captchaType);
        if (null == sendCount) {
            sendCount = 1;
        } else {
            sendCount++;
        }
        sendCountGroupByType.put(captchaType, sendCount);
        smsSendInfoDTO.getLastSendTimeByType().put(captchaType, currentMilli);
        // 换入缓存， 并设置过期时间为今天为止
        redisCache.set(SMSRedisCacheKeys.CAPTCHA_SEND_INFO_BY_USER + captchaDto.getTelephone(), JSON.toJSONString(smsSendInfoDTO), this.getResetSecond().intValue());

        SmsLimitIpInfoDTO smsLimitIpInfoDTO = LimitIpInfoContext.get();
        if (null == smsLimitIpInfoDTO) {
            smsLimitIpInfoDTO = new SmsLimitIpInfoDTO().firstSend(captchaDto.getIp(), randNum);
        } else {
            smsLimitIpInfoDTO.setLastSendTime(currentMilli);
            smsLimitIpInfoDTO.setLastSendCaptcha(randNum);
            smsLimitIpInfoDTO.setSendCount(smsLimitIpInfoDTO.getSendCount() + 1);
            if (ipRequestCountLimit.compareTo(smsLimitIpInfoDTO.getSendCount()) < 0) {
                // ip请求达到每日限制，设置当日已禁用
                smsLimitIpInfoDTO.setProhibit(true);
            }
        }
        // 更新ip 各验证类型请求次数
        Map<String, Integer> ipRequestGroupByType = smsLimitIpInfoDTO.getIpRequestGroupByType();
        Integer ipRequestCount = ipRequestGroupByType.get(captchaType);
        if (null == ipRequestCount) {
            ipRequestCount = 1;
        } else {
            ipRequestCount++;
        }
        ipRequestGroupByType.put(captchaType, ipRequestCount);
        // 换入缓存， 并设置过期时间为今天为止
        redisCache.hset(SMSRedisCacheKeys.CAPTCHA_IP_SENT_LIMIT, captchaDto.getIp(), JSON.toJSONString(smsLimitIpInfoDTO));
        redisCache.expire(SMSRedisCacheKeys.CAPTCHA_IP_SENT_LIMIT, this.getResetSecond().intValue());
    }

    /**
     * Description: 查询模板
     *
     * @Author: hxpeng
     * createTime: 2018/6/11
     * @param:
     **/
    private MessageTemplateEntity findTemplate(Integer type) {
        MessageTemplateEntity condition = new MessageTemplateEntity();
        condition.cleanInit();
        condition.setTemplateType(type);
        condition.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        MessageTemplateEntity templateEntity = messageTemplateDao.selectOne(condition);
        if (null == templateEntity) {
            throw new GlobalException(MessageExceptionEnum.SENT_SMS_DON_NOT_FOUND_TEMPLATE);
        }
        return templateEntity;
    }

    /**
     * Description: 发送校验
     *
     * @Author: hxpeng
     * createTime: 2018/6/11
     * @param:
     **/
    private void checkSendingBefore(CaptchaDto captchaDto) {
        SmsSendInfoDTO smsSendInfoDTO = SmsSendInfoContext.get();
        // 为空 或者 不做限制
        if (null == smsSendInfoDTO || sentLimitSwitch) {
            return;
        }
        // 当前毫秒数
        Long currentMilli = Instant.now().toEpochMilli();
        // 今日获取短信验证码已达上线
        if (smsSendInfoDTO.getProhibit()) {
            throw new GlobalException(MessageExceptionEnum.CAPTCHA_SEND_IS_PROHIBIT);
        }
        // 获取验证码频率判断
        Long lastSendMilli = smsSendInfoDTO.getLastSendTimeByType().get(captchaDto.getType().getType());
        if (null != lastSendMilli && (currentMilli - lastSendMilli) < captchaIntervalTime) {
            throw new GlobalException(MessageExceptionEnum.CAPTCHA_SEND_IS_IN_THE_COOLING_ONE_MINUTE);
        }
        // 当前手机号对各个短信类型的获取次数判断
        Integer sendCount = smsSendInfoDTO.getSendCountGroupByType().get(captchaDto.getType().getType());
        if (null != sendCount && Constants.SMS.SEND_CAPTCHA_MAX_COUNT_GROUP_BY_TYPE.compareTo(sendCount) <= 0) {
            log.warn(">>>>>>>>>>发送失败，手机号：{} 对类型：{}的短信获取已达上限值！", captchaDto.getTelephone(), captchaDto.getType());
            throw new GlobalException(MessageExceptionEnum.CAPTCHA_SEND_IS_PROHIBIT);
        }
        // 当前获取的短信验证类型 是否被允许
        if (MapUtils.isNotEmpty(smsSendInfoDTO.getAllowToBeSendTime())) {
            Long allowToBeSendTime = smsSendInfoDTO.getAllowToBeSendTime().get(captchaDto.getType().getType());
            if (null != allowToBeSendTime && currentMilli <= allowToBeSendTime) {
                throw new GlobalException(MessageExceptionEnum.CAPTCHA_SEND_IS_IN_THE_COOLING_TEN_MINUTE);
            }
        }
        //IP当天发送次数
        String requestIpInfo = redisCache.hget(SMSRedisCacheKeys.CAPTCHA_IP_SENT_LIMIT, captchaDto.getIp());
        if (StringUtils.isBlank(requestIpInfo)) {
            return;
        }
        SmsLimitIpInfoDTO smsLimitIpInfoDTO = JSON.parseObject(requestIpInfo, SmsLimitIpInfoDTO.class);
        LimitIpInfoContext.set(smsLimitIpInfoDTO);
        if (smsLimitIpInfoDTO.getSendCount() > ipRequestCountLimit) {
            log.warn(">>>>>>>>>>captcha： 获取验证码失败， 请求者的IP已达限制值！");
            throw new GlobalException(MessageExceptionEnum.CAPTCHA_CAN_NOT_SEND_AGAIN);
        }
    }

    /**
     * 距离晚上十二点剩余的时间
     *
     * @return
     */
    private Long getResetSecond() {
        Date date = new Date();
        // 获取 明天零点零分零秒
        LocalDateTime midnight = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        // 当前时间
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        // 返回二者相差秒数
        return ChronoUnit.SECONDS.between(currentDateTime, midnight);
    }


}

