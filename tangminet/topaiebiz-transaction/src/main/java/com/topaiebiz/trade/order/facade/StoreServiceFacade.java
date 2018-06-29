package com.topaiebiz.trade.order.facade;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.dto.store.MerchantMemberDTO;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-10 15:39
 */
@Component
@Slf4j
public class StoreServiceFacade {

    @Autowired
    private StoreApi storeApi;

    public Map<Long, StoreInfoDetailDTO> getStoreMap(List<Long> storeIds) {
        Map<Long, StoreInfoDetailDTO> resultMap = storeApi.getStoreMap(storeIds);
        log.info("storeApi.getStoreMap({}) return:{}", JSON.toJSONString(storeIds), JSON.toJSONString(resultMap));
        return resultMap;
    }

    public Integer saveMerchantMemberRelation(MerchantMemberDTO merchantMemberDTO) {
        log.info("pointApi.saveMerchantMemberRelation({}) request send ...", JSON.toJSONString(merchantMemberDTO));
        Integer result = storeApi.saveMerchantMemberRelation(merchantMemberDTO);
        log.info("pointApi.saveMerchantMemberRelation() return: {}", result);
        return result;
    }
}