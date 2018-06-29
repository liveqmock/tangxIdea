package com.topaiebiz.trade.order.facade;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.member.api.PointApi;
import com.topaiebiz.member.dto.point.AssetChangeDto;
import com.topaiebiz.member.dto.point.MemberAssetDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-21 10:49
 */
@Slf4j
@Component
public class ScoreServiceFacade {
    @Autowired
    private PointApi pointApi;

    public boolean useAccountAssets(AssetChangeDto assetChangeDto) {
        boolean result = false;
        try {
            log.info("pointApi.getMemberAsset({}) request send ...", JSON.toJSONString(assetChangeDto));
            result = pointApi.useAccountAssets(assetChangeDto);
            log.info("response ...", JSON.toJSONString(result));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return result;
    }

    public boolean rollbackAccountAssets(AssetChangeDto assetChangeDto) {
        boolean result = false;
        try {
            log.info("pointApi.getMemberAsset({}) request send ...", JSON.toJSONString(assetChangeDto));
            result = pointApi.rollbackAccountAssets(assetChangeDto);
            log.info("response ...", JSON.toJSONString(result));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return result;
    }

    public MemberAssetDto getMemberAsset(Long memberId) {
        MemberAssetDto dto = null;
        try {
            log.info("pointApi.getMemberAsset({}) request send ...", memberId);
            dto = pointApi.getMemberAsset(memberId);
            log.info("response ...", JSON.toJSONString(dto));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return dto;
    }
}