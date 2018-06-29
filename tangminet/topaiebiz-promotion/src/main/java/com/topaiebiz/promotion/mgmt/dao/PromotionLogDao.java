package com.topaiebiz.promotion.mgmt.dao;


import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.promotion.mgmt.entity.PromotionLogEntity;

public interface PromotionLogDao extends BaseDao<PromotionLogEntity> {
    int delete(Long id);

    int save(PromotionLogEntity record);

    int saveSte(PromotionLogEntity record);

    PromotionLogEntity get(Long id);

    int updateSte(PromotionLogEntity record);

    int update(PromotionLogEntity record);
}