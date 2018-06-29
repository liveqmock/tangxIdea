package com.topaiebiz.promotion.mgmt.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.entity.PromotionCouponConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PromotionCouponConfigDao extends BaseDao<PromotionCouponConfigEntity> {

    /**
     * @param page,promotionDto
     * @Author: tangx.w
     * @Description: 分页查询选中的优惠券
     * @Date: 2018/5/12 9:31
     */
    List<PromotionDto> getSelectedCoupons(Page<PromotionDto> page, PromotionDto promotionDto);


    /**
     * 更新配置中剩余的优惠券数量
     *
     * @param id
     * @param number
     * @return
     */
    Integer updateRemainderNumById(@Param("id") Long id, @Param("number") Integer number);
}