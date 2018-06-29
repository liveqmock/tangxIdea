package com.topaiebiz.giftcard.service;

import com.baomidou.mybatisplus.service.IService;
import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.card.dto.PayInfoDTO;
import com.topaiebiz.card.dto.RefundOrderDTO;
import com.topaiebiz.giftcard.entity.GiftcardUnit;
import com.topaiebiz.giftcard.vo.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @description: 礼卡实物服务
 * @author: Jeff Chen
 * @date: created in 下午3:04 2018/1/12
 */
public interface GiftcardUnitService extends IService<GiftcardUnit> {

    /**
     * 导出指定批次卡密
     *
     * @param batchId
     * @return
     */
    List<GiftcardExportVO> export(Long batchId);

    /**
     * 分页查询
     *
     * @param giftcardEntityReq
     * @return
     */
    PageInfo<GiftcardUnitVO> queryGiftcard(GiftcardUnitReq giftcardEntityReq);

    /**
     * 续期
     *
     * @param unitHandleReq
     * @return
     */
    Boolean renewal(UnitHandleReq unitHandleReq);

    /**
     * 查询卡片详情
     *
     * @param unitId
     * @return
     */
    GiftcardUnitVO getGiftcardInfoById(Long unitId);

    /**
     * 批量激活
     *
     * @param ids
     * @return
     */
    Boolean active(List<String> ids, String modifier);

    /**
     * 支持批量冻结
     *
     * @param ids
     * @param modifier
     * @return
     */
    Boolean freeze(List<Long> ids, String modifier);

    /**
     * 解冻
     *
     * @param unitId
     * @param modifier
     * @return
     */
    @Deprecated
    Boolean unfreeze(Long unitId, String modifier);

    /**
     *解冻
     * @param unitId
     * @param modifier
     * @param note
     * @return
     */
    Boolean unfreeze(Long unitId, String modifier,String note);

    /**
     * 批量激活
     * @param cardOpReq
     * @return
     */
    Boolean batchActive(CardOpReq cardOpReq);

    /**
     * 批量冻结
     * @param cardOpReq
     * @return
     */
    Boolean batchFreeze(CardOpReq cardOpReq);

    /**
     * 查询用户已绑定卡列表
     *
     * @param memberId
     * @return
     */
    List<GiftcardUnit> selectMemberBoundCards(Long memberId);

    /**
     * 用卡支付
     *
     * @param payInfoDTO
     * @return
     */
    Boolean payByCards(PayInfoDTO payInfoDTO);

    /**
     * 卡退款
     *
     * @param refundOrderDTO
     * @return
     */
    Boolean refundCards(RefundOrderDTO refundOrderDTO);

    /**
     * 根据卡号查询
     * @param cardNo
     * @param memberId
     * @return
     */
    GiftcardUnit selectByCardNo(String cardNo,Long memberId);

    /**
     * @param memberId
     * @return
     */
    BigDecimal totalBalance(Long memberId);

    /**
     * 查询我绑定的卡片列表：可用、不可用
     *
     * @param memberId
     * @return
     */
    MyGiftcardListVO selectMyGiftcardList(Long memberId);

    /**
     * 绑定
     *
     * @param cardBindVO
     * @return
     */
    Boolean bindCard(CardBindVO cardBindVO);

    /**
     * 分页查询我的礼卡
     * @param myGiftcardReq
     * @return
     */
    PageInfo<MyGiftcardVO> getMyGiftcardBycategory(MyGiftcardReq myGiftcardReq);

    /**
     * 查询用户购买指定礼卡批次的数量
     * @param batchId
     * @param owner
     * @return
     */
    Integer countByMemberAndBatch(Long batchId, Long owner);

    /**
     * 变更已用完状态和已过期状态
     * @return
     */
    Integer updGiftcardUnitStatus();

    /**
     * 批量查询用户的礼卡余额
     * @param memberIdList
     * @return
     */
    Map<String, Object> getBalanceByMemberIds(List<Long> memberIdList,Integer type);
}
