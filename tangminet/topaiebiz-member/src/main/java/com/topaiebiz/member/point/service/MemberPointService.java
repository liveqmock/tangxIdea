package com.topaiebiz.member.point.service;

import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.dto.point.AssetChangeDto;
import com.topaiebiz.member.dto.point.MemberAssetDto;
import com.topaiebiz.member.dto.point.PointChangeDto;
import com.topaiebiz.member.dto.point.PointCrmLogDto;
import com.topaiebiz.member.po.RedressAssetPo;
import com.topaiebiz.member.vo.CrmPointSumVo;
import com.topaiebiz.member.vo.PointCrmLogReturnVo;

import java.util.List;
import java.util.Map;

public interface MemberPointService {

    boolean useOrRollbackAccountAssets(AssetChangeDto assetChangeDto);

    boolean addOrReducePoint(PointChangeDto pointChangeDto);

    MemberAssetDto getMemberAsset(Long memberId);

    CrmPointSumVo getCrmPointSum(MemberTokenDto memberTokenDto);

    Map<Long, MemberAssetDto> getMemberAssetMap(List<Long> memberIdList);

    boolean doCrmPointToMmgPoint(MemberTokenDto memberTokenDto, Integer reduceCrmPoint);

    PointCrmLogReturnVo getPointCrmLog(Long memberId, Long lastLogId);

    Integer getMmgPointSum(Long memberId);

    Map<Long, PointCrmLogDto> getCrmPointLogList(List<Long> crmLogIdList);

    Boolean redressAssetData(RedressAssetPo redressAssetPo);
}
