package com.topaiebiz.member.point.service.impl;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.common.sharding.UseShardingDataSource;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.dto.point.AssetChangeDto;
import com.topaiebiz.member.exception.PointExceptionEnum;
import com.topaiebiz.member.point.dao.MemberBalanceLogDao;
import com.topaiebiz.member.point.entity.MemberBalanceLogEntity;
import com.topaiebiz.member.point.service.MemberBalanceLogService;
import com.topaiebiz.member.point.utils.MemberPointUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-02-24 11:53
 */
@Service
@Slf4j
public class MemberBalanceLogServiceImpl implements MemberBalanceLogService {
    @Autowired
    private MemberBalanceLogDao memberBalanceLogDao;

    @Autowired
    private MemberPointUtil memberPointUtil;

    @Override
    @UseShardingDataSource
    @Deprecated
    public void saveBalanceLog(Long memberId, AssetChangeDto assetChangeDto, BigDecimal beforeBalance) {
        if (BigDecimal.ZERO.compareTo(assetChangeDto.getBalance()) == 0) {
            return;
        }
        String operateType = assetChangeDto.getOperateType().operateType;
        String operateSn = assetChangeDto.getOperateSn();

        MemberBalanceLogEntity balanceLogEntity = getBalanceLog(memberId, operateType, operateSn);
        if (null != balanceLogEntity) {
            log.error("资产-余额变化重复请求memberId={},change={}", memberId, JSON.toJSONString(assetChangeDto));
            throw new GlobalException(PointExceptionEnum.MEMBER_ASSET_REPEAT_REQUESET);
        }
        MemberBalanceLogEntity balanceLog = memberPointUtil.packageBalanceLogEntity(assetChangeDto, beforeBalance);
        log.info("余额变化日志 {}", JSON.toJSONString(balanceLog));
        memberBalanceLogDao.insert(balanceLog);
    }

    private MemberBalanceLogEntity getBalanceLog(Long memberId, String operateType, String operateSn) {
        MemberBalanceLogEntity param = new MemberBalanceLogEntity();
        param.cleanInit();
        param.setMemberId(memberId);
        param.setOperateType(operateType);
        param.setOperateSn(operateSn);
        return memberBalanceLogDao.selectOne(param);
    }
}
