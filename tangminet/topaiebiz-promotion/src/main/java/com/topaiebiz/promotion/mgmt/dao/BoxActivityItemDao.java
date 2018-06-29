package com.topaiebiz.promotion.mgmt.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.promotion.mgmt.entity.box.BoxActivityItemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BoxActivityItemDao extends BaseDao<BoxActivityItemEntity> {

    /**
     * 减库存
     *
     * @param id     开宝箱活动配置ID
     * @param number 差量
     * @return
     */
    Integer reduceStock(@Param("id") Long id, @Param("number") Integer number);

    /**
     * 批量更新当日剩余库存
     *
     * @param activityId 开宝箱活动ID
     * @param awardTypes 奖品类型
     * @return
     */
    Integer batchUpdateRestStorage(@Param("activityId") Long activityId, @Param("awardTypes") String awardTypes);
}
