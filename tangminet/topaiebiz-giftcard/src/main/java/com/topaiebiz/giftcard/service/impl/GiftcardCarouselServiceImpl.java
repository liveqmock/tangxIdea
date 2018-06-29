package com.topaiebiz.giftcard.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.common.BeanCopyUtil;
import com.topaiebiz.giftcard.dao.GiftcardCarouselDao;
import com.topaiebiz.giftcard.entity.GiftcardCarousel;
import com.topaiebiz.giftcard.service.GiftcardCarouselService;
import com.topaiebiz.giftcard.vo.GiftcardCarouselVO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 下午3:44 2018/1/12
 */
@Service
public class GiftcardCarouselServiceImpl extends ServiceImpl<GiftcardCarouselDao,GiftcardCarousel> implements GiftcardCarouselService {

    @Override
    public List<GiftcardCarouselVO> queryAll() {
        //TODO get from redis
        List<GiftcardCarousel> carouselList = selectList(null);
        if (!CollectionUtils.isEmpty(carouselList)) {
            List<GiftcardCarouselVO> carouselVOList = new ArrayList<>(carouselList.size());
            carouselList.forEach(giftcardCarousel -> {
                GiftcardCarouselVO carouselVO = new GiftcardCarouselVO();
                BeanCopyUtil.copy(giftcardCarousel, carouselVO);
                carouselVOList.add(carouselVO);
            });
            return carouselVOList;
        }
        return null;
    }
}
