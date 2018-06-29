package com.topaiebiz.promotion.mgmt.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.promotion.mgmt.entity.card.CardActivityItemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CardActivityItemDao extends BaseDao<CardActivityItemEntity> {
    /**
     * 下单锁库存
     *
     * @param id
     * @param number
     * @return
     */
    Integer reduceStock(@Param("id") Long id, @Param("number") Integer number);

    /**
     * 释放库存
     *
     * @param id
     * @param number
     * @return
     */
    Integer backStock(@Param("id") Long id, @Param("number") Integer number);

    /**
     * 批量更新当日剩余库存
     *
     * @param activityIds
     * @return
     */
    Integer batchUpdateRestStorage(@Param("list") List<Long> activityIds);
}
