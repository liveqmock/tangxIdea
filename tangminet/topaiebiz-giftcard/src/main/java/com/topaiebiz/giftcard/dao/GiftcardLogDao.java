package com.topaiebiz.giftcard.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.topaiebiz.giftcard.entity.GiftcardCarousel;
import com.topaiebiz.giftcard.entity.GiftcardLog;
import com.topaiebiz.giftcard.vo.GiftcardLogReq;
import com.topaiebiz.giftcard.vo.MyGiftcardLogReq;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 礼卡操作日志dao
 * @author: Jeff Chen
 * @date: created in 上午10:53 2018/1/12
 */
@Mapper
public interface GiftcardLogDao extends BaseMapper<GiftcardLog>{

    /**
     * 分页查询消费日志
     * @param page
     * @param giftcardLogReq
     * @return
     */
    List<GiftcardLog> queryLog(Page page, GiftcardLogReq giftcardLogReq);

    /**
     * 分页查询我的消费日志
     * @param page
     * @param myGiftcardLogReq
     * @return
     */
    List<GiftcardLog> queryMyGiftcardLog(Page page, MyGiftcardLogReq myGiftcardLogReq);
}
