package com.topaiebiz.member.api;

import com.topaiebiz.member.dto.point.AssetChangeDto;
import com.topaiebiz.member.dto.point.MemberAssetDto;
import com.topaiebiz.member.dto.point.PointChangeDto;

/**
 * Created by ward on 2018-01-15.
 */
public interface PointApi {

    /**
     * 用户资产的使用（积分+余额），需要弱化余额 故将其放在积分模块
     */
    boolean useAccountAssets(AssetChangeDto assetChangeDto);

    boolean rollbackAccountAssets(AssetChangeDto assetChangeDto);

    boolean addPoint(PointChangeDto pointChangeDto);

    boolean consumePoint(PointChangeDto pointChangeDto);

    MemberAssetDto getMemberAsset(Long memberId);
}
