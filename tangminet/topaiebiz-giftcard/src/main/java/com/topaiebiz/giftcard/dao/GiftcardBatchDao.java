package com.topaiebiz.giftcard.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.topaiebiz.giftcard.entity.GiftcardBatch;
import com.topaiebiz.giftcard.vo.GiftcardIssueReq;
import com.topaiebiz.giftcard.vo.GiftcardShowReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 礼卡批次dao
 * @author: Jeff Chen
 * @date: created in 上午10:53 2018/1/12
 */
@Mapper
public interface GiftcardBatchDao extends BaseMapper<GiftcardBatch>{

    /**
     * 分页查询
     * @param page
     * @return
     */
    List<GiftcardBatch> queryGiftcardIssue(Page page, GiftcardIssueReq giftcardIssueReq);

    /**
     * 前端展示卡信息
     * @param page
     * @param giftcardShowReq
     * @return
     */
    List<GiftcardBatch> queryGiftcardShow(Page page, GiftcardShowReq giftcardShowReq);

    /**
     * 指定标签和属性后分组查询
     * @param giftcardShowReq
     * @return
     */
    List<GiftcardBatch> queryGiftcardGroupByParam(GiftcardShowReq giftcardShowReq);

    /**
     * 获取指定前缀最大被占用的卡号
     * @param prefix
     * @return
     */
    GiftcardBatch getCardNoSpan(@Param("prefix") String prefix);
}
