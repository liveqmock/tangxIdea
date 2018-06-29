package com.topaiebiz.giftcard.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.card.constant.ApplyScopeEnum;
import com.topaiebiz.card.dto.CardBatchDTO;
import com.topaiebiz.card.dto.PrizeCardDTO;
import com.topaiebiz.file.mgmt.service.FileMgmtService;
import com.topaiebiz.giftcard.BizConstants;
import com.topaiebiz.giftcard.dao.GiftcardBatchDao;
import com.topaiebiz.giftcard.dao.GiftcardGoodsDao;
import com.topaiebiz.giftcard.dao.GiftcardLabelDao;
import com.topaiebiz.giftcard.dao.GiftcardUnitDao;
import com.topaiebiz.giftcard.entity.*;
import com.topaiebiz.giftcard.enums.*;
import com.topaiebiz.giftcard.service.GiftcardBatchService;
import com.topaiebiz.giftcard.service.GiftcardOpLogService;
import com.topaiebiz.giftcard.util.AESUtil;
import com.topaiebiz.giftcard.util.BizSerialUtil;
import com.topaiebiz.giftcard.util.DateUtil;
import com.topaiebiz.giftcard.vo.*;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 下午3:48 2018/1/12
 */
@Service
@Slf4j
public class GiftcardBatchServiceImpl extends ServiceImpl<GiftcardBatchDao, GiftcardBatch> implements GiftcardBatchService {

    @Autowired
    private GiftcardBatchDao giftcardBatchDao;
    @Autowired
    private GiftcardUnitDao giftcardUnitDao;
    @Autowired
    private GiftcardLabelDao giftcardLabelDao;
    @Autowired
    private GiftcardGoodsDao giftcardGoodsDao;

    @Autowired
    private StoreApi storeApi;
    @Autowired
    private GoodsApi goodsApi;
    @Autowired
    private FileMgmtService fileMgmtService;

    @Autowired
    private GiftcardOpLogService giftcardOpLogService;
    @Override
    public Boolean save(GiftcardBatch giftcardBatch) {
        //判断卡号区间是可用
        GiftcardBatch batch = giftcardBatchDao.getCardNoSpan(giftcardBatch.getPrefix());
        if ((giftcardBatch.getNoEnd() - giftcardBatch.getNoStart() + 1) != giftcardBatch.getIssueNum()) {
            throw new GlobalException(GiftcardExceptionEnum.CARD_NO_SPAN_ERROR);
        }
        if (null != batch) {
            if (batch.getNoEnd() >= giftcardBatch.getNoEnd()
                    || batch.getNoStart() >= giftcardBatch.getNoStart()) {
                throw new GlobalException(GiftcardExceptionEnum.INVALID_NO_SPAN);
            }
        }
        //设置批次号，状态，售出
        giftcardBatch.setBatchNo(BizSerialUtil.getBatchNo());
        giftcardBatch.setOutNum(0);
        giftcardBatch.setIssueStatus(IssueStatusEnum.AUDIT_WAIT.getStatusId());
        return insert(giftcardBatch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpd(GiftcardBatch giftcardBatch) {
        //草稿不校验
        //判断卡号区间是可用
        if (IssueStatusEnum.AUDIT_WAIT.getStatusId().equals(giftcardBatch.getIssueStatus())) {
            if ((giftcardBatch.getNoEnd() - giftcardBatch.getNoStart() + 1) != giftcardBatch.getIssueNum()) {
                throw new GlobalException(GiftcardExceptionEnum.CARD_NO_SPAN_ERROR);
            }
            GiftcardBatch batch = giftcardBatchDao.getCardNoSpan(giftcardBatch.getPrefix());
            //更新时不校验卡号区间
            if (null != batch && !batch.getId().equals(giftcardBatch.getId())) {
                if (batch.getNoEnd() >= giftcardBatch.getNoEnd() || batch.getNoStart() >= giftcardBatch.getNoStart()) {
                    throw new GlobalException(GiftcardExceptionEnum.INVALID_NO_SPAN);
                }
            }
        }
        if (null != giftcardBatch.getId()) {
            if (giftcardBatchDao.updateById(giftcardBatch)>0) {
                if (ApplyScopeEnum.APPLY_GOODS.getScopeId().equals(giftcardBatch.getApplyScope())) {
                    saveGiftcardGoods(giftcardBatch.getId(), giftcardBatch.getGoodsIds());
                }
            }
            return true;
        } else {
            //设置批次号，状态，售出
            giftcardBatch.setBatchNo(BizSerialUtil.getBatchNo());
            giftcardBatch.setOutNum(0);
            if (giftcardBatchDao.insert(giftcardBatch)>0) {
                if (ApplyScopeEnum.APPLY_GOODS.getScopeId().equals(giftcardBatch.getApplyScope())) {
                    saveGiftcardGoods(giftcardBatch.getId(), giftcardBatch.getGoodsIds());
                }
            }
            return true;
        }
    }

    @Override
    public PageInfo<GiftcardIssueVO> queryGiftcardIssue(GiftcardIssueReq giftcardIssueReq) {
        Page page = PageDataUtil.buildPageParam(giftcardIssueReq);
        List<GiftcardBatch> giftcardIssueList = giftcardBatchDao.queryGiftcardIssue(page, giftcardIssueReq);
        page.setRecords(giftcardIssueList);
        if (!CollectionUtils.isEmpty(giftcardIssueList)) {
            List<GiftcardIssueVO> giftcardIssueVOList = new ArrayList<>(giftcardIssueReq.getPageSize());
            giftcardIssueList.forEach(giftcardIssue -> {
                GiftcardIssueVO giftcardIssueVO = new GiftcardIssueVO();
                BeanCopyUtil.copy(giftcardIssue, giftcardIssueVO);
                giftcardIssueVO.setBatchId(giftcardIssue.getId());
                giftcardIssueVOList.add(giftcardIssueVO);
            });
            page.setRecords(giftcardIssueVOList);
        }
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeIssueStatus(GiftcardBatch giftcardIssue) {
        GiftcardBatch issue = giftcardBatchDao.selectById(giftcardIssue.getId());
        if (null == issue || issue.getDelFlag().byteValue() == Constants.DeletedFlag.DELETED_YES) {
            throw new GlobalException(GiftcardExceptionEnum.ISSUE_NOT_EXIST);
        }
        if (issue.getIssueStatus().equals(giftcardIssue.getIssueStatus())) {
            throw new GlobalException(GiftcardExceptionEnum.NO_CHANGE);
        }
        //只有通过审核的才能上架或生产
        if (giftcardIssue.getIssueStatus().equals(IssueStatusEnum.CARD_READY.getStatusId())) {
            if (!issue.getIssueStatus().equals(IssueStatusEnum.AUDIT_PASS.getStatusId())) {
                throw new GlobalException(GiftcardExceptionEnum.NOT_AUDIT);
            }
        }
        //只有已生产的实体卡才能入库
        if (giftcardIssue.getIssueStatus().equals(IssueStatusEnum.CARD_IMPORT.getStatusId())) {
            if (!issue.getMedium().equals(CardMediumEnum.SOLID_CARD.getMediumId())
                    || !issue.getIssueStatus().equals(IssueStatusEnum.CARD_READY.getStatusId())) {
                throw new GlobalException(GiftcardExceptionEnum.NOT_IMPORT);
            }
        }
        //操作日志
        GiftcardOpLog giftcardOpLog = new GiftcardOpLog();
        giftcardOpLog.setBizId(giftcardIssue.getId());
        giftcardOpLog.setNote(giftcardIssue.getRemark());
        giftcardOpLog.setOperator(giftcardIssue.getModifier());
        giftcardOpLog.setOpTime(new Date());
        giftcardOpLog.setOpSrc(OpSrcEnum.OP_BATCH.getSrcId());
        giftcardOpLog.setOpType(IssueStatusEnum.getById(giftcardIssue.getIssueStatus()).getDesc());
        if (giftcardOpLogService.insert(giftcardOpLog)) {
            return giftcardBatchDao.updateById(giftcardIssue) > 0;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean editGiftcardIssue(GiftcardBatch giftcardIssue) {
        //电子卡未上架可以编辑
        GiftcardBatch issue = selectById(giftcardIssue.getId());
        if (null == issue || issue.getDelFlag().byteValue() == Constants.DeletedFlag.DELETED_YES) {
            throw new GlobalException(GiftcardExceptionEnum.ISSUE_NOT_EXIST);
        }
        if (CardMediumEnum.ELECT_CARD.getMediumId() != issue.getMedium()) {
            throw new GlobalException(GiftcardExceptionEnum.NOT_TO_EDIT);
        }
        //操作日志
        GiftcardOpLog giftcardOpLog = new GiftcardOpLog();
        giftcardOpLog.setBizId(giftcardIssue.getId());
        giftcardOpLog.setNote(giftcardIssue.getRemark());
        giftcardOpLog.setOperator(giftcardIssue.getModifier());
        giftcardOpLog.setOpTime(new Date());
        giftcardOpLog.setOpSrc(OpSrcEnum.OP_BATCH.getSrcId());
        giftcardOpLog.setOpType(CardOpTypeEnum.EDIT.getOpType());
        if (giftcardOpLogService.insert(giftcardOpLog)) {
            return giftcardBatchDao.updateById(giftcardIssue) > 0;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePriority(GiftcardBatch giftcardIssue) {
        //优先级在1-10直接
        if (null == giftcardIssue || null == giftcardIssue.getPriority()
                || giftcardIssue.getPriority() < 1 || giftcardIssue.getPriority() > 10) {
            throw new GlobalException(GiftcardExceptionEnum.PRIORIRTY_OVERFLOW);
        }
        //操作日志
        GiftcardOpLog giftcardOpLog = new GiftcardOpLog();
        giftcardOpLog.setBizId(giftcardIssue.getId());
        giftcardOpLog.setNote(giftcardIssue.getRemark());
        giftcardOpLog.setOperator(giftcardIssue.getModifier());
        giftcardOpLog.setOpTime(new Date());
        giftcardOpLog.setOpSrc(OpSrcEnum.OP_BATCH.getSrcId());
        giftcardOpLog.setOpType(CardOpTypeEnum.UPD_PRIORITY.getOpType());
        if (giftcardOpLogService.insert(giftcardOpLog)) {
            return giftcardBatchDao.updateById(giftcardIssue) > 0;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer produceSolidCards(GiftcardBatch giftcardBatch) {
        GiftcardBatch batch = giftcardBatchDao.selectById(giftcardBatch.getId());
        if (null == batch || CardMediumEnum.SOLID_CARD.getMediumId() != batch.getMedium()
                || !IssueStatusEnum.AUDIT_PASS.getStatusId().equals(batch.getIssueStatus())) {
            throw new GlobalException(GiftcardExceptionEnum.ONLY_PRODUCE_SOLID);
        }
        int icount = 0;
        //卡号生成规则：前缀+批次发行时分配的卡号区间
        long cardNo = batch.getNoStart();
        if (null != batch.getIssueNum() && batch.getIssueNum() > 0) {
            for (int i = 0; i < batch.getIssueNum(); i++) {
                GiftcardUnit giftcardUnit = new GiftcardUnit();
                giftcardUnit.setBatchNo(batch.getBatchNo());
                giftcardUnit.setBindingMember(0L);
                giftcardUnit.setCardNo(BizSerialUtil.getCardNo(batch.getPrefix(), cardNo));
                giftcardUnit.setCardStatus(CardUnitStatusEnum.INACTIVED.getStatusCode());
                giftcardUnit.setGivenStatus(batch.getGivenFlag());
                giftcardUnit.setOwner(0L);
                giftcardUnit.setCreator(giftcardBatch.getCreator());
                giftcardUnit.setCreatedTime(giftcardBatch.getCreatedTime());
                giftcardUnit.setModifier(giftcardBatch.getModifier());
                giftcardUnit.setModifiedTime(giftcardBatch.getModifiedTime());
                giftcardUnit.setBatchId(batch.getId());
                giftcardUnit.setLabelId(batch.getLabelId());
                giftcardUnit.setBalance(batch.getFaceValue());
                giftcardUnit.setPassword(AESUtil.encrypt(BizSerialUtil.getFixLenthString(6), AESUtil.CARD_ENCRYPT_KEY));
                if (giftcardUnitDao.insert(giftcardUnit) > 0) {
                    icount++;
                    cardNo++;
                }
            }
        }
        if (icount == batch.getIssueNum()) {
            //更新状态到：待入库
            giftcardBatch.setIssueStatus(IssueStatusEnum.CARD_READY.getStatusId());
            giftcardBatch.setOutNum(icount);
            giftcardBatch.setCreatedTime(null);
            giftcardBatch.setCreator(null);
            //操作日志
            GiftcardOpLog giftcardOpLog = new GiftcardOpLog();
            giftcardOpLog.setBizId(giftcardBatch.getId());
            giftcardOpLog.setNote(giftcardBatch.getRemark());
            giftcardOpLog.setOperator(giftcardBatch.getModifier());
            giftcardOpLog.setOpTime(new Date());
            giftcardOpLog.setOpSrc(OpSrcEnum.OP_BATCH.getSrcId());
            giftcardOpLog.setOpType(CardOpTypeEnum.PRODUCE_CARD.getOpType());
            if (giftcardOpLogService.insert(giftcardOpLog)) {
                giftcardBatchDao.updateById(giftcardBatch);
            }
        } else {
            log.error("生产实体失败：{}", JSONObject.toJSON(giftcardBatch));
            //回滚
            throw new GlobalException(GiftcardExceptionEnum.CARD_PRODUCE_ERROR);
        }
        return icount;
    }

    @Override
    public PageInfo<GiftcardShowVO> selectGiftcardShow(GiftcardShowReq giftcardShowReq) {
        Page page = PageDataUtil.buildPageParam(giftcardShowReq);
        List<GiftcardBatch> issues = giftcardBatchDao.queryGiftcardShow(page, giftcardShowReq);
        if (!CollectionUtils.isEmpty(issues)) {
            List<GiftcardShowVO> showVOList = new ArrayList<>(issues.size());
            issues.forEach(giftcardIssue -> {
                GiftcardShowVO showVO = new GiftcardShowVO();
                showVO.setBatchId(giftcardIssue.getId());
                showVO.setCardName(giftcardIssue.getCardName());
                showVO.setCover(giftcardIssue.getCover());
                showVO.setFaceValue(giftcardIssue.getFaceValue());
                showVO.setSalePrice(giftcardIssue.getSalePrice());
                showVO.setScope(ApplyScopeEnum.getById(giftcardIssue.getApplyScope()).getScopeDesc());
                showVO.setDeadTime(DateUtil.renewalDays(new Date(), giftcardIssue.getValidDays()));
                showVOList.add(showVO);
            });
            page.setRecords(showVOList);
        }
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public GiftcardShowDetailVO detailGiftcardShow(GiftcardShowReq giftcardShowReq) {

        GiftcardBatch issue = selectById(giftcardShowReq.getBatchId());
        if (null == issue) {
            throw new GlobalException(GiftcardExceptionEnum.ISSUE_NOT_EXIST);
        }
        //相同标签相同属性的面值列表
        giftcardShowReq.setLabelId(issue.getLabelId());
        giftcardShowReq.setCardAttr(issue.getCardAttr());
        List<GiftcardBatch> giftcardIssueList = giftcardBatchDao.queryGiftcardGroupByParam(giftcardShowReq);
        List<IssueItemVO> issueItemVOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(giftcardIssueList)) {
            giftcardIssueList.forEach(giftcardIssue -> {
                IssueItemVO itemVO = new IssueItemVO();
                itemVO.setFaceValue(giftcardIssue.getFaceValue());
                itemVO.setSalePrice(giftcardIssue.getSalePrice());
                itemVO.setBatchId(giftcardIssue.getId());
                //产品逻辑：同一标签下同一面值不同售价的也要展示
                if (giftcardIssue.getFaceValue().compareTo(issue.getFaceValue()) == 0
                        && giftcardIssue.getSalePrice().compareTo(issue.getSalePrice()) == 0) {
                    itemVO.setBatchId(issue.getId());
                }
                issueItemVOList.add(itemVO);
            });
        } else {
            //当前礼卡
            IssueItemVO itemVO = new IssueItemVO();
            itemVO.setFaceValue(issue.getFaceValue());
            itemVO.setSalePrice(issue.getSalePrice());
            itemVO.setBatchId(issue.getId());
            issueItemVOList.add(itemVO);
        }


        GiftcardShowDetailVO giftcardShowDetailVO = new GiftcardShowDetailVO();
        BeanCopyUtil.copy(issue, giftcardShowDetailVO);
        //可下单数量
        giftcardShowDetailVO.setRestNum(issue.getIssueNum() - issue.getOrderQty());
        giftcardShowDetailVO.setBatchId(issue.getId());
        giftcardShowDetailVO.setItemList(issueItemVOList);
        if (!ApplyScopeEnum.APPLY_ALL.getScopeId().equals(issue.getApplyScope())) {
            giftcardShowDetailVO.setStoreNameList(getStoreName(issue.getStoreIds()));
        }
        giftcardShowDetailVO.setOrderKey(BizSerialUtil.getUUID());
        return giftcardShowDetailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> produceElecCardsForMember(PlaceOrderVO placeOrderVO) {
        GiftcardBatch batch = giftcardBatchDao.selectById(placeOrderVO.getBatchId());
        if (null == batch || CardMediumEnum.ELECT_CARD.getMediumId() != batch.getMedium()
                || !IssueStatusEnum.CARD_READY.getStatusId().equals(batch.getIssueStatus())) {
            throw new GlobalException(GiftcardExceptionEnum.ONLY_PRODUCE_ELEC);
        }
        EntityWrapper<GiftcardBatch> wrapper = new EntityWrapper<>();
        wrapper.eq("id", batch.getId());
        //乐观锁
        wrapper.eq("outNum", batch.getOutNum());
        GiftcardBatch updBatch = new GiftcardBatch();
        updBatch.setOutNum(batch.getOutNum() + placeOrderVO.getIssueNum());
        if (!update(updBatch, wrapper)) {
            throw new GlobalException(GiftcardExceptionEnum.LIMIT_TO_BUY);
        }
        //卡号列表
        List<String> cardNoList = new ArrayList<>(placeOrderVO.getIssueNum());
        //卡号生成规则：前缀+批次发行时分配的卡号区间
        long cardNo = batch.getNoStart() + batch.getOutNum();
        for (int i = 0; i < placeOrderVO.getIssueNum(); i++) {
            GiftcardUnit giftcardUnit = new GiftcardUnit();
            giftcardUnit.setBatchNo(batch.getBatchNo());
            giftcardUnit.setBindingMember(placeOrderVO.getMemberId());
            giftcardUnit.setCardNo(BizSerialUtil.getCardNo(batch.getPrefix(), cardNo));
            giftcardUnit.setCardStatus(CardUnitStatusEnum.BOUND.getStatusCode());
            giftcardUnit.setGivenStatus(batch.getGivenFlag());
            giftcardUnit.setOwner(placeOrderVO.getMemberId());
            giftcardUnit.setCreator(BizConstants.SYS_USER_ORDER);
            giftcardUnit.setCreatedTime(new Date());
            giftcardUnit.setModifier(BizConstants.SYS_USER_ORDER);
            giftcardUnit.setModifiedTime(new Date());
            giftcardUnit.setBatchId(batch.getId());
            giftcardUnit.setLabelId(batch.getLabelId());
            giftcardUnit.setBindingTime(new Date());
            giftcardUnit.setBalance(batch.getFaceValue());
            giftcardUnit.setPassword(AESUtil.encrypt(BizSerialUtil.getFixLenthString(6), AESUtil.CARD_ENCRYPT_KEY));
            giftcardUnit.setDeadTime(DateUtil.renewalDays(new Date(), batch.getValidDays()));
            //卖出就激活
            giftcardUnit.setActiveTime(new Date());
            if (giftcardUnitDao.insert(giftcardUnit) > 0) {
                cardNo++;
                cardNoList.add(giftcardUnit.getCardNo());
            }
        }
        return cardNoList;
    }

    private List<String> getStoreName(String storeIds) {
        List<String> storeName = new ArrayList<>();
        if (StringUtils.isNotBlank(storeIds)) {
            String[] storeArray = storeIds.split(",");
            if (ArrayUtils.isNotEmpty(storeArray)) {
                List<Long> storeIdList = new ArrayList<>(storeArray.length);
                for (String storeId : storeArray) {
                    storeIdList.add(Long.valueOf(storeId));
                }
                Map<Long, StoreInfoDetailDTO> storeMap = storeApi.getStoreMap(storeIdList);
                if (null != storeMap) {
                    Collection<StoreInfoDetailDTO> values = storeMap.values();
                    if (!CollectionUtils.isEmpty(values)) {
                        for (StoreInfoDetailDTO dto : values) {
                            storeName.add(dto.getName());
                        }
                    }
                }
            }
        }

        return storeName;
    }

    @Override
    public List<CardBatchDTO> getCardBatchByIds(List<Long> batchIds) {
        List<CardBatchDTO> batchDTOS = new ArrayList<>(batchIds.size());
        List<GiftcardBatch> batchList = giftcardBatchDao.selectBatchIds(batchIds);
        if (!CollectionUtils.isEmpty(batchList)) {
            batchList.forEach(giftcardBatch -> {
                CardBatchDTO batchDTO = new CardBatchDTO();
                BeanCopyUtil.copy(giftcardBatch, batchDTO);
                batchDTO.setBatchId(giftcardBatch.getId());
                //库存
                batchDTO.setQty(giftcardBatch.getIssueNum() - giftcardBatch.getOrderQty());
                batchDTOS.add(batchDTO);
            });
        }
        return batchDTOS;
    }

    @Override
    public Map<String, Object> getCardNoSpan(String prefix, Integer issueNum) {
        //查询是否存在相同前缀
        //相同前缀的不同批次，卡号具有连续性
        GiftcardBatch giftcardBatch = giftcardBatchDao.getCardNoSpan(prefix);
        Map<String, Object> resMap = new HashMap<>(2);
        //兼容秒杀卡发行0张，占用该前缀的一个卡号资源
        if (0 == issueNum) {
            issueNum = 1;
        }
        if (null == giftcardBatch || null == giftcardBatch.getNoStart()
                || null == giftcardBatch.getNoStart()) {
            long start = BizSerialUtil.ID_START + 1;
            long end = BizSerialUtil.ID_START + issueNum;
            resMap.put("noStart", start);
            resMap.put("noEnd", end);
        } else {
            resMap.put("noStart", giftcardBatch.getNoEnd() + 1);
            resMap.put("noEnd", giftcardBatch.getNoEnd() + issueNum);
        }
        return resMap;
    }

    @Override
    public PrizeCardDTO bindCardFromGiftcardBatch(Long batchId, Long memberId) {
        GiftcardBatch batch = giftcardBatchDao.selectById(batchId);
        if (null == batch || CardMediumEnum.ELECT_CARD.getMediumId() != batch.getMedium()
                || !IssueStatusEnum.CARD_READY.getStatusId().equals(batch.getIssueStatus())) {
            throw new GlobalException(GiftcardExceptionEnum.ONLY_PRODUCE_ELEC);
        }
        EntityWrapper<GiftcardBatch> wrapper = new EntityWrapper<>();
        wrapper.eq("id", batch.getId());
        //乐观锁
        wrapper.eq("outNum", batch.getOutNum());
        GiftcardBatch updBatch = new GiftcardBatch();
        updBatch.setOutNum(batch.getOutNum() + 1);
        wrapper.ge("issueNum", updBatch.getOutNum());
        if (!update(updBatch, wrapper)) {
            throw new GlobalException(GiftcardExceptionEnum.BIND_CARD_MISS);
        }
        //卡号生成规则：前缀+批次发行时分配的卡号区间
        long cardNo = batch.getNoStart() + batch.getOutNum();
        GiftcardUnit giftcardUnit = new GiftcardUnit();
        giftcardUnit.setBatchNo(batch.getBatchNo());
        giftcardUnit.setBindingMember(memberId);
        giftcardUnit.setCardNo(BizSerialUtil.getCardNo(batch.getPrefix(), cardNo));
        giftcardUnit.setCardStatus(CardUnitStatusEnum.BOUND.getStatusCode());
        giftcardUnit.setGivenStatus(batch.getGivenFlag());
        giftcardUnit.setOwner(memberId);
        giftcardUnit.setCreator(BizConstants.BINDING_DIRECT);
        giftcardUnit.setCreatedTime(new Date());
        giftcardUnit.setModifier(BizConstants.BINDING_DIRECT);
        giftcardUnit.setModifiedTime(new Date());
        giftcardUnit.setBatchId(batch.getId());
        giftcardUnit.setLabelId(batch.getLabelId());
        giftcardUnit.setBindingTime(new Date());
        giftcardUnit.setActiveTime(new Date());
        giftcardUnit.setBalance(batch.getFaceValue());
        giftcardUnit.setPassword(AESUtil.encrypt(BizSerialUtil.getFixLenthString(6), AESUtil.CARD_ENCRYPT_KEY));
        giftcardUnit.setDeadTime(DateUtil.renewalDays(new Date(), batch.getValidDays()));
        if (giftcardUnitDao.insert(giftcardUnit) > 0) {
            PrizeCardDTO prizeCardDTO = new PrizeCardDTO();
            prizeCardDTO.setCardName(batch.getCardName());
            prizeCardDTO.setCover(batch.getCover());
            prizeCardDTO.setFaceValue(batch.getFaceValue());
            prizeCardDTO.setSalePrice(batch.getSalePrice());
            prizeCardDTO.setCardNo(giftcardUnit.getCardNo());
            return prizeCardDTO;
        }
        return null;
    }

    @Override
    public Integer updateCover() {
        List<GiftcardBatch> giftcardBatchList = selectList(new EntityWrapper<>());
        if (!CollectionUtils.isEmpty(giftcardBatchList)) {
            giftcardBatchList.forEach(giftcardBatch -> {
                String cover = giftcardBatch.getCover();
                if (StringUtils.isNotEmpty(cover)) {
                    String srcUrl = null;
                    String fileExt = null;
                    if (cover.contains("shop")) {
                        srcUrl = StringUtils.join("http://pic.motherbuy.com/", cover.substring(0, cover.indexOf("?")));
                        fileExt = FilenameUtils.getExtension(cover.substring(0, cover.indexOf("?")));
                    } else if (cover.contains("mamago-new")) {
                        srcUrl = cover;
                        fileExt = FilenameUtils.getExtension(cover);
                    }
                    if (null != fileExt) {
                        String newFileName = StringUtils.join("card/cover/", giftcardBatch.getBatchNo(), ".", fileExt);
                        try {
                            log.info("{} 图片:{}", srcUrl, newFileName);
                            byte[] data = newCover(srcUrl);
                            if (data.length > 0) {
                                fileMgmtService.uploadFile(data, newFileName);
                                giftcardBatch.setCover(newFileName);
                                giftcardBatchDao.updateById(giftcardBatch);
                            }
                        } catch (Exception ex) {
                            log.error(ex.getMessage(), ex);
                        }
                    }
                }
            });
        }
        return null;
    }

    private byte[] newCover(String srcUrl) {

        byte[] data = new byte[0];
        try {
            data = Request.Get(srcUrl).connectTimeout(300).socketTimeout(300).execute().returnContent().asBytes();
        } catch (IOException e) {
            log.error(">>>>>>>>>>>>>图片找不到：{}", srcUrl);
        }
        return data;
    }

    @Override
    public GiftcardIssueVO getById(Long batchId) {
        GiftcardIssueVO giftcardIssueVO = new GiftcardIssueVO();
        GiftcardBatch giftcardIssue = giftcardBatchDao.selectById(batchId);
        if (null != giftcardIssue) {
            BeanCopyUtil.copy(giftcardIssue, giftcardIssueVO);
            giftcardIssueVO.setBatchId(giftcardIssue.getId());
            //标签名
            GiftcardLabel giftcardLabel = giftcardLabelDao.selectById(giftcardIssue.getLabelId());
            if (null != giftcardLabel) {
                giftcardIssueVO.setLabelName(giftcardLabel.getLabelName());
            }
            //操作日志
            GiftcardOpLog giftcardOpLog = new GiftcardOpLog();
            giftcardOpLog.setBizId(batchId);
            giftcardOpLog.setOpSrc(OpSrcEnum.OP_BATCH.getSrcId());
            List<CardOpLogVO> opLogVOList = giftcardOpLogService.selectByBizId(giftcardOpLog);
            giftcardIssueVO.setOpLogList(opLogVOList);
        }
        CardBatchUsageVO cardBatchUsageVO = new CardBatchUsageVO();
        cardBatchUsageVO.setIssueNum(giftcardIssue.getIssueNum());
        cardBatchUsageVO.setCardNoSpan(giftcardIssue.getPrefix() + giftcardIssue.getNoStart()
                + "~" + giftcardIssue.getPrefix() + giftcardIssue.getNoEnd());
        List<Map<String, Object>> usageList = giftcardUnitDao.selectCardNumByStatus(batchId);
        if (!CollectionUtils.isEmpty(usageList)) {
            usageList.forEach(objectMap -> {
                switch (Integer.valueOf(objectMap.get("cardStatus").toString())) {
                    case 1:
                        cardBatchUsageVO.setBoundNum(Integer.valueOf(objectMap.get("usageNum").toString()));
                        break;
                    case 2:
                        cardBatchUsageVO.setBoundNum(Integer.valueOf(objectMap.get("usageNum").toString()));
                        break;
                    case 3:
                        cardBatchUsageVO.setFreezedNum(Integer.valueOf(objectMap.get("usageNum").toString()));
                        break;
                    case 4:
                        cardBatchUsageVO.setUseOutNum(Integer.valueOf(objectMap.get("usageNum").toString()));
                        break;
                    case 5:
                        cardBatchUsageVO.setExpiredNum(Integer.valueOf(objectMap.get("usageNum").toString()));
                        break;
                    default:
                        break;
                }
            });
        }
        giftcardIssueVO.setUsageVO(cardBatchUsageVO);
        //圈定的商品
        if (ApplyScopeEnum.APPLY_GOODS.getScopeId().equals(giftcardIssue.getApplyScope())) {
            giftcardIssueVO.setGoodsIds(giftcardGoodsDao.getGoodsByBatchId(giftcardIssue.getId()));
        }
        return giftcardIssueVO;
    }

    @Override
    public List<Long> getGiftcardGoodsByBatchId(Long batchId) {
        if (null == batchId) {
            return null;
        }
        return giftcardGoodsDao.getGoodsByBatchId(batchId);
    }

    @Override
    public List<GiftcardGoodsVO> getGiftcardGoodsByGoodsIds(List<Long> goodsIds) {
        if (!CollectionUtils.isEmpty(goodsIds)) {
            List<ItemDTO> itemDTOList = goodsApi.getItemMap(goodsIds);
            if (!CollectionUtils.isEmpty(itemDTOList)) {
                List<GiftcardGoodsVO> giftcardGoodsVOList = new ArrayList<>();
                itemDTOList.forEach(itemDTO -> {
                    GiftcardGoodsVO goodsVO = new GiftcardGoodsVO();
                    goodsVO.setGoodsId(itemDTO.getId());
                    goodsVO.setMarketPrice(itemDTO.getMarketPrice());
                    goodsVO.setPrice(itemDTO.getDefaultPrice());
                    goodsVO.setSaleImage(itemDTO.getPictureName());
                    goodsVO.setSalesVolume(itemDTO.getSalesVolome());
                    goodsVO.setName(itemDTO.getName());
                    giftcardGoodsVOList.add(goodsVO);
                });
                return giftcardGoodsVOList;
            }
        }
        return null;
    }

    private Boolean saveGiftcardGoods(Long batchId, List<Long> goodsIds) {
        if (null == batchId) {
            throw new GlobalException(GiftcardExceptionEnum.INVALID_REQ);
        }
        //删掉旧的数据
        EntityWrapper<GiftcardGoods> wrapper = new EntityWrapper<>();
        wrapper.eq("batchId", batchId);
        giftcardGoodsDao.delete(wrapper);
        for (Long goodsId : goodsIds) {
            GiftcardGoods giftcardGoods = new GiftcardGoods();
            giftcardGoods.setBatchId(batchId);
            giftcardGoods.setGoodsId(goodsId);
            giftcardGoods.setCreatedTime(new Date());
            giftcardGoodsDao.insert(giftcardGoods);
        }
        return true;
    }
}
