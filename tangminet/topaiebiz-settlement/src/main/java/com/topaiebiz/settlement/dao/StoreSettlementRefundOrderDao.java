package com.topaiebiz.settlement.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.settlement.dto.SettlementRefundOrderStatisDTO;
import com.topaiebiz.settlement.entity.StoreSettlementRefundOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Description： 商家结算Dao。
 * <p>
 * Author Harry
 * <p>
 * Date 2017年10月31日 下午2:13:41
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Mapper
public interface StoreSettlementRefundOrderDao extends BaseDao<StoreSettlementRefundOrderEntity> {
    SettlementRefundOrderStatisDTO selectSettlementStatis(@Param("settlementId") Long settlementId, @Param("storeId") Long storeId);
}