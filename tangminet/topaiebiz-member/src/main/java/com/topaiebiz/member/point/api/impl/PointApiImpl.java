package com.topaiebiz.member.point.api.impl;

import com.topaiebiz.member.api.PointApi;
import com.topaiebiz.member.dto.point.AssetChangeDto;
import com.topaiebiz.member.dto.point.MemberAssetDto;
import com.topaiebiz.member.dto.point.PointChangeDto;
import com.topaiebiz.member.point.service.MemberPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by ward on 2018-01-18.
 */
@Component
public class PointApiImpl implements PointApi {

    @Autowired
    private MemberPointService memberPointService;

    @Override
    public boolean useAccountAssets(AssetChangeDto assetChangeDto) {
        assetChangeDto.setPoint(0 - assetChangeDto.getPoint());
        assetChangeDto.setBalance(BigDecimal.ZERO.subtract(assetChangeDto.getBalance()));
        return memberPointService.useOrRollbackAccountAssets(assetChangeDto);
    }

    @Override
    public boolean rollbackAccountAssets(AssetChangeDto assetChangeDto) {
        return memberPointService.useOrRollbackAccountAssets(assetChangeDto);
    }

    @Override
    public boolean addPoint(PointChangeDto pointChangeDto) {
        return memberPointService.addOrReducePoint(pointChangeDto);
    }

    @Override
    public boolean consumePoint(PointChangeDto pointChangeDto) {
        pointChangeDto.setPoint(0 - pointChangeDto.getPoint());
        return memberPointService.addOrReducePoint(pointChangeDto);
    }

    @Override
    public MemberAssetDto getMemberAsset(Long memberId) {
        return memberPointService.getMemberAsset(memberId);
    }
}
