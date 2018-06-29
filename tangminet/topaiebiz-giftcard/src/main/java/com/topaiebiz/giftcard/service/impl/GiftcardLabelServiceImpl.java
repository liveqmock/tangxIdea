package com.topaiebiz.giftcard.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.topaiebiz.giftcard.dao.GiftcardLabelDao;
import com.topaiebiz.giftcard.entity.GiftcardBatch;
import com.topaiebiz.giftcard.entity.GiftcardLabel;
import com.topaiebiz.giftcard.enums.CardAttrEnum;
import com.topaiebiz.giftcard.enums.CardMediumEnum;
import com.topaiebiz.giftcard.enums.IssueStatusEnum;
import com.topaiebiz.giftcard.service.GiftcardBatchService;
import com.topaiebiz.giftcard.service.GiftcardLabelService;
import com.topaiebiz.giftcard.vo.GiftcardLabelVO;
import com.topaiebiz.giftcard.vo.GiftcardLogVO;
import com.topaiebiz.giftcard.vo.LabelShowVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 下午3:15 2018/1/12
 */
@Service
@Slf4j
public class GiftcardLabelServiceImpl extends ServiceImpl<GiftcardLabelDao, GiftcardLabel> implements GiftcardLabelService {

    @Autowired
    private GiftcardLabelDao giftcardLabelDao;
    @Autowired
    private GiftcardBatchService giftcardBatchService;

    @Override
    public Boolean batchDeleteByIds(List<Long> ids, String modifier) {
        GiftcardLabel giftcardLabel = new GiftcardLabel();
        giftcardLabel.setDelFlag(1);
        giftcardLabel.setModifiedTime(new Date());
        giftcardLabel.setModifier(modifier);
        EntityWrapper<GiftcardLabel> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", ids);
        entityWrapper.eq("delFlag", 0);
        return giftcardLabelDao.update(giftcardLabel, entityWrapper) > 0;
    }

    @Override
    public PageInfo<GiftcardLabelVO> queryGiftcardLabel(PagePO pagePo, String labelName) {
        Page page = PageDataUtil.buildPageParam(pagePo);
        List<GiftcardLabel> giftcardLabelList = giftcardLabelDao.queryGiftcardLabel(page, labelName);
        if (!CollectionUtils.isEmpty(giftcardLabelList)) {
            List<GiftcardLabelVO> labelVOList = new ArrayList<>(pagePo.getPageSize());
            giftcardLabelList.forEach(giftcardLabel -> {
                GiftcardLabelVO giftcardLabelVO = new GiftcardLabelVO();
                BeanCopyUtil.copy(giftcardLabel, giftcardLabelVO);
                giftcardLabelVO.setLabelId(giftcardLabel.getId());
                labelVOList.add(giftcardLabelVO);
            });
            page.setRecords(labelVOList);
        }
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public List<LabelShowVO> showLabelList() {
        EntityWrapper<GiftcardLabel> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("delFlag", 0);
        List<GiftcardLabel> giftcardLabels = selectList(entityWrapper);
        List<LabelShowVO> labelShowVOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(giftcardLabels)) {
            giftcardLabels.forEach(giftcardLabel -> {
                //只显示有上架的普通电子卡的标签
                EntityWrapper<GiftcardBatch> batchEntityWrapper = new EntityWrapper<>();
                batchEntityWrapper.eq("labelId", giftcardLabel.getId());
                batchEntityWrapper.eq("cardAttr", CardAttrEnum.COMMON.getId());
                batchEntityWrapper.eq("delFlag", 0);
                batchEntityWrapper.eq("medium", CardMediumEnum.ELECT_CARD.getMediumId());
                batchEntityWrapper.eq("issueStatus", IssueStatusEnum.CARD_READY.getStatusId());
                if (giftcardBatchService.selectCount(batchEntityWrapper) > 0) {
                    LabelShowVO labelShowVO = new LabelShowVO();
                    labelShowVO.setLabelId(giftcardLabel.getId());
                    labelShowVO.setLabelName(giftcardLabel.getLabelName());
                    labelShowVOList.add(labelShowVO);
                }
            });
        }
        return labelShowVOList;
    }

    @Override
    public List<LabelShowVO> allLabelList() {
        EntityWrapper<GiftcardLabel> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("delFlag", 0);
        List<GiftcardLabel> giftcardLabels = selectList(entityWrapper);
        List<LabelShowVO> labelShowVOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(giftcardLabels)) {
            giftcardLabels.forEach(giftcardLabel -> {
                LabelShowVO labelShowVO = new LabelShowVO();
                labelShowVO.setLabelId(giftcardLabel.getId());
                labelShowVO.setLabelName(giftcardLabel.getLabelName());
                labelShowVOList.add(labelShowVO);
            });
        }
        return labelShowVOList;
    }
}

