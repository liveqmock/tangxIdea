package com.topaiebiz.merchant.store.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.merchant.enter.dto.MerchantQualificationDto;
import com.topaiebiz.merchant.store.entity.MerchantFollowEntity;
import com.topaiebiz.merchant.store.entity.MerchantMemberEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/20 0020 上午 10:32
 */
@Mapper
public interface MerchantMmeberDao extends BaseDao<MerchantMemberEntity> {

    /**
     * 根据店铺查询所有的会员
     *
     * @param storeId
     * @return
     */
    List<Long> selectMerchantMemberByStoreId(Page<MemberDto> page, Long storeId);

    /**
     * 根据会员id与店铺id查询关注表
     *
     * @param merchantMemberEntity
     * @return
     */
    MerchantMemberEntity selectMerchantMemberByStoreIdAndMember(MerchantMemberEntity merchantMemberEntity);
}
