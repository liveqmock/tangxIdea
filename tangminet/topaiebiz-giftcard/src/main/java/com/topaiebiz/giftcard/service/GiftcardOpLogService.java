package com.topaiebiz.giftcard.service;

import com.topaiebiz.giftcard.entity.GiftcardOpLog;
import com.baomidou.mybatisplus.service.IService;
import com.topaiebiz.giftcard.vo.CardOpLogVO;

import java.util.List;

/**
 * <p>
 * 礼卡相关后台操作日志表 服务类
 * </p>
 *
 * @author Jeff Chen
 * @since 2018-03-16
 */
public interface GiftcardOpLogService extends IService<GiftcardOpLog> {
    /**
     * 按条件查询操作日子列表
     * @param giftcardOpLog
     * @return
     */
    List<CardOpLogVO> selectByBizId(GiftcardOpLog giftcardOpLog);
}
