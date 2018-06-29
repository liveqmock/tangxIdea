package com.topaiebiz.promotion.mgmt.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.promotion.mgmt.dto.coupon.CouponStoreDto;
import com.topaiebiz.promotion.mgmt.entity.PromotionStoresEntity;

import java.util.List;


public interface PromotionStoresDao extends BaseDao<PromotionStoresEntity>{

    List<CouponStoreDto> slectPromotionStoresList(Page<CouponStoreDto> page,CouponStoreDto couponStoreDto);
}