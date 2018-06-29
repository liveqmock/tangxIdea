package com.topaiebiz.member.point.service;

import com.topaiebiz.member.dto.point.AssetChangeDto;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-02-24 11:51
 */
public interface MemberBalanceLogService {
    void saveBalanceLog(Long memberId, AssetChangeDto assetChangeDto, BigDecimal balance);
}