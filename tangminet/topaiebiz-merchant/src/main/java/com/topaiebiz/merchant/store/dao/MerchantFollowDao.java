package com.topaiebiz.merchant.store.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.merchant.store.dto.MerchantFollowDto;
import com.topaiebiz.merchant.store.entity.MerchantFollowEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/19 0019 上午 10:27
 */
@Mapper
public interface MerchantFollowDao extends BaseDao<MerchantFollowEntity> {


    List<MerchantFollowDto> selectMerchantFollowList(Page<MerchantFollowDto> page, MerchantFollowDto merchantFollowDto);

    /**
     * 根据会员id查询所关注的店铺
     * @param merchantQualificationDto
     * @return
     */
  //  List<MerchantFollowDto> selectMerchantFollowLists(Long memberId);
    List<MerchantFollowDto> selectMerchantFollowLists(Page<MerchantFollowDto> page,
                                                            MerchantFollowDto merchantQualificationDto);


    /**
     * 根据会员查询关注信息
     * @param merchantFollowEntity
     * @return
     */
    MerchantFollowEntity selectFollowByMemberId(MerchantFollowEntity  merchantFollowEntity);

    /**
     * 取消关注（修改deteleFlag状态为1）
     * @param merchantFollowDto
     * @return
     */
    Integer updateMerchantFollowById(MerchantFollowDto merchantFollowDto);

    /**
     * 根据店铺id查询店铺
     * @param storeId
     * @return
     */
    MerchantFollowDto selectMerchantFollowDetails(Long storeId);
}
