package com.topaiebiz.member.point.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.nebulapaas.common.redis.aop.JedisOperation;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.basic.api.ConfigApi;
import com.topaiebiz.member.constants.AssetOperateType;
import com.topaiebiz.member.constants.PointOperateType;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.dto.point.*;
import com.topaiebiz.member.exception.MemberExceptionEnum;
import com.topaiebiz.member.exception.PointExceptionEnum;
import com.topaiebiz.member.member.constants.MemberCacheKey;
import com.topaiebiz.member.member.service.MemberService;
import com.topaiebiz.member.po.RedressAssetPo;
import com.topaiebiz.member.point.crm.ws.*;
import com.topaiebiz.member.point.dao.MemberAssetDao;
import com.topaiebiz.member.point.dao.MemberPointLogDao;
import com.topaiebiz.member.point.dao.PointCrmLogDao;
import com.topaiebiz.member.point.entity.MemberAssetEntity;
import com.topaiebiz.member.point.entity.PointCrmLogEntity;
import com.topaiebiz.member.point.service.MemberBalanceLogService;
import com.topaiebiz.member.point.service.MemberPointLogService;
import com.topaiebiz.member.point.service.MemberPointService;
import com.topaiebiz.member.point.utils.MemberPointUtil;
import com.topaiebiz.member.point.utils.XmlParserTool;
import com.topaiebiz.member.vo.CrmPointSumVo;
import com.topaiebiz.member.vo.PointCrmLogReturnVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

import javax.xml.ws.Holder;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class MemberPointServiceImpl implements MemberPointService, DisposableBean {

    @Value("${point.convert.rate}")
    private BigDecimal crmPointToMmgPointRate = new BigDecimal(5);
    private final static Integer POINT_INIT_ZERO = 0;
    private final static Integer CRM_EXECUTE_STATUS_SUCCESS = 1;
    private final static Integer CRM_EXECUTE_STATUS_FAIL = 0;
    private final static String CRM_WHITE_PHONE_CONFIG = "crmWhitePhone";
    private final static String CRM_POINT_SWITCH_CONFIG = "crmPointSwitch";
    private final static String CRM_POINT_SWITCH_TRUE = "true";

    @Autowired
    private MemberAssetDao memberAssetDao;
    @Autowired
    private PointCrmLogDao pointCrmLogDao;
    @Autowired
    private MemberPointUtil memberPointUtil;
    @Autowired
    private MemberPointLogService pointLogService;
    @Autowired
    private MemberPointLogDao memberPointLogDao;
    @Autowired
    private MemberBalanceLogService balanceLogService;

    @Autowired
    private ConfigApi configApi;

    private static int coreSize = 2;
    private ExecutorService logExecutor = new ThreadPoolExecutor(coreSize, coreSize, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public void destroy() throws Exception {
        logExecutor.shutdown();
    }

    private MemberAssetEntity getAsset(Long memberId) {
        MemberAssetEntity param = new MemberAssetEntity();
        param.cleanInit();
        param.setMemberId(memberId);
        MemberAssetEntity memberAssetEntity = memberAssetDao.selectOne(param);
        return memberAssetEntity;
    }

    private boolean insertMeberAsset(Long memberId, String userName, String telephone) {
        MemberAssetEntity assetEntity = new MemberAssetEntity();
        assetEntity.setMemberId(memberId);
        assetEntity.setUserName(userName);
        assetEntity.setTelephone(telephone);
        assetEntity.setBalance(BigDecimal.ZERO);
        assetEntity.setPoint(POINT_INIT_ZERO);
        assetEntity.setCreatorId(memberId);
        return memberAssetDao.insert(assetEntity) > 0;
    }

    private MemberAssetEntity getValidAssetEntity(Long memberId, String userName, String telephone) {
        MemberAssetEntity memberAssetEntity = getAsset(memberId);
        if (null == memberAssetEntity) {
            insertMeberAsset(memberId, userName, telephone);
            memberAssetEntity = getAsset(memberId);
            if (null == memberAssetEntity) {
                throw new GlobalException(PointExceptionEnum.MEMBER_ASSET_NULL);
            }
        }
        return memberAssetEntity;
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
    public Map<Long, MemberAssetDto> getMemberAssetMap(List<Long> memberIdList) {
        if (CollectionUtils.isEmpty(memberIdList)) {
            return null;
        }
        EntityWrapper<MemberAssetEntity> condition = new EntityWrapper<>();
        condition.in("memberId", memberIdList);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<MemberAssetEntity> memberAssetEntityList = memberAssetDao.selectList(condition);
        if (CollectionUtils.isEmpty(memberAssetEntityList)) {
            return null;
        }
        Map<Long, MemberAssetDto> memberAssetDtoMap = new HashMap<>();
        for (MemberAssetEntity memberAssetEntity : memberAssetEntityList) {
            MemberAssetDto memberAssetDto = new MemberAssetDto();
            BeanUtils.copyProperties(memberAssetEntity, memberAssetDto);
            memberAssetDtoMap.put(memberAssetEntity.getMemberId(), memberAssetDto);
        }
        return memberAssetDtoMap;
    }

    @Override
    public MemberAssetDto getMemberAsset(Long memberId) {
        MemberAssetEntity memberAssetEntity = getAsset(memberId);
        MemberAssetDto memberAssetDto = new MemberAssetDto();
        if (null == memberAssetEntity) {
            memberAssetDto.setMemberId(memberId);
            return memberAssetDto;
        } else {
            BeanCopyUtil.copy(memberAssetEntity, memberAssetDto);
            return memberAssetDto;
        }
    }

    @Override
    @JedisOperation
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public boolean useOrRollbackAccountAssets(AssetChangeDto assetChangeDto) {
        Long memberId = assetChangeDto.getMemberId();
        String userName = assetChangeDto.getUserName();
        String telephone = assetChangeDto.getTelephone();
        /**使用单机版的redis 防范重复提交**/
        tryRedisLock("useOrRollbackAccountAssets" + memberId.toString());

        MemberAssetEntity memberAssetEntity = getValidAssetEntity(memberId, userName, telephone);
        if (memberAssetEntity.getPoint() + assetChangeDto.getPoint() < 0 ||
                memberAssetEntity.getBalance().add(assetChangeDto.getBalance()).compareTo(BigDecimal.ZERO) < 0) {
            log.error("资产变化异常memberId={}, before={},change={}", memberId, JSON.toJSONString(memberAssetEntity), JSON.toJSONString(assetChangeDto));
            throw new GlobalException(PointExceptionEnum.MEMBER_ASSET_OVER);
        }
        if (memberAssetDao.updateOnlyAsset(assetChangeDto.getPoint(), assetChangeDto.getBalance(), memberId, new Date()) <= 0) {
            log.error("资产变化执行失败memberId={},change={}", memberId, JSON.toJSONString(assetChangeDto));
            throw new GlobalException(PointExceptionEnum.MEMBER_UPDATE_ASSET_FAIL);
        }

        //异步记录日志
        Future<Boolean> future = logExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return pointLogService.saveAssetLog(memberId, assetChangeDto,
                        memberAssetEntity.getPoint(), memberAssetEntity.getBalance());
            }
        });
        Boolean saveLogRet = false;
        try {
            saveLogRet = future.get(2000, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            if (null != ex.getCause() && ex.getCause() instanceof GlobalException) {
                log.error("异步记录资产变化日志业务 memberId={},change={},error={}", memberId, JSON.toJSONString(assetChangeDto), JSON.toJSONString(((GlobalException) ex.getCause()).getExceptionInfo()));
                throw (GlobalException) ex.getCause();
            } else {
                log.error("资产变化记录日志失败获得结果超时 memberId={},change={}", memberId, JSON.toJSONString(assetChangeDto));
                throw new GlobalException(PointExceptionEnum.MEMBER_UPDATE_ASSET_FAIL);
            }
        }
        return saveLogRet;
    }


    @Override
    @JedisOperation
    public boolean addOrReducePoint(PointChangeDto pointChangeDto) {
        Long memberId = pointChangeDto.getMemberId();
        String userName = pointChangeDto.getUserName();
        String telephone = pointChangeDto.getTelephone();
        /**使用单机版的redis 防范重复提交**/
        tryRedisLock("addOrReducePoint" + memberId.toString());

        MemberAssetEntity memberAssetEntity = getValidAssetEntity(memberId, userName, telephone);
        if (memberAssetEntity.getPoint() + pointChangeDto.getPoint() < 0) {
            log.error("积分变化异常before={},change={}", JSON.toJSONString(memberAssetEntity), JSON.toJSONString(pointChangeDto));
            throw new GlobalException(PointExceptionEnum.MEMBER_POINT_OVER);
        }
        if (0 == pointChangeDto.getPoint()) {
            log.error("积分变化为0 before={},change={}", JSON.toJSONString(memberAssetEntity), JSON.toJSONString(pointChangeDto));
            throw new GlobalException(PointExceptionEnum.MEMBER_POINT_ZERO);
        }

        if (memberAssetDao.updateOnlyAsset(pointChangeDto.getPoint(), BigDecimal.ZERO, memberId, new Date()) <= 0) {
            log.error("资产-积分变化执行失败memberId={},change={}", memberId, JSON.toJSONString(pointChangeDto));
            throw new GlobalException(PointExceptionEnum.MEMBER_UPDATE_ASSET_FAIL);
        }
        //异步记录日志
        Future<Boolean> future = logExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return pointLogService.savePointLog(memberId, pointChangeDto, memberAssetEntity.getPoint());
            }
        });
        Boolean savePointLogRet = null;
        try {
            savePointLogRet = future.get(2000, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            if (null != ex.getCause() && ex.getCause() instanceof GlobalException) {
                log.error("异步记录积分变化日志业务 memberId={},change={},error={}", memberId, JSON.toJSONString(pointChangeDto), JSON.toJSONString(((GlobalException) ex.getCause()).getExceptionInfo()));
                throw (GlobalException) ex.getCause();
            } else {
                log.error("积分记录日志失败获得结果超时 memberId={},change={}", memberId, JSON.toJSONString(pointChangeDto));
                throw new GlobalException(PointExceptionEnum.MEMBER_UPDATE_ASSET_FAIL);
            }
        }
        return savePointLogRet;
    }


    private CrmContact getCrmMemberInfo(String memberMobile) {
        Holder<String> errCode = new Holder<String>();
        Holder<String> errDesc = new Holder<String>();
        Holder<String> lastPage = new Holder<String>();
        Holder<String> outXml = new Holder<String>();
        CRMContactWS cRMContactWS = new CRMContactWS();
        ContactQuery contactQuery = cRMContactWS.getContactQuery();
        contactQuery.contactQuery("10", "", "All", "积分商城", "0", "[Contact.BYM Contact Business Type] ='贝因美' AND [Contact.Status] ='活动' AND [Contact.BYM Baby Flag]='N' AND [Contact.Cellular Phone #]='" + memberMobile + "'", errCode, errDesc, lastPage, outXml);
        if (!errCode.value.equals("100")) {
            log.error("调用CRM接口获取用户信息异常:{} 描述:{}", JSON.toJSONString(errCode), JSON.toJSONString(errDesc));
            throw new GlobalException(PointExceptionEnum.MEMBER_POINT_CRM_ERR);
        }
        String result = outXml.value;
        if (outXml.value != null && !outXml.value.contains("<Contact>")) {
            return null;
        }
        CrmContact crmContact = XmlParserTool.convertXmlStrToObject(CrmContact.class, result.replace("<ListOfContact>", "").replace("</ListOfContact>", ""));
        return crmContact;
    }


    private Long executeCrmToMmg(String crmMemberId, Integer waitConvertPoint, Integer mmgPoint, Double rate, MemberTokenDto memberTokenDto) {
        //int number = (int) Math.floor(waitConvertPoint / rate);
        int number = mmgPoint;
        //TODO 原来 代码是 int single = (int) Math.floor(rate); 需要验证的是：能否传入值为float？？
        int single = (int) Math.floor(rate);
        if (single <= 0) {
            log.error("调贝因美积分转换妈妈购积分比例异常:{}", rate);
            throw new GlobalException(PointExceptionEnum.MEMBER_POINT_CRM_RATE_ERR);
        }
        //Double single = rate;
        String xml = "<?xml version='1.0' encoding='UTF-8'?><RedeemReq><MemberType>单个</MemberType><MemberId>" + crmMemberId + "</MemberId><Channel>积分商城</Channel><RedeemType>邮寄兑换</RedeemType><UseJLPointsFlag>N</UseJLPointsFlag><PointType>购物积分</PointType><OrderToTxnFlag>Y</OrderToTxnFlag><Comments>OK</Comments><ProdList><ProdInfo><ProdNum>1-DKKCQGP</ProdNum><ItemId>1</ItemId><Points>" + single + "</Points><Quantity>" + number + "</Quantity><OverWriteFlag>Y</OverWriteFlag></ProdInfo></ProdList></RedeemReq>";
        Long memberId = memberTokenDto.getMemberId();
        Date preDate = new Date();
        PointCrmLogEntity insertCrmLog = new PointCrmLogEntity();
        insertCrmLog.setMemberId(memberId);
        insertCrmLog.setUserName(memberTokenDto.getUserName());
        insertCrmLog.setTelephone(memberTokenDto.getTelephone());
        insertCrmLog.setCrmPoint(waitConvertPoint);
        insertCrmLog.setPointRate(rate.toString());
        insertCrmLog.setMmgPoint(number);
        insertCrmLog.setTrueMmgPoint(number * 10);
        //insertCrmLog.setClAddtime(TimeUtil.toPhpIntegerTime(new Date()));
        insertCrmLog.setExecuteStatus(CRM_EXECUTE_STATUS_FAIL);
        insertCrmLog.setRequestParam(xml);
        insertCrmLog.setCreatorId(memberId);

        if (pointCrmLogDao.insert(insertCrmLog) <= 0) {
            log.error("写CRM积分转换日志表失败!insertCrmLog={}", JSON.toJSONString(insertCrmLog));
            throw new GlobalException(PointExceptionEnum.MEMBER_POINT_CRM_LOG_FAIL);
        }
        Long crmLogId = insertCrmLog.getId();

        Holder<String> errCode = new Holder<String>();
        Holder<String> errDesc = new Holder<String>();
        Holder<String> outXml = new Holder<String>();
        Holder<String> redeemPointsBal = new Holder<String>();
        Holder<String> needPoints = new Holder<String>();
        CRMRedemptionWS cRMRedemptionWS = new CRMRedemptionWS();
        NewRedemptionOrder newRedemptionOrder = cRMRedemptionWS.getNewRedemptionOrder();
        newRedemptionOrder.newRedemptionOrder(xml, "积分商城", errCode, errDesc, needPoints, outXml, redeemPointsBal);
        Date afterDate = new Date();
        log.info("积分换购crm返回的状态:" + JSON.toJSONString(errCode) + " 描述:" + JSON.toJSONString(errDesc));
        log.info("积分换购返回的xml:" + JSON.toJSONString(outXml));
        PointCrmLogEntity updateCrmLog = new PointCrmLogEntity();
        updateCrmLog.cleanInit();
        updateCrmLog.setId(crmLogId);
        updateCrmLog.setResponResult(errCode + ":" + outXml.value);
        updateCrmLog.setExecuteTime(String.valueOf((afterDate.getTime() - preDate.getTime()) / 1000.00));

        if ("100".equals(errCode.value)) {//成功
            updateCrmLog.setExecuteStatus(CRM_EXECUTE_STATUS_SUCCESS);
            pointCrmLogDao.updateById(updateCrmLog);
            return crmLogId;
        }
        pointCrmLogDao.updateById(updateCrmLog);
        log.error("积分转换：扣减crm积分失败memberId={},reduceCrmPoint={},crmMemberId={}", memberTokenDto.getMemberId(), waitConvertPoint, crmMemberId);
        throw new GlobalException(PointExceptionEnum.MEMBER_POINT_CRM_REDUCE_ERR);
    }


    /**
     * 获得某个用户 贝因美crm系统积分总数
     *
     * @return
     */
    @Override
    public CrmPointSumVo getCrmPointSum(MemberTokenDto memberTokenDto) {
        CrmPointSumVo crmPointSumVo = new CrmPointSumVo();
        CrmContact crmContact = getCrmMemberInfo(memberTokenDto.getTelephone());
        Double crmPointsBalance = null;
        if (null == crmContact) {
            log.info("贝因美crm中没有该用户信息memberTokenDto={}", JSON.toJSONString(memberTokenDto));
            crmPointsBalance = new Double(0);
            crmPointSumVo.setCrmPoint(crmPointsBalance);
            crmPointSumVo.setMmgPoint(0);
            return crmPointSumVo;
        }
        crmPointsBalance = crmContact.getRedeemPointsBalance();
        if (null == crmPointsBalance) {
            crmPointsBalance = new Double(0);
        }
        crmPointSumVo.setCrmPoint(crmPointsBalance);
        BigDecimal crmPoint = new BigDecimal(crmPointsBalance);
        crmPointSumVo.setMmgPoint(getTrueMmgPoint(crmPoint.divide(crmPointToMmgPointRate).intValue()));
        crmPointSumVo.setCrmPointTop(getCrmPointTop(crmPoint));
        return crmPointSumVo;
    }

    private Integer getCrmPointTop(BigDecimal crmPointsBalance) {
        Integer crmPointTop = crmPointsBalance.divide(crmPointToMmgPointRate).intValue() * 5;
        return crmPointTop;
    }

    private Integer getTrueMmgPoint(Integer mmgPoint) {
        if (null == mmgPoint) {
            return 0;
        }
        return mmgPoint * 10;
    }

    @Override
    @JedisOperation
    public boolean doCrmPointToMmgPoint(MemberTokenDto memberTokenDto, Integer reduceCrmPoint) {
        //判断手机号
        String telephone = memberTokenDto.getTelephone();
        if (StringUtils.isBlank(telephone)) {
            throw new GlobalException(PointExceptionEnum.MEMBER_POINT_CONVERT_TELEPHONE_NULL);
        }
        tryRedisLock("doCrmPointToMmgPoint" + telephone);

        //判断开关,如果关闭了 则判断是否在白名单中
        String switchConfig = configApi.getConfig(CRM_POINT_SWITCH_CONFIG);
        if (null == switchConfig || !CRM_POINT_SWITCH_TRUE.equals(switchConfig)) {
            String whitePhoneConfig = configApi.getConfig(CRM_WHITE_PHONE_CONFIG);
            if (null == whitePhoneConfig) {
                throw new GlobalException(PointExceptionEnum.MEMBER_POINT_CRM_POINT_CLOSE);
            }
            List<String> whitePhoneList = JSON.parseArray(whitePhoneConfig, String.class);
            if (!whitePhoneList.contains(telephone)) {
                throw new GlobalException(PointExceptionEnum.MEMBER_POINT_CRM_POINT_CLOSE);
            }
        }

        Long meberId = memberTokenDto.getMemberId();
        if (null == reduceCrmPoint || reduceCrmPoint <= 0) {
            throw new GlobalException(PointExceptionEnum.MEMBER_POINT_CONVERT_PARAM);
        }

        //原来 5个贝因美积转1个妈妈购积分 改成 1个贝因美积分转成2个妈妈购积分 1/2 -> 0.5
        BigDecimal reduceCrmPointBig = new BigDecimal(reduceCrmPoint);
        Integer addMmgPoint = reduceCrmPointBig.divide(crmPointToMmgPointRate).intValue();
        BigDecimal addMmgPointBig = new BigDecimal(addMmgPoint);

        if (addMmgPointBig.multiply(crmPointToMmgPointRate).compareTo(reduceCrmPointBig) != 0) {
            throw new GlobalException(PointExceptionEnum.MEMBER_POINT_MULTIPLE_ERR);
        }
        //调用crm的webservice接口获取crm中该会员的信息
        CrmContact crmContact = getCrmMemberInfo(telephone);
        if (null == crmContact) {
            log.error("积分转换：该手机号在贝因美crm不存在信息memberId={},telephone={}", meberId, telephone);
            throw new GlobalException(PointExceptionEnum.MEMBER_POINT_CRM_LIMIT);
        }
        String crmMemberId = crmContact.getMemberId();
        Double crmPointsBalance = crmContact.getRedeemPointsBalance();

        if (reduceCrmPoint > crmPointsBalance) {
            log.error("积分转换：转换数量大于crm实际数量memberId={},reduceCrmPoint={},crmContact={}", meberId, reduceCrmPoint,
                    JSON.toJSONString(crmContact));
            throw new GlobalException(PointExceptionEnum.MEMBER_POINT_CRM_LIMIT);
        }
        log.info("beingmate Integral==:" + reduceCrmPoint);
        if (reduceCrmPoint <= crmPointsBalance) {
            // return true;
            Long crmLogId = executeCrmToMmg(crmMemberId, reduceCrmPoint, addMmgPoint, crmPointToMmgPointRate.doubleValue(), memberTokenDto);

            log.info("积分转换-新增mmg积分memberId={},addMmgPoint={}", meberId, addMmgPoint);
            PointChangeDto pointChangeDto = new PointChangeDto();
            pointChangeDto.setMemberId(memberTokenDto.getMemberId());
            pointChangeDto.setTelephone(memberTokenDto.getTelephone());
            pointChangeDto.setUserName(memberTokenDto.getUserName());
            pointChangeDto.setPoint(getTrueMmgPoint(addMmgPoint));
            pointChangeDto.setOperateType(PointOperateType.CRM_TO_MMG);
            pointChangeDto.setOperateSn("integral_convert" + crmLogId);
            addOrReducePoint(pointChangeDto);
        }
        return true;
    }

    @Autowired
    private MemberPointLogService memberPointLogService;

    @Override
    public PointCrmLogReturnVo getPointCrmLog(Long memberId, Long lastLogId) {
        PointCrmLogEntity param = new PointCrmLogEntity();
        param.cleanInit();
        param.setMemberId(memberId);
        param.setExecuteStatus(CRM_EXECUTE_STATUS_SUCCESS);
        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        EntityWrapper<PointCrmLogEntity> wrapperParam = new EntityWrapper<>();
        wrapperParam.setEntity(param);
        if (null != lastLogId && lastLogId > 0) {
            wrapperParam.lt("id", lastLogId);
        }
        wrapperParam.orderBy("id", false);
        wrapperParam.last("limit 10");
        List<PointCrmLogEntity> pointCrmLogEntityList = pointCrmLogDao.selectList(wrapperParam);
        if (CollectionUtils.isEmpty(pointCrmLogEntityList)) {
            return null;
        }


        List<String> operateSnList = memberPointUtil.extractOperateSn(pointCrmLogEntityList);
        Map<String, PointLogDto> pointLogDtoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(operateSnList)) {

            Future<Map<String, PointLogDto>> future = logExecutor.submit(new Callable<Map<String, PointLogDto>>() {
                @Override
                public Map<String, PointLogDto> call() throws Exception {
                    return memberPointLogService.getPointLogList(operateSnList);
                }
            });
            try {
                pointLogDtoMap = future.get(2000, TimeUnit.MILLISECONDS);
            } catch (Exception ex) {
                log.error("异步调用getPointLogList异常={}", ex.getMessage());
            }
        }
        List<PointCrmLogDto> pointCrmLogDtoList = new ArrayList<>();
        for (PointCrmLogEntity pointCrmLogEntity : pointCrmLogEntityList) {
            PointCrmLogDto pointCrmLogDto = new PointCrmLogDto();
            BeanCopyUtil.copy(pointCrmLogEntity, pointCrmLogDto);
            PointLogDto pointLogDto = (null == pointLogDtoMap) ? null : pointLogDtoMap.get("integral_convert" + pointCrmLogDto.getId());
            if (null != pointLogDto) {
                pointCrmLogDto.setAfterMmgPoint(pointLogDto.getAfterPoint());
            }
            pointCrmLogDtoList.add(pointCrmLogDto);
            lastLogId = pointCrmLogDto.getId();
        }
        PointCrmLogReturnVo pointCrmLogReturnVo = new PointCrmLogReturnVo();
        pointCrmLogReturnVo.setList(pointCrmLogDtoList);
        pointCrmLogReturnVo.setLastLogId(lastLogId);
        return pointCrmLogReturnVo;
    }

    @Override
    public Integer getMmgPointSum(Long memberId) {
        MemberAssetEntity param = new MemberAssetEntity();
        param.cleanInit();
        param.setMemberId(memberId);
        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        MemberAssetEntity memberAssetEntity = memberAssetDao.selectOne(param);
        if (null == memberAssetEntity || null == memberAssetEntity.getPoint()
                || memberAssetEntity.getPoint() < 1) {
            return 0;
        }
        return memberAssetEntity.getPoint();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)

    public Map<Long, PointCrmLogDto> getCrmPointLogList(List<Long> crmLogIdList) {
        if (CollectionUtils.isEmpty(crmLogIdList)) {
            return null;
        }
        EntityWrapper<PointCrmLogEntity> condition = new EntityWrapper<>();
        condition.in("id", crmLogIdList);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<PointCrmLogEntity> pointCrmLogEntityList = pointCrmLogDao.selectList(condition);
        if (CollectionUtils.isEmpty(pointCrmLogEntityList)) {
            return null;
        }
        Map<Long, PointCrmLogDto> pointCrmLogDtoMap = new HashMap<>();
        for (PointCrmLogEntity pointCrmLogEntity : pointCrmLogEntityList) {
            PointCrmLogDto pointCrmLogDto = new PointCrmLogDto();
            BeanCopyUtil.copy(pointCrmLogEntity, pointCrmLogDto);
            pointCrmLogDtoMap.put(pointCrmLogEntity.getId(), pointCrmLogDto);
        }
        return pointCrmLogDtoMap;
    }

    @Autowired
    private MemberService memberService;

    private final static String REDRESS_ASSET_WHITE_PHONE_CONFIG = "redressAssetWhitePhone";
    private final static String REDRESS_ASSET_SWITCH_CONFIG = "redressAssetSwitch";
    private final static String REDRESS_ASSET_SWITCH_TRUE = "true";

    @Override
    @JedisOperation
    public Boolean redressAssetData(RedressAssetPo redressAssetPo) {
        Long memberId = redressAssetPo.getMemberId();
        String loginAccount = redressAssetPo.getLoginAccount();

        MemberDto memberDto = memberService.getMemberByNameOrPhone(loginAccount);
        if (null == memberDto) {
            throw new GlobalException(PointExceptionEnum.MEMBER_ASSET_REDRESS_MEMBER_NULL);
            //修正会员资产数据---会员信息异常
        }
        if (null == memberId || memberId <= 0 || !memberId.equals(memberDto.getId())) {
            //传入待修正会员信息不匹配
            throw new GlobalException(PointExceptionEnum.MEMBER_ASSET_REDRESS_MEBER_NO_MATCH);
        }


        //判断开关,如果关闭了 则判断是否在白名单中
        String switchConfig = configApi.getConfig(REDRESS_ASSET_SWITCH_CONFIG);
        if (null == switchConfig || !REDRESS_ASSET_SWITCH_TRUE.equals(switchConfig)) {
            String whitePhoneConfig = configApi.getConfig(REDRESS_ASSET_WHITE_PHONE_CONFIG);
            if (null == whitePhoneConfig) {
                throw new GlobalException(PointExceptionEnum.MEMBER_ASSET_REDRESS_CLOSE);
            }
            List<String> whitePhoneList = JSON.parseArray(whitePhoneConfig, String.class);
            if (!whitePhoneList.contains(memberDto.getTelephone())) {
                throw new GlobalException(PointExceptionEnum.MEMBER_ASSET_REDRESS_CLOSE);
            }
        }


        AssetChangeDto assetChangeDto = new AssetChangeDto();
        assetChangeDto.setMemberId(memberId);
        assetChangeDto.setTelephone(memberDto.getTelephone());
        assetChangeDto.setUserName(memberDto.getUserName());
        assetChangeDto.setBalance(redressAssetPo.getBalance());
        assetChangeDto.setPoint(redressAssetPo.getPoint());
        assetChangeDto.setOperateSn(redressAssetPo.getOperateSn());
        assetChangeDto.setOperateType(AssetOperateType.REDRESS);
        assetChangeDto.setMemo(redressAssetPo.getRedressMemo());
        return useOrRollbackAccountAssets(assetChangeDto);
    }

}