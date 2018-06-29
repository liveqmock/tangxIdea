package com.topaiebiz.promotion.mgmt.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.promotion.mgmt.entity.PromotionStoreUsageLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 店铺活动适用记录
 */
@Mapper
public interface PromotionStoreUsageLogDao extends BaseDao<PromotionStoreUsageLogEntity> {

}
