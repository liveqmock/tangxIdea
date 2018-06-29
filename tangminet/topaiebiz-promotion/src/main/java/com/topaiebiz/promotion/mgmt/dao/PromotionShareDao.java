package com.topaiebiz.promotion.mgmt.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.promotion.mgmt.dto.coupon.ShareCouponDto;
import com.topaiebiz.promotion.mgmt.entity.PromotionShareEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PromotionShareDao extends BaseDao<PromotionShareEntity> {


}