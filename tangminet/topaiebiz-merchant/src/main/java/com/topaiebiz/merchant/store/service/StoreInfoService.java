package com.topaiebiz.merchant.store.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.merchant.enter.dto.StoreInfoDto;
import com.topaiebiz.merchant.info.dto.StoreInfoDetailDto;

import java.util.List;
import java.util.Map;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/22 0022 下午 5:28
 */
public interface StoreInfoService {

    /**
     * 店铺信息列表
     * @param pagePO
     * @param storeInfoDto
     * @return
     */
    PageInfo<StoreInfoDto> getStoreInfoList(PagePO pagePO, StoreInfoDto storeInfoDto);

    /**
     * 根据多个id查询店铺信息
     * @param storeId
     * @return
     */
    Map<Long,StoreInfoDto> getStoreMap(Long[] storeId);

    /**
     *模糊查询店铺
     * @param storeInfoDto
     * @return
     */
    List<StoreInfoDto> getstoreinfos(StoreInfoDto storeInfoDto);

    /**
     * 无优惠活动的店铺列表
     * @param pagePO
     * @param storeInfoDto
     * @return
     */
    PageInfo<StoreInfoDto> getStoreInfosList(PagePO pagePO, StoreInfoDto storeInfoDto);
}
