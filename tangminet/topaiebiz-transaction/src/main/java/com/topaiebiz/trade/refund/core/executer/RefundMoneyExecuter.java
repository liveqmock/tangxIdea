package com.topaiebiz.trade.refund.core.executer;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.topaiebiz.card.dto.PayCard;
import com.topaiebiz.card.dto.RefundOrderDTO;
import com.topaiebiz.member.constants.AssetOperateType;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.member.dto.point.AssetChangeDto;
import com.topaiebiz.pay.dto.refund.RefundParamDTO;
import com.topaiebiz.trade.dto.pay.GoodPayDTO;
import com.topaiebiz.trade.order.util.CardRefundUtil;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.trade.refund.core.context.RefundParamsContext;
import com.topaiebiz.trade.refund.dao.RefundOrderLogDao;
import com.topaiebiz.trade.refund.entity.RefundOrderDetailEntity;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.trade.refund.entity.RefundOrderLogEntity;
import com.topaiebiz.trade.refund.facade.GiftCardServiceFacade;
import com.topaiebiz.trade.refund.facade.MemberServiceFacade;
import com.topaiebiz.trade.refund.facade.PointServiceFacade;
import com.topaiebiz.trade.refund.facade.RefundServiceFacade;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description 退款处理者
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/2 10:20
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class RefundMoneyExecuter extends AbstractRefundExecuter {

    @Autowired
    private MemberServiceFacade memberServiceFacade;

    @Autowired
    private GiftCardServiceFacade giftCardServiceFacade;

    @Autowired
    private PointServiceFacade pointServiceFacade;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private RefundServiceFacade refundServiceFacade;

    @Autowired
    private RefundOrderLogDao refundOrderLogDao;

    @Override
    public boolean execute(RefundParamsContext refundParamsContext) {
        RefundOrderEntity refundOrderEntity = refundParamsContext.getRefundOrderEntity();
        OrderEntity orderEntity = refundParamsContext.getOrderEntity();
        MemberDto memberDto = memberServiceFacade.getMember(refundOrderEntity.getMemberId());
        Long refundOrderId = refundOrderEntity.getId();
        Date currentDate = new Date();
        boolean isAllRefundSuccess = true;

        // 退款日志
        RefundOrderLogEntity refundOrderLogEntity = refundOrderLogHelper.findByRefundId(refundOrderId);
        if (null == refundOrderLogEntity) {
            refundOrderLogEntity = refundOrderLogHelper.insertLog(refundOrderEntity);
            if (null == refundOrderLogEntity) {
                log.error(">>>>>>>>>>refund:{} have not refundLog info！", refundOrderId);
                return false;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();

        //1：退还积分 和 余额
        try {
            if (MathUtil.greator(refundOrderLogEntity.getRefundAssetPrice(), BigDecimal.ZERO) && !Constants.Refund.REFUND_YES.equals(refundOrderLogEntity.getRefundAssetResult())) {
                if (refundAsset(refundOrderEntity, memberDto)) {
                    refundOrderLogEntity.setRefundAssetResult(Constants.Refund.REFUND_YES);
                    refundOrderLogEntity.setRefundAssetTime(currentDate);
                } else {
                    isAllRefundSuccess = false;
                    log.error(">>>>>>>>>>refund member asset fail, refundOrder id:{}", refundOrderId);
                }
            }
        } catch (Exception e) {
            isAllRefundSuccess = false;
            stringBuilder.append("refund member asset error, msg:").append(e.getMessage()).append("\t\n");
        }

        //2：退还第三方支付现金
        try {
            if (MathUtil.greator(refundOrderLogEntity.getRefundAmounts(), BigDecimal.ZERO) && !Constants.Refund.REFUND_YES.equals(refundOrderLogEntity.getRefundAmountsResult())) {
                String callBackNo = refundAmounts(refundOrderEntity);
                if (StringUtils.isNotBlank(callBackNo)) {
                    refundOrderLogEntity.setRefundAmountsResult(Constants.Refund.REFUND_YES);
                    refundOrderLogEntity.setRefundAmountsTime(currentDate);
                    refundOrderEntity.setCallBackNo(callBackNo);
                } else {
                    isAllRefundSuccess = false;
                    log.error(">>>>>>>>>>refund third money fail, refundOrder id:{}", refundOrderId);
                }
            }
        } catch (Exception e) {
            isAllRefundSuccess = false;
            stringBuilder.append("refund third money error, msg:").append(e.getMessage()).append("\t\n");
        }

        //3：退还美礼卡
        try {
            if (MathUtil.greator(refundOrderLogEntity.getRefundCardPrice(), BigDecimal.ZERO) && !Constants.Refund.REFUND_YES.equals(refundOrderLogEntity.getRefundCardResult())) {
                if (refundCardPrice(refundOrderEntity, orderEntity, memberDto)) {
                    refundOrderLogEntity.setRefundCardResult(Constants.Refund.REFUND_YES);
                    refundOrderLogEntity.setRefundCardTime(currentDate);
                } else {
                    isAllRefundSuccess = false;
                    log.error(">>>>>>>>>>refund card price fail, refundOrder id:{}", refundOrderId);
                }
            }
        } catch (Exception e) {
            isAllRefundSuccess = false;
            stringBuilder.append("refund third money error, msg:").append(e.getMessage()).append("\t\n");
        }
        refundOrderLogEntity.setRefundErrorMsg(stringBuilder.toString());


        // 更新退款日志
        if (isAllRefundSuccess) {
            refundOrderLogEntity.setRefundState(Constants.Refund.REFUND_ALL);
        } else {
            if (refundOrderLogEntity.hasRefundSuccess()) {
                refundOrderLogEntity.setRefundState(Constants.Refund.REFUND_PART);
            }
        }
        refundOrderLogEntity.setLastModifiedTime(currentDate);
        refundOrderLogEntity.setLastModifierId(memberDto.getId());
        refundOrderLogDao.updateById(refundOrderLogEntity);

        // 全部退款成功，再发退款成功通知
        if (isAllRefundSuccess) {
            refundOrderLogEntity.setRefundState(Constants.Refund.REFUND_ALL);

            //发起退款成功的通知
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setType(MessageTypeEnum.ORDER_REFUND);
            messageDTO.getParams().put("storeId_orderId", StringUtils.join(orderEntity.getStoreId(), "_", orderEntity.getId()));
            messageSender.publicMessage(messageDTO);
            return true;
        }
        return false;
    }

    /**
     * Description: 调第三方支付退款接口
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/2
     *
     * @param:
     **/
    private String doRefund(Long refundOrderId, Long orderId, BigDecimal refundPrice, String refundReason) {
        RefundParamDTO refundParamDTO = new RefundParamDTO();
        OrderEntity orderEntity = super.ordersQueryUtil.queryOrder(orderId);
        OrderPayEntity orderPayEntity = orderPayDao.selectById(orderEntity.getPayId());
        refundParamDTO.setPayId(orderEntity.getPayId().toString());
        refundParamDTO.setRefundPrice(refundPrice);
        refundParamDTO.setOrderType(Constants.Order.ORDER_TYPE_GOOD);
        refundParamDTO.setPayPrice(orderPayEntity.getThirdAmount());
        refundParamDTO.setPayType(orderEntity.getPayType());
        refundParamDTO.setRefundOrderId(refundOrderId.toString());
        refundParamDTO.setRefundReason(refundReason);

        return refundServiceFacade.refund(refundParamDTO);
    }


    /**
     * Description: 退用户资产
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/3
     *
     * @param:
     **/
    private boolean refundAsset(RefundOrderEntity refundOrderEntity, MemberDto memberDto) {
        log.info("----------send integral and balance refund request!");
        AssetChangeDto assetChangeDto = new AssetChangeDto();
        assetChangeDto.setMemberId(memberDto.getId());
        assetChangeDto.setUserName(memberDto.getUserName());
        assetChangeDto.setTelephone(memberDto.getTelephone());
        assetChangeDto.setOperateSn(refundOrderEntity.getId().toString());
        assetChangeDto.setPoint(refundOrderEntity.getRefundIntegralPrice().multiply(Constants.Order.INTEGRAL_RATE).intValue());
        assetChangeDto.setBalance(refundOrderEntity.getRefundBalance());
        assetChangeDto.setOperateType(AssetOperateType.REFUND);
        return pointServiceFacade.rollbackAccountAssets(assetChangeDto);
    }

    /**
     * Description: 退第三方现金
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/3
     *
     * @param:
     **/
    private String refundAmounts(RefundOrderEntity refundOrderEntity) {
        log.info("----------send thirdAmount refund request!");
        return this.doRefund(refundOrderEntity.getId(), refundOrderEntity.getOrderId(), refundOrderEntity.getRefundThirdAmount(), refundOrderEntity.getRefundReason());
    }


    /**
     * Description: 退美礼卡
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/3
     *
     * @param:
     **/
    private boolean refundCardPrice(RefundOrderEntity refundOrderEntity, OrderEntity orderEntity, MemberDto memberDto) {
        log.info("----------send giftCard refund request!");
        BigDecimal giftCardPrice = refundOrderEntity.getRefundCardPrice();
        RefundOrderDTO refundOrderDTO = new RefundOrderDTO();
        refundOrderDTO.setMemberId(memberDto.getId());
        refundOrderDTO.setMemberName(memberDto.getUserName());
        refundOrderDTO.setMemberPhone(memberDto.getTelephone());
        refundOrderDTO.setPayPwd(memberDto.getPayPassword());
        refundOrderDTO.setTotalAmount(giftCardPrice);
        refundOrderDTO.setPaySn(orderEntity.getPayId().toString());
        refundOrderDTO.setOrderNo(refundOrderEntity.getId().toString());

        // 美礼卡支付明细
        List<PayCard> payCards = new ArrayList<>();

        // 获取订单明细
        List<Long> orderDetailIds = super.refundQueryUtil.queryDetailsByRefundId(refundOrderEntity.getId()).stream().map(RefundOrderDetailEntity::getOrderDetailId).collect(Collectors.toList());
        List<OrderDetailEntity> orderDetailEntities = super.ordersQueryUtil.queryDetailsByDetailsIds(orderDetailIds);

        for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
            // 商品支付明细
            GoodPayDTO goodPayDTO = JSON.parseObject(orderDetailEntity.getPayDetail(), GoodPayDTO.class);
            Map<String, BigDecimal> cardDetailMap = goodPayDTO.getCardDetail();
            if (MapUtils.isEmpty(cardDetailMap)) {
                continue;
            }
            for (Map.Entry<String, BigDecimal> cardDetail : cardDetailMap.entrySet()) {
                BigDecimal cardAmount = cardDetail.getValue();
                if (null == cardAmount || MathUtil.sameValue(giftCardPrice, BigDecimal.ZERO)) {
                    continue;
                }
                cardAmount = MathUtil.greator(giftCardPrice, cardAmount) ? cardAmount : giftCardPrice;
                giftCardPrice = giftCardPrice.subtract(cardAmount);

                PayCard payCard = new PayCard();
                payCard.setCardNo(cardDetail.getKey());
                payCard.setAmount(cardAmount);
                payCard.setGoodsId(orderDetailEntity.getSkuId());
                payCard.setGoodsName(orderDetailEntity.getName());
                payCard.setStoreId(refundOrderEntity.getStoreId());
                payCard.setStoreName(refundOrderEntity.getStoreName());
                payCards.add(payCard);
            }
        }

        //补充礼卡的运费支付信息
        if (OrderStatusEnum.PENDING_DELIVERY.getCode().equals(orderEntity.getOrderState()) && MathUtil.greator(giftCardPrice, BigDecimal.ZERO)) {
            List<PayCard> freightCards = CardRefundUtil.buildFreightCards(orderEntity);
            if (CollectionUtils.isNotEmpty(freightCards)) {
                payCards.addAll(freightCards);
            }
        }
        refundOrderDTO.setPayCardList(payCards);
        return giftCardServiceFacade.refund(refundOrderDTO);
    }
}
