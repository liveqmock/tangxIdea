package com.topaiebiz.merchant.api;

import com.topaiebiz.merchant.dto.store.MerchantAccountDTO;
import com.topaiebiz.merchant.dto.store.MerchantInfoDTO;
import com.topaiebiz.merchant.dto.store.MerchantMemberDTO;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhaoxupeng
 * @date 2018/1/4 - 15:38
 */
public interface StoreApi {

    /**
     * 根据storeId查询店铺信息
     *
     * @param storeId
     * @return
     */
    StoreInfoDetailDTO getStore(Long storeId);

    /**
     * Description: 根据商家ID 查询店铺ID
     * <p>
     * <p>Author: hxpeng createTime: 2018/3/9
     *
     * @param:
     */
    StoreInfoDetailDTO getStoreByMerchantId(Long merchantId);

    /**
     * 根据MerchantId查询商家信息
     *
     * @param merchantId
     * @return
     */
    MerchantInfoDTO getMerchant(Long merchantId);

    /**
     * 批量查询店铺信息
     *
     * @param storeIds
     * @return
     */
    Map<Long, StoreInfoDetailDTO> getStoreMap(List<Long> storeIds);

    /**
     * 店铺记录会员
     */
    Integer saveMerchantMemberRelation(MerchantMemberDTO merchantMemberDTO);

    /**
     * 判断该店铺是否存在该会员
     *
     * @param memberId
     * @param storeId
     * @return
     */
    Boolean checkStoreMemberRelation(Long memberId, Long storeId);

    /**
     * 更新店铺的下次结算时间
     *
     * @param storeId
     * @return
     */
    Boolean updateNextSettleDate(Long storeId, Date nextSettleDate);

    /**
     * 根据id游标进行分页查询
     *
     * @param startId   起始ID，不包含即查询条件为: id > startId
     * @param querySize 查询记录条数
     * @return
     */
    List<StoreInfoDetailDTO> queryStores(Long startId, Integer querySize);

    /**
     * 根据店铺名模糊查询
     *
     * @param name
     * @return
     */
    List<StoreInfoDetailDTO> getStoreInfoListByStoreName(String name);

    /**
     * 根据店铺名模糊查询
     *
     * @param name
     * @return
     */
    List<StoreInfoDetailDTO> queryStores(String name);

    /**
     * 根据MerchantId查询商家银行信息
     *
     * @param merchantId
     * @return
     */
    MerchantAccountDTO getMerchantAccountInfo(Long merchantId);

    /**
     * 根据merchantId查询商家信息
     * @param merchantIdList
     * @return
     */
    List<MerchantInfoDTO> getMerchantInfo(List<Long> merchantIdList);

    /**
     * 根据merchantId查询商家信息
     * @param storeIds
     * @return
     */
    List<StoreInfoDetailDTO> getStoreList(List<Long> storeIds);


    /**
     * 查询冻结店铺id
     *
     * @return
     */
    List<Long> getFrozenStoreIds();
}