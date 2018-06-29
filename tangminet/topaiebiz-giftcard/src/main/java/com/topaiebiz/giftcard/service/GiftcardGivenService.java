package com.topaiebiz.giftcard.service;

import com.baomidou.mybatisplus.service.IService;
import com.topaiebiz.giftcard.entity.GiftcardGiven;
import com.topaiebiz.giftcard.vo.GiftcardGivenVO;
import com.topaiebiz.giftcard.vo.GivenDetailVO;

/**
 * @description: 礼卡转赠服务
 * @author: Jeff Chen
 * @date: created in 下午3:05 2018/1/12
 */
public interface GiftcardGivenService extends IService<GiftcardGiven>{

    /**
     * 获取指定用户转赠卡的信息
     * @param cardNo
     * @param memberId
     * @return
     */
    GiftcardGivenVO getGiftcard4Given(String cardNo,Long memberId);

    /**
     * 生成转赠记录
     * @param giftcardGiven
     * @return
     */
    GiftcardGiven generate(GiftcardGiven giftcardGiven);

    /**
     * 取消转赠
     * @param giftcardGiven
     * @return
     */
    Boolean cancle(GiftcardGiven giftcardGiven);

    /**
     * 根据转赠链接id查询
     * @param linkId
     * @return
     */
    GivenDetailVO getByLinkId(String linkId);

    /**
     * 领取转赠
     * @param giftcardGiven
     * @return
     */
    Boolean getTheGiven(GiftcardGiven giftcardGiven);
}
