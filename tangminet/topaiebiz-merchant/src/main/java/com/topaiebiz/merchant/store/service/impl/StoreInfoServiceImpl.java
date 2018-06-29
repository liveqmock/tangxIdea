package com.topaiebiz.merchant.store.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.dto.sku.StoreGoodsDTO;
import com.topaiebiz.merchant.enter.dao.StoreInfoDao;
import com.topaiebiz.merchant.enter.dto.StoreInfoDto;
import com.topaiebiz.merchant.info.exception.MerchantInfoException;
import com.topaiebiz.merchant.store.exception.StoreInfoException;
import com.topaiebiz.merchant.store.service.StoreInfoService;
import com.topaiebiz.promotion.api.PromotionApi;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/22 0022 下午 5:32
 */
@Service
public class StoreInfoServiceImpl implements StoreInfoService {


    @Autowired
    private StoreInfoDao storeInfoDao;

    @Autowired
    private GoodsApi goodsApi;

    @Autowired
    private PromotionApi promotionApi;


    @Override
    public PageInfo<StoreInfoDto> getStoreInfoList(PagePO pagePO, StoreInfoDto storeInfoDto) {
        Page<StoreInfoDto> page = PageDataUtil.buildPageParam(pagePO);
        page.setRecords(storeInfoDao.selectStoreInfoList(page, storeInfoDto));
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public PageInfo<StoreInfoDto> getStoreInfosList(PagePO pagePO, StoreInfoDto storeInfoDto) {
        Page<StoreInfoDto> page = PageDataUtil.buildPageParam(pagePO);
        //有营销的店铺
        if (storeInfoDto.getPromotionId() != null) {
            List<Long> storeIdListByPromotionId = promotionApi.getStoreIdListByPromotionId(storeInfoDto.getPromotionId());
            if (CollectionUtils.isNotEmpty(storeIdListByPromotionId)) {
                storeInfoDto.setStoreIdListByPromotionId(storeIdListByPromotionId);
            }
        }
        List<StoreInfoDto> storeInfoDtos = storeInfoDao.selectStoreInfosList(page, storeInfoDto);
        page.setRecords(storeInfoDtos);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public Map<Long, StoreInfoDto> getStoreMap(Long[] storeId) {
        if (storeId == null) {
            throw new GlobalException(StoreInfoException.STOREINFO_ID_NOT_NULL);
        }
        Map<Long, StoreInfoDto> storeInfoDtoMap = new HashMap<Long, StoreInfoDto>();
        for (Long storeIds : storeId) {
            StoreInfoDto storeInfoDto = storeInfoDao.selectStoreInfoLists(storeIds);
            if (storeInfoDto == null) {
                throw new GlobalException(MerchantInfoException.MERCHANTINFO_ID_EXIST);
            }
            StoreInfoDto storeInfoDtos = new StoreInfoDto();
            BeanCopyUtil.copy(storeInfoDto, storeInfoDtos);
            storeInfoDtoMap.put(storeIds, storeInfoDtos);
        }
        return storeInfoDtoMap;
    }

    @Override
    public List<StoreInfoDto> getstoreinfos(StoreInfoDto storeInfoDto) {
        //根据店铺id查询查询店铺所售商品
        List<StoreInfoDto> storeInfoDtoss = new ArrayList<StoreInfoDto>();
        List<StoreInfoDto> storeInfoDtos = storeInfoDao.selectStoreInfoByName(storeInfoDto);
        for (StoreInfoDto storeInfoDto1 : storeInfoDtos) {
            List<StoreGoodsDTO> storeGoods = goodsApi.getStoreGoods(storeInfoDto1.getId());
            storeInfoDto1.setStoreGoods(storeGoods);
            storeInfoDtoss.add(storeInfoDto1);
        }
        return storeInfoDtoss;
    }
}
