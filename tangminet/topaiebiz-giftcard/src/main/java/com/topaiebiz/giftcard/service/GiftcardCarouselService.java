package com.topaiebiz.giftcard.service;

import com.baomidou.mybatisplus.service.IService;
import com.topaiebiz.giftcard.entity.GiftcardCarousel;
import com.topaiebiz.giftcard.vo.GiftcardCarouselVO;

import java.util.List;

/**
 * @description: 礼卡轮播服务
 * @author: Jeff Chen
 * @date: created in 下午3:03 2018/1/12
 */
public interface GiftcardCarouselService extends IService<GiftcardCarousel> {

    /**
     * 查询所有轮播数据
     * @return
     */
    List<GiftcardCarouselVO> queryAll();
}
