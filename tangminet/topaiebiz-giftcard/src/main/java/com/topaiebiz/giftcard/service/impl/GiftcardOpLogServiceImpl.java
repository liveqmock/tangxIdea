package com.topaiebiz.giftcard.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.common.BeanCopyUtil;
import com.topaiebiz.giftcard.dao.GiftcardOpLogDao;
import com.topaiebiz.giftcard.entity.GiftcardOpLog;
import com.topaiebiz.giftcard.service.GiftcardOpLogService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.topaiebiz.giftcard.vo.CardOpLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 礼卡相关后台操作日志表 服务实现类
 * </p>
 *
 * @author Jeff Chen
 * @since 2018-03-16
 */
@Service
public class GiftcardOpLogServiceImpl extends ServiceImpl<GiftcardOpLogDao, GiftcardOpLog> implements GiftcardOpLogService {

    @Autowired
    private GiftcardOpLogDao giftcardOpLogDao;
    @Override
    public List<CardOpLogVO> selectByBizId(GiftcardOpLog giftcardOpLog) {
        EntityWrapper<GiftcardOpLog> wrapper = new EntityWrapper<>();
        wrapper.eq("bizId", giftcardOpLog.getBizId());
        wrapper.eq("opSrc", giftcardOpLog.getOpSrc());
        List<GiftcardOpLog> giftcardOpLogs = giftcardOpLogDao.selectList(wrapper);
        if (!CollectionUtils.isEmpty(giftcardOpLogs)) {
            List<CardOpLogVO> cardOpLogVOList = new ArrayList<>(giftcardOpLogs.size());
            giftcardOpLogs.forEach(giftcardOpLog1 -> {
                CardOpLogVO cardOpLogVO = new CardOpLogVO();
                BeanCopyUtil.copy(giftcardOpLog1, cardOpLogVO);
                cardOpLogVOList.add(cardOpLogVO);
            });
            return cardOpLogVOList;
        }
        return null;
    }
}
