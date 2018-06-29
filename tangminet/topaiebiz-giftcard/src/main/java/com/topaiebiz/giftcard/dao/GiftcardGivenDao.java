package com.topaiebiz.giftcard.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.topaiebiz.giftcard.entity.GiftcardGiven;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description: 礼卡转赠dao
 * @author: Jeff Chen
 * @date: created in 上午10:53 2018/1/12
 */
@Mapper
public interface GiftcardGivenDao extends BaseMapper<GiftcardGiven>{
}
