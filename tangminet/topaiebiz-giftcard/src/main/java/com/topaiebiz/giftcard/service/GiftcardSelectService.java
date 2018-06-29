package com.topaiebiz.giftcard.service;

import com.baomidou.mybatisplus.service.IService;
import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.giftcard.entity.GiftcardSelect;
import com.topaiebiz.giftcard.vo.GiftcardSelectReq;
import com.topaiebiz.giftcard.vo.GiftcardSelectVO;
import com.topaiebiz.giftcard.vo.GiftcardShowReq;
import com.topaiebiz.giftcard.vo.GiftcardShowVO;

/**
 * @description: 礼卡精选服务
 * @author: Jeff Chen
 * @date: created in 下午3:10 2018/1/12
 */
public interface GiftcardSelectService extends IService<GiftcardSelect> {

    /**
     * 分页查询
     *
     * @param giftcardSelectReq
     * @return
     */
    PageInfo<GiftcardSelectVO> querySelect(GiftcardSelectReq giftcardSelectReq);

    /**
     * 添加精选
     *
     * @param giftcardSelect
     * @return
     */
    Boolean add(GiftcardSelect giftcardSelect);

    /**
     * 上移
     *
     * @return
     */
    Boolean moveUp(GiftcardSelect giftcardSelect);

    /**
     * 下移
     *
     * @return
     */
    Boolean moveDown(GiftcardSelect giftcardSelect);

    /**
     * C端展示精选列表
     * @param giftcardSelectReq
     * @return
     */
    PageInfo<GiftcardShowVO> querySelectShow(GiftcardSelectReq giftcardSelectReq);
}
