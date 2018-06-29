package com.topaiebiz.settlement.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.settlement.dto.SettlementOrderStatisDTO;
import com.topaiebiz.settlement.entity.StoreSettlementOrderEntity;
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
public interface StoreSettlementOrderDao extends BaseDao<StoreSettlementOrderEntity> {

    SettlementOrderStatisDTO selectSettlementStatis(@Param("settlementId") Long settlementId, @Param("storeId") Long storeId);
}