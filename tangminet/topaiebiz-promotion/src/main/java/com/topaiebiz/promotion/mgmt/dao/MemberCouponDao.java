package com.topaiebiz.promotion.mgmt.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.promotion.mgmt.dto.MemberCouponDto;
import com.topaiebiz.promotion.mgmt.entity.MemberCouponEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员优惠券
 * Created by Joe on 2018/1/10.
 */
@Mapper
public interface MemberCouponDao extends BaseDao<MemberCouponEntity> {

    /**
     * 查询会员可用优惠券数量
     *
     * @param memberId
     * @param couponPromIds
     * @return
     */
    List<Long> countAvailByMemberIdAndCouponId(@Param("memberId") Long memberId, @Param("list") List<Long> couponPromIds);

    /**
     * 获取会员可用优惠券数量/获取会员在某店铺下可用优惠券数量
     *
     * @param memberCouponDto
     * @return
     */
    Integer selectCouponNum(MemberCouponDto memberCouponDto);

}
