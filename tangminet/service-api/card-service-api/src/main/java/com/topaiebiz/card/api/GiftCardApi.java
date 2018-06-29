package com.topaiebiz.card.api;

import com.topaiebiz.card.dto.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 礼卡对外接口
 * @author: Jeff Chen
 * @date: created in 下午8:03 2018/1/8
 */
public interface GiftCardApi {

    /**
     * 获取指定用户的有效礼卡信息
     * @param memberId
     * @return
     */
    MemberCardDTO getMemberValidCards(Long memberId);

    /**
     * 用户用礼卡支付
     * @param payInfoDTO
     * @return
     */
    Boolean payByCards(PayInfoDTO payInfoDTO);

    /**
     * 礼卡退款
     * @param refundOrderDTO
     * @return
     */
    Boolean refundCards(RefundOrderDTO refundOrderDTO);

    /**
     * 查询用户礼卡余额：可用、不可用
     * @param memberId
     * @return
     */
    CardBalanceDTO getBalanceByMember(Long memberId);

    /**
     * 支付成功后回调
     * @param paidResultDTO
     * @return
     */
    Boolean cardPaidCallBack(CardPaidResultDTO paidResultDTO);

    /**
     * 订单id
     * @param orderId
     * @return
     */
    BriefCardOrderDTO getOrderInfoById(Long orderId);

    /**
     * 查询指定批次id的信息
     * @param batchIds
     * @return
     */
    List<CardBatchDTO> getCardBatchByIds(List<Long> batchIds);

    /**
     * 更新指定时间段内订单状态
     * @param fromTime
     * @return 更新的数量
     */
    Integer updGiftcardOrderStatus(Date fromTime);

    /**
     * 更新卡单元状态
     * @return 更新的数量
     */
    Integer updGiftcardUnitStatus();

    /**
     * 为用户绑定某批次的一张卡
     * @param batchId
     * @param memberId
     * @return
     */
    PrizeCardDTO bindCardFromGiftcardBatch(Long batchId, Long memberId);

    /**
     * 查询指定用户id列表对应的可用礼卡余额
     * @param memberIdList
     * @return 调用方要判空
     */
    @Deprecated
    Map<String, Object> getBalanceByMemberList(List<Long> memberIdList);

    /**
     * 查询指定用户id列表对应的可用礼卡余额
     * @param memberIdList
     * @param useType 1-可用 2-不可用
     * @return
     */
    Map<String, Object> getBalanceByMemberList(List<Long> memberIdList,Integer useType);

}
