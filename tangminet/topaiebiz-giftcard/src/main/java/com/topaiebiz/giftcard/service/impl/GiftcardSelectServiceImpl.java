package com.topaiebiz.giftcard.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.card.constant.ApplyScopeEnum;
import com.topaiebiz.giftcard.dao.GiftcardSelectDao;
import com.topaiebiz.giftcard.entity.GiftcardBatch;
import com.topaiebiz.giftcard.entity.GiftcardSelect;
import com.topaiebiz.giftcard.enums.CardAttrEnum;
import com.topaiebiz.giftcard.enums.GiftcardExceptionEnum;
import com.topaiebiz.giftcard.service.GiftcardBatchService;
import com.topaiebiz.giftcard.service.GiftcardSelectService;
import com.topaiebiz.giftcard.util.DateUtil;
import com.topaiebiz.giftcard.vo.GiftcardSelectReq;
import com.topaiebiz.giftcard.vo.GiftcardSelectVO;
import com.topaiebiz.giftcard.vo.GiftcardShowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 下午3:54 2018/1/12
 */
@Service
public class GiftcardSelectServiceImpl extends ServiceImpl<GiftcardSelectDao,GiftcardSelect> implements GiftcardSelectService {

    @Autowired
    private GiftcardSelectDao giftcardSelectDao;
    @Autowired
    private GiftcardBatchService giftcardBatchService;

    @Override
    public PageInfo<GiftcardSelectVO> querySelect(GiftcardSelectReq giftcardSelectReq) {
        Page page = PageDataUtil.buildPageParam(giftcardSelectReq);
        List<GiftcardSelectVO> selectVOList = giftcardSelectDao.querySelect(page, giftcardSelectReq);
        if (!CollectionUtils.isEmpty(selectVOList)) {
            page.setRecords(selectVOList);
        }
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean moveUp(GiftcardSelect giftcardSelect) {
        GiftcardSelect selectA = giftcardSelectDao.selectById(giftcardSelect.getId());
        if (null == selectA) {
            throw new GlobalException(GiftcardExceptionEnum.SELECT_NOT_EXIST);
        }
        GiftcardSelect selectB = giftcardSelectDao.uponBySeq(selectA.getSeq());
        if (null == selectB) {
            throw new GlobalException(GiftcardExceptionEnum.NOT_TO_MOVE);
        }
        //交换seq
        int tmp = selectA.getSeq();
        selectA.setModifiedTime(new Date());
        selectA.setModifier(giftcardSelect.getModifier());
        selectA.setSeq(selectB.getSeq());
        giftcardSelectDao.updateById(selectA);
        selectB.setModifiedTime(new Date());
        selectB.setModifier(giftcardSelect.getModifier());
        selectB.setSeq(tmp);
        giftcardSelectDao.updateById(selectB);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean moveDown(GiftcardSelect giftcardSelect) {
        GiftcardSelect selectA = giftcardSelectDao.selectById(giftcardSelect.getId());
        if (null == selectA) {
            throw new GlobalException(GiftcardExceptionEnum.SELECT_NOT_EXIST);
        }
        GiftcardSelect selectB = giftcardSelectDao.nextBySeq(selectA.getSeq());
        if (null == selectB) {
            throw new GlobalException(GiftcardExceptionEnum.NOT_TO_MOVE);
        }
        //交换seq
        int tmp = selectA.getSeq();
        selectA.setModifiedTime(new Date());
        selectA.setModifier(giftcardSelect.getModifier());
        selectA.setSeq(selectB.getSeq());
        giftcardSelectDao.updateById(selectA);
        selectB.setModifiedTime(new Date());
        selectB.setModifier(giftcardSelect.getModifier());
        selectB.setSeq(tmp);
        giftcardSelectDao.updateById(selectB);
        return true;
    }

    @Override
    public Boolean add(GiftcardSelect giftcardSelect) {
        //联名卡和活动卡不能添加精选
        GiftcardBatch giftcardBatch = giftcardBatchService.selectById(giftcardSelect.getBatchId());
        if (null == giftcardBatch || !giftcardBatch.getCardAttr().equals(CardAttrEnum.COMMON.getId())) {
            throw new GlobalException(GiftcardExceptionEnum.ONLY_SELECT_COMMON);
        }
        //查找最大seq
        EntityWrapper<GiftcardSelect> entityWrapper = new EntityWrapper<>();
        entityWrapper.orderBy("seq", false);
        entityWrapper.last("limit 1");
        List<GiftcardSelect> giftcardSelects = giftcardSelectDao.selectList(entityWrapper);
        if (!CollectionUtils.isEmpty(giftcardSelects)) {
            giftcardSelect.setSeq(giftcardSelects.get(0).getSeq() + 1);
        } else {
            giftcardSelect.setSeq(1);
        }
        return insert(giftcardSelect);
    }

    @Override
    public PageInfo<GiftcardShowVO> querySelectShow(GiftcardSelectReq giftcardSelectReq) {
        Page page = PageDataUtil.buildPageParam(giftcardSelectReq);
        List<GiftcardSelectVO> selectVOList = giftcardSelectDao.querySelect(page, giftcardSelectReq);
        if (!CollectionUtils.isEmpty(selectVOList)) {
            List<GiftcardShowVO> showVOList = new ArrayList<>(selectVOList.size());
            selectVOList.forEach(giftcardSelectVO -> {
                GiftcardShowVO showVO = new GiftcardShowVO();
                showVO.setSalePrice(giftcardSelectVO.getSalePrice());
                showVO.setFaceValue(giftcardSelectVO.getFaceValue());
                showVO.setCardName(giftcardSelectVO.getCardName());
                showVO.setScope(ApplyScopeEnum.getById(giftcardSelectVO.getApplyScope()).getScopeDesc());
                showVO.setCover(giftcardSelectVO.getCover());
                showVO.setBatchId(giftcardSelectVO.getBatchId());
                showVO.setDeadTime(DateUtil.renewalDays(new Date(),giftcardSelectVO.getValidDays()));
                showVOList.add(showVO);
            });
            page.setRecords(showVOList);
        }
        return PageDataUtil.copyPageInfo(page);
    }
}
