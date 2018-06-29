package com.topaiebiz.member.point.utils;

import com.topaiebiz.member.constants.PointOperateType;
import com.topaiebiz.member.dto.point.AssetChangeDto;
import com.topaiebiz.member.dto.point.PointChangeDto;
import com.topaiebiz.member.point.entity.MemberBalanceLogEntity;
import com.topaiebiz.member.point.entity.MemberPointLogEntity;
import com.topaiebiz.member.point.entity.PointCrmLogEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ward on 2018-01-18.
 */
@Component
public class MemberPointUtil {

    public MemberPointLogEntity packagePointLogEntity(AssetChangeDto assetChangeDto, Integer beforePoint) {
        Long memberId = assetChangeDto.getMemberId();
        Integer pointChange = assetChangeDto.getPoint();
        MemberPointLogEntity pointLog = new MemberPointLogEntity();
        pointLog.setMemberId(memberId);
        pointLog.setUserName(assetChangeDto.getUserName());
        pointLog.setTelephone(assetChangeDto.getTelephone());
        pointLog.setBeforePoint(beforePoint);
        pointLog.setPointChange(pointChange);
        pointLog.setAfterPoint(beforePoint + pointChange);
        pointLog.setOperateType(assetChangeDto.getOperateType().operateType);
        pointLog.setOperateDesc(assetChangeDto.getOperateType().operateBalanceDesc);
        pointLog.setCreatorId(memberId);
        pointLog.setOperateSn(assetChangeDto.getOperateSn());
        pointLog.setMeno(assetChangeDto.getMemo());
        return pointLog;

    }

    public MemberBalanceLogEntity packageBalanceLogEntity(AssetChangeDto assetChangeDto, BigDecimal beforeBalance) {
        Long memberId = assetChangeDto.getMemberId();
        BigDecimal balanceChange = assetChangeDto.getBalance();
        MemberBalanceLogEntity balanceLog = new MemberBalanceLogEntity();
        balanceLog.setMemberId(memberId);
        balanceLog.setUserName(assetChangeDto.getUserName());
        balanceLog.setTelephone(assetChangeDto.getTelephone());
        balanceLog.setBeforeBalance(beforeBalance);
        balanceLog.setBalanceChange(balanceChange);
        balanceLog.setAfterBalance(beforeBalance.add(balanceChange));
        balanceLog.setCreatorId(memberId);
        balanceLog.setOperateSn(assetChangeDto.getOperateSn());
        balanceLog.setOperateType(assetChangeDto.getOperateType().operateType);
        balanceLog.setOperateDesc(assetChangeDto.getOperateType().operatePointDesc);
        balanceLog.setCreatorId(memberId);
        balanceLog.setOperateSn(assetChangeDto.getOperateSn());
        balanceLog.setMeno(assetChangeDto.getMemo());
        return balanceLog;
    }

    public MemberPointLogEntity packagePointLogEntity(PointChangeDto pointChangeDto, Integer beforePoint) {
        Long memberId = pointChangeDto.getMemberId();
        Integer pointChange = pointChangeDto.getPoint();
        MemberPointLogEntity pointLog = new MemberPointLogEntity();
        pointLog.setMemberId(memberId);
        pointLog.setUserName(pointChangeDto.getUserName());
        pointLog.setTelephone(pointChangeDto.getTelephone());
        pointLog.setBeforePoint(beforePoint);
        pointLog.setPointChange(pointChange);
        pointLog.setAfterPoint(beforePoint + pointChange);
        PointOperateType operateType = pointChangeDto.getOperateType();
        if (null != operateType) {
            pointLog.setOperateType(operateType.getOperateType());
            pointLog.setOperateDesc(operateType.getOperateDesc());
        }
        pointLog.setCreatorId(memberId);
        pointLog.setOperateSn(pointChangeDto.getOperateSn());
        pointLog.setMeno(pointChangeDto.getMemo());
        return pointLog;
    }

    public List<Long> extractCrmLogId(List<MemberPointLogEntity> pointLogEntityList) {
        List<Long> crmLogIdList = new ArrayList<>();
        for (MemberPointLogEntity pointLog : pointLogEntityList) {
            //integral_convert972429776680763394
            if (StringUtils.isBlank(pointLog.getOperateSn()) ||
                    !"integral_convert".equals(pointLog.getOperateType())) {
                continue;
            }
            String operateSn = pointLog.getOperateSn().trim().replace("integral_convert", "");
            Long crmLogId = new Long(operateSn);
            if (crmLogId <= 0 || !operateSn.equals(crmLogId.toString())) {
                continue;
            }
            crmLogIdList.add(crmLogId);
        }
        return crmLogIdList;
    }

    public Long extractCrmLogId(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        String operateSn = string.trim().replace("integral_convert", "");
        Long crmLogId = new Long(operateSn);
        if (crmLogId <= 0 || !operateSn.equals(crmLogId.toString())) {
            return null;
        }
        return crmLogId;
    }

    public List<String> extractOperateSn(List<PointCrmLogEntity> pointCrmLogEntityList) {
        if (CollectionUtils.isEmpty(pointCrmLogEntityList)) {
            return null;
        }
        List<String> operateSnList = new ArrayList<>();
        for (PointCrmLogEntity pointCrmLogEntity : pointCrmLogEntityList) {
            operateSnList.add(StringUtils.join("integral_convert", pointCrmLogEntity.getId()));
        }
        return operateSnList;
    }
}
