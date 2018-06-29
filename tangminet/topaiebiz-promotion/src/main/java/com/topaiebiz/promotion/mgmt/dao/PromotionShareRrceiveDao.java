package com.topaiebiz.promotion.mgmt.dao;


import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.promotion.mgmt.entity.PromotionShareRrceiveEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PromotionShareRrceiveDao extends BaseDao<PromotionShareRrceiveEntity> {


	Integer countRrceiveCouponShareNum(@Param("promotionId") Long promotionId, @Param("memberId") Long memberId);

	Integer countDayRrceiveCouponShareNum(@Param("promotionId") Long promotionId, @Param("memberId") Long memberId);

	Integer countPeopleRrceiveNum(@Param("shareId") Long shareId);

}