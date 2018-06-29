package com.topaiebiz.giftcard.service;

import com.baomidou.mybatisplus.service.IService;
import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.card.dto.CardBatchDTO;
import com.topaiebiz.card.dto.PrizeCardDTO;
import com.topaiebiz.giftcard.entity.GiftcardBatch;
import com.topaiebiz.giftcard.vo.*;

import java.util.List;
import java.util.Map;

/**
 * @description: 礼卡批次服务
 * @author: Jeff Chen
 * @date: created in 下午3:05 2018/1/12
 */
public interface GiftcardBatchService extends IService<GiftcardBatch>{

    /**
     * 保存草稿
     * @param giftcardBatch
     * @return
     */
    Boolean save(GiftcardBatch giftcardBatch);

    /**
     * 保存或更新
     * @param giftcardBatch
     * @return
     */
    Boolean saveOrUpd(GiftcardBatch giftcardBatch);
    /**
     * 分页查询
     * @param giftcardIssueReq
     * @return
     */
    PageInfo<GiftcardIssueVO> queryGiftcardIssue(GiftcardIssueReq giftcardIssueReq);


    /**
     * 修改礼卡批次状态：0-待审核，1-审核通过（未上架/未生产），2-未通过，3-已上架/未入库，4-已入库
     * @param giftcardIssue
     * @return
     */
    Boolean changeIssueStatus(GiftcardBatch giftcardIssue);

    /**
     * 编辑
     * @param giftcardIssue
     * @return
     */
    Boolean editGiftcardIssue(GiftcardBatch giftcardIssue);

    /**
     * 修改优先级
     * @param giftcardIssue
     * @return
     */
    Boolean updatePriority(GiftcardBatch giftcardIssue);

    /**
     * 生产实体卡
     * @param giftcardIssue
     * @return
     */
    Integer produceSolidCards(GiftcardBatch giftcardIssue);

    /**
     * 按标签or属性查询
     * @return
     */
    PageInfo<GiftcardShowVO> selectGiftcardShow(GiftcardShowReq giftcardShowReq);

    /**
     * 前端展示礼卡详情
     * @param giftcardShowReq
     * @return
     */
    GiftcardShowDetailVO detailGiftcardShow(GiftcardShowReq giftcardShowReq);

    /**
     * 为用户生产电子卡
     * @param placeOrderVO
     * @return
     */
    List<String> produceElecCardsForMember(PlaceOrderVO placeOrderVO);

    /**
     * 查询指定id列表的批次信息
     * @param batchIds
     * @return
     */
    List<CardBatchDTO> getCardBatchByIds(List<Long> batchIds);

    /**
     * 获取卡号起止
     * @param prefix
     * @param issueNum
     * @return
     */
    Map<String, Object> getCardNoSpan(String prefix, Integer issueNum);

    /**
     * 为用户绑定某批次的一张卡
     * @param batchId
     * @param memberId
     * @return
     */
    PrizeCardDTO bindCardFromGiftcardBatch(Long batchId, Long memberId);

    /**
     * 封面迁移
     * @return
     */
    Integer updateCover();

    /**
     * 根据id查询批次详情
     * @param batchId
     * @return
     */
    GiftcardIssueVO getById(Long batchId);

    /**
     * 查询指定批次的圈定商品id列表
     * @param batchId
     * @return
     */
    List<Long> getGiftcardGoodsByBatchId(Long batchId);

    /**
     * 根据商品id查询圈定的商品信息
     * @param goodsIds
     * @return
     */
    List<GiftcardGoodsVO> getGiftcardGoodsByGoodsIds(List<Long> goodsIds);

}
