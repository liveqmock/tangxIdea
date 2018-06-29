package com.topaiebiz.giftcard.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.topaiebiz.giftcard.entity.GiftcardGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 礼卡适用商品dao
 * @author: Jeff Chen
 * @date: created in 上午10:09 2018/4/19
 */
@Mapper
public interface GiftcardGoodsDao extends BaseMapper<GiftcardGoods>{

    /**
     *查询指定批次适用的商品id列表
     * @param batchId
     * @return
     */
    List<Long> getGoodsByBatchId(@Param("batchId") Long batchId);
}
