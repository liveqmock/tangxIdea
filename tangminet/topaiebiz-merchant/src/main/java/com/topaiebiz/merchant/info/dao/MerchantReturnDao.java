package com.topaiebiz.merchant.info.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.topaiebiz.merchant.info.entity.MerchantReturnEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商家退货地址dao
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/24 0024 下午 4:44
 */
@Mapper
public interface MerchantReturnDao extends BaseMapper<MerchantReturnEntity> {

    /**
     * 根据商家id查询信息
     * @param merchantId
     * @return
     */
    MerchantReturnEntity selectMerchantReturnInfoByMerchantId(Long merchantId);

}
