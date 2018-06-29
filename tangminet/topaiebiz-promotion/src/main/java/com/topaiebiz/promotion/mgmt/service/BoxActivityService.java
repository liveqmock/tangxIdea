package com.topaiebiz.promotion.mgmt.service;

import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.dto.box.AwardRecordDTO;
import com.topaiebiz.promotion.mgmt.dto.box.BoxActivityDTO;
import com.topaiebiz.promotion.mgmt.dto.box.BoxReceiverDTO;
import com.topaiebiz.promotion.mgmt.dto.box.MemberBoxDTO;
import com.topaiebiz.promotion.mgmt.dto.box.json.ResBoxJsonDTO;

import java.util.List;

public interface BoxActivityService {
    /**
     * 产生宝箱，并更新数据
     * 根据会员编号，查询宝箱是否存在
     *
     * @param memberId 会员ID
     * @return
     */
    Boolean produceBox(Long memberId, Integer nodeType);

    /**
     * 根据会员编号，查询宝箱数量
     *
     * @param memberId 会员ID
     * @return
     */
    MemberBoxDTO getAwardCount(Long memberId);

    /**
     * 根据会员编号，查询中奖记录
     *
     * @param memberId    会员ID
     * @param promotionId 开宝箱活动ID
     * @return
     */
    List<AwardRecordDTO> getAwardRecordsList(Long memberId, Long promotionId);

    /**
     * 插入宝箱领奖人信息
     */
    Boolean insertBoxReceiver(BoxReceiverDTO boxReceiverDTO);

    /**
     * 开宝箱
     *
     * @param memberId
     * @return
     */
    AwardRecordDTO openBox(Long memberId);

    /**
     * 查询实物宝箱详情
     *
     * @param id 宝箱记录ID
     * @return
     */
    ResBoxJsonDTO getResBox(Long id);

    /**
     * 获奖信息弹幕
     *
     * @return
     */
    List<String> getAwardsInfo();

    /**
     * 获取活动宝箱
     *
     * @return
     */
    BoxActivityDTO getBoxActivity();

    /**
     * 获取当前开宝箱活动
     *
     * @return
     */
    PromotionDto getPromotion();
}
