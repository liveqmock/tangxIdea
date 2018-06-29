package com.topaiebiz.member.point.service;

import com.topaiebiz.member.dto.point.AssetChangeDto;
import com.topaiebiz.member.dto.point.PointChangeDto;
import com.topaiebiz.member.dto.point.PointLogDto;
import com.topaiebiz.member.po.PointHistoryPo;
import com.topaiebiz.member.vo.PointHistoryReturnVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-02-23 16:17
 */
public interface MemberPointLogService {

    void savePointLog(Long memberId, AssetChangeDto assetChangeDto, Integer pointNum);

    Boolean savePointLog(Long memberId, PointChangeDto pointChangeDto, Integer pointNum);

    PointHistoryReturnVo getPointHistory(Long memberId, PointHistoryPo pointHistoryPo);

    Map<String, PointLogDto> getPointLogList(List<String> operateSnList);

    Boolean saveAssetLog(Long memberId, AssetChangeDto assetChangeDto, Integer beforePoint, BigDecimal beforeBalance);
}