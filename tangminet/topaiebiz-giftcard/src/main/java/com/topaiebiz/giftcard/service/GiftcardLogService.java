package com.topaiebiz.giftcard.service;

import com.baomidou.mybatisplus.service.IService;
import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.giftcard.entity.GiftcardLog;
import com.topaiebiz.giftcard.vo.GiftcardLogReq;
import com.topaiebiz.giftcard.vo.GiftcardLogVO;
import com.topaiebiz.giftcard.vo.MyGiftcardLogReq;
import com.topaiebiz.giftcard.vo.MyGiftcardLogVO;

/**
 * @description: 礼卡操作日志服务
 * @author: Jeff Chen
 * @date: created in 下午3:07 2018/1/12
 */
public interface GiftcardLogService extends IService<GiftcardLog>{

    /**
     * 分页查询消费日志
     * @param giftcardLogReq
     * @return
     */
    PageInfo<GiftcardLogVO> queryLog(GiftcardLogReq giftcardLogReq);

    /**
     * 分页查询我的消费日志
     * @param myGiftcardLogReq
     * @return
     */
    PageInfo<MyGiftcardLogVO> queryMyGiftcardLog(MyGiftcardLogReq myGiftcardLogReq);
}
