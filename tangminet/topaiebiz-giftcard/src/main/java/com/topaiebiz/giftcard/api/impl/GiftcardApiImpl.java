package com.topaiebiz.giftcard.api.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.exception.SystemExceptionEnum;
import com.topaiebiz.card.api.GiftCardApi;
import com.topaiebiz.card.constant.ApplyScopeEnum;
import com.topaiebiz.card.constant.CardOrderStatusEnum;
import com.topaiebiz.card.dto.*;
import com.topaiebiz.giftcard.entity.GiftcardOrder;
import com.topaiebiz.giftcard.entity.GiftcardUnit;
import com.topaiebiz.giftcard.enums.CardUnitStatusEnum;
import com.topaiebiz.giftcard.enums.GiftcardExceptionEnum;
import com.topaiebiz.giftcard.service.GiftcardBatchService;
import com.topaiebiz.giftcard.service.GiftcardOrderService;
import com.topaiebiz.giftcard.service.GiftcardUnitService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 礼卡对外接口
 * @author: Jeff Chen
 * @date: created in 下午7:31 2018/1/12
 */
@Service
public class GiftcardApiImpl implements GiftCardApi{
    @Autowired
    private GiftcardUnitService giftcardUnitService;
    @Autowired
    private GiftcardOrderService giftcardOrderService;
    @Autowired
    private GiftcardBatchService giftcardBatchService;
    @Override
    public MemberCardDTO getMemberValidCards(Long memberId) {
        List<GiftcardUnit> giftcardUnitList = giftcardUnitService.selectMemberBoundCards(memberId);
        MemberCardDTO memberCardDTO = new MemberCardDTO();
        if (!CollectionUtils.isEmpty(giftcardUnitList)) {
            List<BriefCardDTO> briefCardDTOList = new ArrayList<>(giftcardUnitList.size());
            giftcardUnitList.forEach(giftcardUnit -> {
                //可用卡片：绑定,余额不为0
                if (giftcardUnit.getCardStatus().equals(CardUnitStatusEnum.BOUND.getStatusCode())
                        && giftcardUnit.getBalance().compareTo(BigDecimal.ZERO)==1) {
                    memberCardDTO.setTotalCardNum(memberCardDTO.getTotalCardNum() + 1);
                    memberCardDTO.setTotalCardAmount(memberCardDTO.getTotalCardAmount().add(giftcardUnit.getBalance()));
                    BriefCardDTO briefCardDTO = new BriefCardDTO();
                    //兼容老数据
                    if (null == giftcardUnit.getApplyScope()) {
                        giftcardUnit.setApplyScope(ApplyScopeEnum.APPLY_ALL.getScopeId());
                    }
                    if (null == giftcardUnit.getPriority()) {
                        giftcardUnit.setPriority(10);
                    }
                    if (null == giftcardUnit.getCreatedTime()) {
                        giftcardUnit.setCreatedTime(new Date(1519747200000L));
                    }
                    if (null == giftcardUnit.getDeadTime()) {
                        giftcardUnit.setDeadTime(new Date(1835280000000L));
                    }
                    briefCardDTO.setAmount(giftcardUnit.getBalance());
                    briefCardDTO.setApplyScope(giftcardUnit.getApplyScope());
                    briefCardDTO.setCardNo(giftcardUnit.getCardNo());
                    briefCardDTO.setPriority(giftcardUnit.getPriority());
                    briefCardDTO.setCardName(giftcardUnit.getCardName());
                    //发卡时间即产生卡号的时间
                    briefCardDTO.setIssuedTime((int) (giftcardUnit.getCreatedTime().getTime()/1000));
                    briefCardDTO.setExpiredTime((int) (giftcardUnit.getDeadTime().getTime() / 1000));

                    //圈店铺
                    if (ApplyScopeEnum.APPLY_INCLUDE.getScopeId().equals(giftcardUnit.getApplyScope())
                            || ApplyScopeEnum.APPLY_EXCLUDE.getScopeId().equals(giftcardUnit.getApplyScope())) {
                        //非全平台适用
                        if (StringUtils.isNotBlank(giftcardUnit.getStoreIds())) {
                            String[] ids = giftcardUnit.getStoreIds().split(",");
                            if (null != ids) {
                                List<Long> storeIds = new ArrayList<>(ids.length);
                                for (String id : ids) {
                                    if (StringUtils.isNotBlank(id)) {
                                        storeIds.add(Long.valueOf(id));
                                    }
                                }
                                briefCardDTO.setStoreIds(storeIds);
                            }
                        }
                    }
                    //圈商品
                    if (ApplyScopeEnum.APPLY_GOODS.getScopeId().equals(giftcardUnit.getApplyScope())) {
                        briefCardDTO.setGoodsIds(giftcardBatchService.getGiftcardGoodsByBatchId(giftcardUnit.getBatchId()));
                    }
                    briefCardDTOList.add(briefCardDTO);
                }
            });
            memberCardDTO.setBriefCardDTOList(briefCardDTOList);
        }

        return memberCardDTO;
    }

    @Override
    public Boolean payByCards(PayInfoDTO payInfoDTO) {
        return giftcardUnitService.payByCards(payInfoDTO);
    }

    @Override
    public Boolean refundCards(RefundOrderDTO refundOrderDTO) {
        return giftcardUnitService.refundCards(refundOrderDTO);
    }

    @Override
    public CardBalanceDTO getBalanceByMember(Long memberId) {
        CardBalanceDTO cardBalanceDTO = new CardBalanceDTO();
        List<GiftcardUnit> giftcardUnitList = giftcardUnitService.selectMemberBoundCards(memberId);
        if (!CollectionUtils.isEmpty(giftcardUnitList)) {
            giftcardUnitList.forEach(giftcardUnit -> {
                //不可用余额
                if (giftcardUnit.getCardStatus().equals(CardUnitStatusEnum.FREEZED.getStatusCode())) {
                    cardBalanceDTO.setFreezeBalance(cardBalanceDTO.getFreezeBalance().add(giftcardUnit.getBalance()));
                } else {
                    cardBalanceDTO.setBalance(cardBalanceDTO.getBalance().add(giftcardUnit.getBalance()));
                }
            });
        }

        return cardBalanceDTO;
    }

    @Override
    public Boolean cardPaidCallBack(CardPaidResultDTO paidResultDTO) {
        if(null == paidResultDTO||null == paidResultDTO.getOrderId()||null == paidResultDTO.getPayAmount()
                ||null == paidResultDTO.getPayCode() || null == paidResultDTO.getPaySn()) {
            throw new GlobalException(SystemExceptionEnum.ILLEGAL_PARAM);
        }
        GiftcardOrder giftcardOrder = new GiftcardOrder();
        giftcardOrder.setPaySn(paidResultDTO.getPaySn());
        giftcardOrder.setId(paidResultDTO.getOrderId());
        giftcardOrder.setPayAmount(paidResultDTO.getPayAmount());
        giftcardOrder.setPayCode(paidResultDTO.getPayCode());
        return giftcardOrderService.updateOrderAfterPay(giftcardOrder);
    }

    @Override
    public BriefCardOrderDTO getOrderInfoById(Long orderId) {
        GiftcardOrder giftcardOrder = giftcardOrderService.selectById(orderId);
        if (null == giftcardOrder) {
            throw new GlobalException(GiftcardExceptionEnum.ORDER_NOT_EXIST);
        }
        BriefCardOrderDTO briefCardOrderDTO = new BriefCardOrderDTO();
        briefCardOrderDTO.setOrderId(orderId);
        briefCardOrderDTO.setPayAmount(giftcardOrder.getPayAmount());
        briefCardOrderDTO.setOrderStatus(giftcardOrder.getOrderStatus());
        return briefCardOrderDTO;
    }

    @Override
    public List<CardBatchDTO> getCardBatchByIds(List<Long> batchIds) {
        if (CollectionUtils.isEmpty(batchIds)) {
            throw  new GlobalException(GiftcardExceptionEnum.ISSUE_NOT_EXIST);
        }
        return giftcardBatchService.getCardBatchByIds(batchIds);
    }

    @Override
    public Integer updGiftcardOrderStatus(Date fromTime) {
        EntityWrapper<GiftcardOrder> wrapper = new EntityWrapper<>();
        wrapper.le("createdTime", fromTime);
        wrapper.eq("orderStatus", CardOrderStatusEnum.UNPAID.getStatusCode());
        GiftcardOrder giftcardOrder = new GiftcardOrder();
        giftcardOrder.setOrderStatus(CardOrderStatusEnum.CANCELED.getStatusCode());
        giftcardOrder.setModifiedTime(new Date());

        return giftcardOrderService.updateBatchByWrapper(giftcardOrder,wrapper);
    }

    @Override
    public Integer updGiftcardUnitStatus() {
        return giftcardUnitService.updGiftcardUnitStatus();
    }
    @Override
    public PrizeCardDTO bindCardFromGiftcardBatch(Long batchId, Long memberId) {
        return giftcardBatchService.bindCardFromGiftcardBatch(batchId,memberId);
    }

    @Override
    public Map<String, Object> getBalanceByMemberList(List<Long> memberIdList) {
        return giftcardUnitService.getBalanceByMemberIds(memberIdList,1);
    }

    @Override
    public Map<String, Object> getBalanceByMemberList(List<Long> memberIdList, Integer useType) {
        return giftcardUnitService.getBalanceByMemberIds(memberIdList,useType);
    }
}
