package com.topaiebiz.member.point.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.sharding.UseShardingDataSource;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.constants.PointOperateType;
import com.topaiebiz.member.dto.point.*;
import com.topaiebiz.member.exception.PointExceptionEnum;
import com.topaiebiz.member.po.PointHistoryPo;
import com.topaiebiz.member.point.dao.MemberBalanceLogDao;
import com.topaiebiz.member.point.dao.MemberPointLogDao;
import com.topaiebiz.member.point.entity.MemberBalanceLogEntity;
import com.topaiebiz.member.point.entity.MemberPointLogEntity;
import com.topaiebiz.member.point.service.MemberPointLogService;
import com.topaiebiz.member.point.service.MemberPointService;
import com.topaiebiz.member.point.utils.MemberPointUtil;
import com.topaiebiz.member.vo.PointHistoryReturnVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/***
 * @author yfeng
 * @date 2018-02-23 16:19
 */
@Slf4j
@Service
public class MemberPointLogServiceImpl implements MemberPointLogService {

    @Autowired
    private MemberPointLogDao memberPointLogDao;

    @Autowired
    private MemberBalanceLogDao memberBalanceLogDao;


    @Autowired
    private MemberPointUtil memberPointUtil;

    private MemberPointLogEntity getPointLog(Long memberId, String operateType, String operateSn) {
        MemberPointLogEntity param = new MemberPointLogEntity();
        param.cleanInit();
        param.setMemberId(memberId);
        param.setOperateType(operateType);
        param.setOperateSn(operateSn);
        return memberPointLogDao.selectOne(param);
    }

    @Autowired
    private MemberPointService memberPointService;

    private static int coreSize = 4;
    private ExecutorService logExecutor = new ThreadPoolExecutor(coreSize, coreSize, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));


    @Override
    @UseShardingDataSource
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PointHistoryReturnVo getPointHistory(Long memberId, PointHistoryPo pointHistoryPo) {
        List<MemberPointLogEntity> pointLogEntityList = getMemberPointLogEntities(memberId, pointHistoryPo);
        if (CollectionUtils.isEmpty(pointLogEntityList)) {
            return null;
        }
        List<Long> crmLogIdList = memberPointUtil.extractCrmLogId(pointLogEntityList);
        Map<Long, PointCrmLogDto> pointCrmLogDtoMap = asyncGetCrmLogMap(crmLogIdList);
        Map<Long, List<PointLogDto>> pointLogDtoMap = intPointLongTreeMap();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        Long lastLogId = null;
        for (MemberPointLogEntity pointLog : pointLogEntityList) {
            //1.------------------
            PointLogDto pointLogDto = new PointLogDto();
            BeanCopyUtil.copy(pointLog, pointLogDto);
            PointOperateType operType = PointOperateType.get(pointLogDto.getOperateType());
            if (null != operType) {
                pointLogDto.setOperateDesc(operType.getOperateDesc());
            }
            if (PointOperateType.CRM_TO_MMG.equals(PointOperateType.get(pointLog.getOperateType()))) {
                PointCrmLogDto pointCrmLogDto = pointCrmLogDtoMap.get(memberPointUtil.extractCrmLogId(pointLog.getOperateSn()));
                if (null != pointCrmLogDto && pointCrmLogDto.getCrmPoint() > 0) {
                    pointLogDto.setCrmPointChange(0 - pointCrmLogDto.getCrmPoint());
                }
            }
            //2-------------------
            Long key = getAggregateKey(formatter, pointLog.getCreatedTime());
            List<PointLogDto> temp = pointLogDtoMap.get(key);
            if (CollectionUtils.isEmpty(temp)) {
                temp = new ArrayList<>();
            }
            temp.add(pointLogDto);
            pointLogDtoMap.put(key, temp);
            //3-------------------
            lastLogId = pointLog.getId();
        }
        List<PointDateLogsDto> list = new ArrayList<>();
        for (Map.Entry<Long, List<PointLogDto>> entry : pointLogDtoMap.entrySet()) {
            PointDateLogsDto temp = new PointDateLogsDto();
            temp.setShowDate(formatter.format(new Date(entry.getKey())));
            temp.setShowList(entry.getValue());
            list.add(temp);
        }
        PointHistoryReturnVo pointHistoryReturnVo = new PointHistoryReturnVo();
        pointHistoryReturnVo.setList(list);
        pointHistoryReturnVo.setLastLogId(lastLogId);
        return pointHistoryReturnVo;
    }

    private TreeMap<Long, List<PointLogDto>> intPointLongTreeMap() {
        return new TreeMap<>(new Comparator<Long>() {
            /*
            * int compare(Object o1, Object o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
            */
            @Override
            public int compare(Long a, Long b) {
                if (b - a > 0) {
                    return 1;
                } else if (b - a < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    private Long getAggregateKey(SimpleDateFormat formatter, Date date) {
        String tempStr = formatter.format(date);
        Date tempDate = null;
        try {
            tempDate = formatter.parse(tempStr);
        } catch (ParseException e) {
            log.error("时间转换异常e={}", e.getMessage());
        }
        return tempDate.getTime();
    }

    private Map<Long, PointCrmLogDto> asyncGetCrmLogMap(final List<Long> crmLogIdList) {
        Map<Long, PointCrmLogDto> pointCrmLogDtoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(crmLogIdList)) {
            //异步查询
            Future<Map<Long, PointCrmLogDto>> future = logExecutor.submit(new Callable<Map<Long, PointCrmLogDto>>() {
                @Override
                public Map<Long, PointCrmLogDto> call() throws Exception {
                    return memberPointService.getCrmPointLogList(crmLogIdList);
                }
            });
            try {
                pointCrmLogDtoMap = future.get(2000, TimeUnit.MILLISECONDS);
            } catch (Exception ex) {
                log.error("异步调用getCrmPointLogList异常={}", ex.getMessage());
            }

        }
        return pointCrmLogDtoMap;
    }

    private List<MemberPointLogEntity> getMemberPointLogEntities(Long memberId, PointHistoryPo pointHistoryPo) {
        EntityWrapper<MemberPointLogEntity> wrapperParam = new EntityWrapper<>();
        MemberPointLogEntity param = new MemberPointLogEntity();
        param.cleanInit();
        param.setMemberId(memberId);
        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        wrapperParam.setEntity(param);
        if (null != pointHistoryPo.getLastLogId() && pointHistoryPo.getLastLogId() > 0) {
            wrapperParam.lt("id", pointHistoryPo.getLastLogId());
        }
        if (null == pointHistoryPo.getIsIncome()) {
            wrapperParam.ne("pointChange", 0);
        } else if (true == pointHistoryPo.getIsIncome()) {
            //收入
            wrapperParam.gt("pointChange", 0);
        } else {
            //支出（消费）
            wrapperParam.lt("pointChange", 0);
        }
        wrapperParam.last("limit 100");
        wrapperParam.orderBy("createdTime", false);
        return memberPointLogDao.selectList(wrapperParam);
    }

    @Override
    @UseShardingDataSource
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Deprecated
    public void savePointLog(Long memberId, AssetChangeDto assetChangeDto, Integer beforePoint) {
        if (assetChangeDto.getPoint() == null || assetChangeDto.getPoint().intValue() == 0) {
            return;
        }
        String operateType = assetChangeDto.getOperateType().operateType;
        String operateSn = assetChangeDto.getOperateSn();

        MemberPointLogEntity memberPointLogEntity = getPointLog(memberId, operateType, operateSn);
        if (null != memberPointLogEntity) {
            log.error("资产-积分变化重复请求memberId={},change={}", memberId, JSON.toJSONString(assetChangeDto));
            throw new GlobalException(PointExceptionEnum.MEMBER_ASSET_REPEAT_REQUESET);
        }
        MemberPointLogEntity pointLog = memberPointUtil.packagePointLogEntity(assetChangeDto, beforePoint);
        log.info("积分变化日志 {}", JSON.toJSONString(pointLog));
        memberPointLogDao.insert(pointLog);
    }

    @Override
    @UseShardingDataSource
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Boolean savePointLog(Long memberId, PointChangeDto pointChangeDto, Integer beforePoint) {
        String operateType = pointChangeDto.getOperateType().getOperateType();
        String operateSn = pointChangeDto.getOperateSn();

        MemberPointLogEntity memberPointLogEntity = getPointLog(memberId, operateType, operateSn);
        if (null != memberPointLogEntity) {
            log.error("积分变化重复提交memberId={} change={}", memberId, JSON.toJSONString(pointChangeDto));
            throw new GlobalException(PointExceptionEnum.MEMBER_ASSET_REPEAT_REQUESET);
        }
        MemberPointLogEntity pointLog = memberPointUtil.packagePointLogEntity(pointChangeDto, beforePoint);
        log.info("积分变化日志 {}", JSON.toJSONString(pointLog));
        memberPointLogDao.insert(pointLog);
        return true;
    }

    @Override
    @UseShardingDataSource
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Map<String, PointLogDto> getPointLogList(List<String> operateSnList) {
        if (CollectionUtils.isEmpty(operateSnList)) {
            return null;
        }
        EntityWrapper<MemberPointLogEntity> condition = new EntityWrapper<>();
        condition.in("operateSn", operateSnList);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<MemberPointLogEntity> pointLogEntityList = memberPointLogDao.selectList(condition);
        if (CollectionUtils.isEmpty(pointLogEntityList)) {
            return null;
        }
        Map<String, PointLogDto> pointLogDtoMap = new HashMap<>();
        for (MemberPointLogEntity pointLogEntity : pointLogEntityList) {
            PointLogDto pointLogDto = new PointLogDto();
            BeanCopyUtil.copy(pointLogEntity, pointLogDto);
            pointLogDtoMap.put(pointLogEntity.getOperateSn(), pointLogDto);
        }
        return pointLogDtoMap;


    }


    private MemberBalanceLogEntity getBalanceLog(Long memberId, String operateType, String operateSn) {
        MemberBalanceLogEntity param = new MemberBalanceLogEntity();
        param.cleanInit();
        param.setMemberId(memberId);
        param.setOperateType(operateType);
        param.setOperateSn(operateSn);
        return memberBalanceLogDao.selectOne(param);
    }

    @Override
    @UseShardingDataSource
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Boolean saveAssetLog(Long memberId, AssetChangeDto assetChangeDto, Integer beforePoint, BigDecimal beforeBalance) {
        String operateType = assetChangeDto.getOperateType().operateType;
        String operateSn = assetChangeDto.getOperateSn();
        if (0 != assetChangeDto.getPoint()) {
            MemberPointLogEntity memberPointLogEntity = getPointLog(memberId, operateType, operateSn);
            if (null != memberPointLogEntity) {
                log.error("积分变化重复提交memberId={} change={}", memberId, JSON.toJSONString(assetChangeDto));
                throw new GlobalException(PointExceptionEnum.MEMBER_ASSET_REPEAT_REQUESET);
            }
            MemberPointLogEntity pointLog = memberPointUtil.packagePointLogEntity(assetChangeDto, beforePoint);
            log.info("积分变化日志 {}", JSON.toJSONString(pointLog));
            memberPointLogDao.insert(pointLog);
        }

        if (BigDecimal.ZERO.compareTo(assetChangeDto.getBalance()) != 0) {
            MemberBalanceLogEntity balanceLogEntity = getBalanceLog(memberId, operateType, operateSn);
            if (null != balanceLogEntity) {
                log.error("资产-余额变化重复请求memberId={},change={}", memberId, JSON.toJSONString(assetChangeDto));
                throw new GlobalException(PointExceptionEnum.MEMBER_ASSET_REPEAT_REQUESET);
            }
            MemberBalanceLogEntity balanceLog = memberPointUtil.packageBalanceLogEntity(assetChangeDto, beforeBalance);
            log.info("余额变化日志 {}", JSON.toJSONString(balanceLog));
            memberBalanceLogDao.insert(balanceLog);
        }
        return true;
    }


}