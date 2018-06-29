package com.topaiebiz.giftcard.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.topaiebiz.giftcard.dao.GiftcardLabelDao;
import com.topaiebiz.giftcard.dao.GiftcardLogDao;
import com.topaiebiz.giftcard.entity.GiftcardLog;
import com.topaiebiz.giftcard.service.GiftcardLogService;
import com.topaiebiz.giftcard.vo.GiftcardLogReq;
import com.topaiebiz.giftcard.vo.GiftcardLogVO;
import com.topaiebiz.giftcard.vo.MyGiftcardLogReq;
import com.topaiebiz.giftcard.vo.MyGiftcardLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 下午3:51 2018/1/12
 */
@Service
public class GiftcardLogServiceImpl extends ServiceImpl<GiftcardLogDao,GiftcardLog> implements GiftcardLogService{

    @Autowired
    private GiftcardLogDao giftcardLogDao;

    @Override
    public PageInfo<GiftcardLogVO> queryLog(GiftcardLogReq giftcardLogReq) {
        Page page = PageDataUtil.buildPageParam(giftcardLogReq);
        List<GiftcardLog> giftcardLogList = giftcardLogDao.queryLog(page, giftcardLogReq);
        if (!CollectionUtils.isEmpty(giftcardLogList)) {
            List<GiftcardLogVO> giftcardLogVOList = new ArrayList<>(giftcardLogList.size());
            giftcardLogList.forEach(giftcardLog -> {
                GiftcardLogVO logVO = new GiftcardLogVO();
                BeanCopyUtil.copy(giftcardLog, logVO);
                giftcardLogVOList.add(logVO);

            });
            page.setRecords(giftcardLogVOList);
        }
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public PageInfo<MyGiftcardLogVO> queryMyGiftcardLog(MyGiftcardLogReq myGiftcardLogReq) {
        Page page = PageDataUtil.buildPageParam(myGiftcardLogReq);
        List<GiftcardLog> giftcardLogList = giftcardLogDao.queryMyGiftcardLog(page, myGiftcardLogReq);
        if (!CollectionUtils.isEmpty(giftcardLogList)) {
            List<MyGiftcardLogVO> myGiftcardLogVOList = new ArrayList<>(giftcardLogList.size());
            giftcardLogList.forEach(giftcardLog -> {
                MyGiftcardLogVO myGiftcardLogVO = new MyGiftcardLogVO();
                myGiftcardLogVO.setAmount(giftcardLog.getAmount());
                myGiftcardLogVO.setCardNo(giftcardLog.getCardNo());
                myGiftcardLogVO.setCreatedTime(giftcardLog.getCreatedTime());
                myGiftcardLogVOList.add(myGiftcardLogVO);
            });
            page.setRecords(myGiftcardLogVOList);
        }
        return PageDataUtil.copyPageInfo(page);
    }
}
