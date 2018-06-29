package com.topaiebiz.giftcard.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.basic.api.ConfigApi;
import com.topaiebiz.card.constant.ApplyScopeEnum;
import com.topaiebiz.card.constant.CardOrderStatusEnum;
import com.topaiebiz.giftcard.BizConstants;
import com.topaiebiz.giftcard.dao.GiftcardOrderDao;
import com.topaiebiz.giftcard.dao.GiftcardOrderItemDao;
import com.topaiebiz.giftcard.entity.GiftcardBatch;
import com.topaiebiz.giftcard.entity.GiftcardOrder;
import com.topaiebiz.giftcard.entity.GiftcardOrderItem;
import com.topaiebiz.giftcard.enums.*;
import com.topaiebiz.giftcard.service.GiftcardBatchService;
import com.topaiebiz.giftcard.service.GiftcardOrderService;
import com.topaiebiz.giftcard.service.GiftcardUnitService;
import com.topaiebiz.giftcard.util.DateUtil;
import com.topaiebiz.giftcard.vo.*;
import com.topaiebiz.member.api.MemberApi;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.promotion.api.CardActivityApi;
import com.topaiebiz.promotion.dto.card.SecKillConsumeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 下午3:52 2018/1/12
 */
@Service
@Slf4j
public class GiftcardOrderServiceImpl extends ServiceImpl<GiftcardOrderDao, GiftcardOrder> implements GiftcardOrderService {

    @Autowired
    private GiftcardOrderDao giftcardOrderDao;
    @Autowired
    private GiftcardOrderItemDao giftcardOrderItemDao;
    @Autowired
    private GiftcardBatchService giftcardBatchService;
    @Autowired
    private GiftcardUnitService giftcardUnitService;
    @Autowired
    private CardActivityApi cardActivityApi;
    @Autowired
    private MemberApi memberApi;
    @Autowired
    MessageSender messageSender;
    @Autowired
    ConfigApi configApi;

    @Override
    public PageInfo<GiftcardOrderVO> queryOrders(GiftcardOrderReq giftcardOrderReq) {
        Page page = PageDataUtil.buildPageParam(giftcardOrderReq);
        List<GiftcardOrder> giftcardOrderList = giftcardOrderDao.queryOrders(page, giftcardOrderReq);
        if (!CollectionUtils.isEmpty(giftcardOrderList)) {
            List<GiftcardOrderVO> giftcardOrderVOList = new ArrayList<>(giftcardOrderReq.getPageSize());
            giftcardOrderList.forEach(giftcardOrder -> {
                GiftcardOrderVO giftcardOrderVO = new GiftcardOrderVO();
                BeanCopyUtil.copy(giftcardOrder, giftcardOrderVO);
                giftcardOrderVO.setOrderId(giftcardOrder.getId());
                //只有已付款，回显支付金额
                if (!giftcardOrder.getOrderStatus().equals(CardOrderStatusEnum.PAID.getStatusCode())) {
                    giftcardOrderVO.setPayAmount(BigDecimal.ZERO);
                }
                giftcardOrderVOList.add(giftcardOrderVO);
            });
            page.setRecords(giftcardOrderVOList);
        }
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public GiftcardOrderDetailVO getByOrderId(Long orderId) {
        GiftcardOrder order = giftcardOrderDao.queryOrderById(orderId);
        if (null == order) {
            throw new GlobalException(GiftcardExceptionEnum.ORDER_NOT_EXIST);
        }
        GiftcardOrderDetailVO giftcardOrderDetailVO = new GiftcardOrderDetailVO();
        BeanCopyUtil.copy(order, giftcardOrderDetailVO);
        giftcardOrderDetailVO.setOrderId(order.getId());
        giftcardOrderDetailVO.setPhone(order.getMemberPhone());
        giftcardOrderDetailVO.setPaySn(order.getPaySn());
        //已付款，回显支付金额
        if (order.getOrderStatus().equals(CardOrderStatusEnum.PAID.getStatusCode())) {
            giftcardOrderDetailVO.setPayAmount(order.getPayAmount());
            giftcardOrderDetailVO.setPhone(order.getMemberPhone());
            giftcardOrderDetailVO.setDiscountAmount(order.getOrderAmount().subtract(order.getPayAmount()));
        }
        return giftcardOrderDetailVO;
    }

    @Override
    public MyOrderVO prepareOrder(PlaceOrderVO placeOrderVO) {
        GiftcardBatch giftcardIssue = giftcardBatchService.selectById(placeOrderVO.getBatchId());
        if (null == giftcardIssue) {
            throw new GlobalException(GiftcardExceptionEnum.ISSUE_NOT_EXIST);
        }
        //生单规则判断
        if (placeOrderVO.getReqType().equals(BizConstants.ORDER_REQ_PLACE_ORDER)) {
            //未上架判断
            if (!giftcardIssue.getIssueStatus().equals(IssueStatusEnum.CARD_READY.getStatusId())) {
                throw new GlobalException(GiftcardExceptionEnum.BATCH_SOLD_OUT);
            }
            if (!giftcardIssue.getMedium().equals(CardMediumEnum.ELECT_CARD.getMediumId())) {
                throw new GlobalException(GiftcardExceptionEnum.ONLY_BUY_ELEC);
            }
            // 活动卡购买需要判断营销规则
            if (giftcardIssue.getCardAttr().equals(CardAttrEnum.ACTIVITY.getId())) {
                if (!cardActivityApi.checkSecKillRule(placeOrderVO.getBatchId(), placeOrderVO.getMemberId())) {
                    throw new GlobalException(GiftcardExceptionEnum.FOBBIDEN_ACTIVITY);
                }
            }
            //限购判断
            if (!giftcardIssue.getLimitNum().equals(0)) {
                int hadNum = giftcardUnitService.countByMemberAndBatch(placeOrderVO.getBatchId(), placeOrderVO.getMemberId());
                if (hadNum >= giftcardIssue.getLimitNum().intValue()
                        || placeOrderVO.getIssueNum() > giftcardIssue.getLimitNum()) {
                    throw new GlobalException(GiftcardExceptionEnum.LIMIT_TO_BUY);
                }
            } else {
                //已售数量判断
                if (placeOrderVO.getIssueNum() > (giftcardIssue.getIssueNum() - giftcardIssue.getOutNum())) {
                    throw new GlobalException(GiftcardExceptionEnum.LIMIT_TO_BUY);
                }
            }
            //下单数量判断
            if ((giftcardIssue.getOrderQty() + placeOrderVO.getIssueNum()) > giftcardIssue.getIssueNum()) {
                throw new GlobalException(GiftcardExceptionEnum.CARD_QTY_OUT);
            }
        }
        MyOrderVO myOrderVO = new MyOrderVO();
        myOrderVO.setCardName(giftcardIssue.getCardName());
        myOrderVO.setCardNum(placeOrderVO.getIssueNum());
        myOrderVO.setCover(giftcardIssue.getCover());
        myOrderVO.setDeadTime(DateUtil.renewalDays(new Date(), giftcardIssue.getValidDays()));
        myOrderVO.setFaceValue(giftcardIssue.getFaceValue());
        myOrderVO.setSalePrice(giftcardIssue.getSalePrice());
        myOrderVO.setScope(ApplyScopeEnum.getById(giftcardIssue.getApplyScope()).getScopeDesc());
        myOrderVO.setOrderAmount(giftcardIssue.getSalePrice().multiply(BigDecimal.valueOf(placeOrderVO.getIssueNum())));
        myOrderVO.setPayAmount(giftcardIssue.getSalePrice().multiply(BigDecimal.valueOf(placeOrderVO.getIssueNum())));
        myOrderVO.setLabelId(giftcardIssue.getLabelId());
        myOrderVO.setOrderKey(placeOrderVO.getOrderKey());
        //是否重复下单
        EntityWrapper<GiftcardOrder> wrapper = new EntityWrapper<>();
        wrapper.eq("orderKey", placeOrderVO.getOrderKey());
        GiftcardOrder giftcardOrder = selectOne(wrapper);
        if (null != giftcardOrder) {
            myOrderVO.setOrderId(giftcardOrder.getId());
            myOrderVO.setOrderAmount(giftcardOrder.getOrderAmount());
            myOrderVO.setPayAmount(giftcardOrder.getPayAmount());
        }
        return myOrderVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long placeOrder(PlaceOrderVO placeOrderVO) {
        MemberDto memberDto = memberApi.getMemberByMemberId(placeOrderVO.getMemberId());
        if (null == memberDto || memberDto.isAccountLock()) {
            throw new GlobalException(GiftcardExceptionEnum.MEMBER_LOCK);
        }
        //下单准备
        MyOrderVO myOrderVO = prepareOrder(placeOrderVO);
        //重复下单
        if (null != myOrderVO.getOrderId()) {
            return myOrderVO.getOrderId();
        }
        //下单数量
        GiftcardBatch batch = giftcardBatchService.selectById(placeOrderVO.getBatchId());
        EntityWrapper<GiftcardBatch> wrapper = new EntityWrapper<>();
        wrapper.eq("id", batch.getId());
        //有效订单限定购买张数
        GiftcardOrder lastOrder = new GiftcardOrder();
        lastOrder.setMemberId(placeOrderVO.getMemberId());
        lastOrder.setBatchId(placeOrderVO.getBatchId());
        Integer cardNum = giftcardOrderDao.queryValidOrderByMemberAndGiftcard(lastOrder);
        if (null != cardNum && (cardNum + placeOrderVO.getIssueNum()) > batch.getLimitNum()) {

            throw new GlobalException(GiftcardExceptionEnum.LIMIT_TO_BUY);
        }
        //乐观锁
        wrapper.eq("orderQty", batch.getOrderQty());
        GiftcardBatch updBatch = new GiftcardBatch();
        updBatch.setOrderQty(batch.getOrderQty() + placeOrderVO.getIssueNum());
        wrapper.ge("issueNum", updBatch.getOrderQty());
        if (!giftcardBatchService.update(updBatch, wrapper)) {
            throw new GlobalException(GiftcardExceptionEnum.SEC_KILL_MISS);
        }
        GiftcardOrder giftcardOrder = new GiftcardOrder();
        //暂不支持优惠，订单总额和支付金额一样
        giftcardOrder.setOrderAmount(myOrderVO.getOrderAmount());
        giftcardOrder.setPayAmount(myOrderVO.getOrderAmount());
        giftcardOrder.setMemberId(placeOrderVO.getMemberId());
        giftcardOrder.setMemberPhone(placeOrderVO.getMemberPhone());
        giftcardOrder.setMemberName(placeOrderVO.getMemberName());
        giftcardOrder.setCreatedTime(new Date());
        giftcardOrder.setOrderKey(placeOrderVO.getOrderKey());
        giftcardOrder.setOrderStatus(CardOrderStatusEnum.UNPAID.getStatusCode());
        giftcardOrder.setCreatedTime(new Date());

        if (giftcardOrderDao.insert(giftcardOrder) > 0) {
            //插入子项
            GiftcardOrderItem orderItem = new GiftcardOrderItem();
            orderItem.setOrderId(giftcardOrder.getId());
            orderItem.setCardName(myOrderVO.getCardName());
            orderItem.setCardNum(placeOrderVO.getIssueNum());
            orderItem.setCover(myOrderVO.getCover());
            orderItem.setFaceValue(myOrderVO.getFaceValue());
            orderItem.setSalePrice(myOrderVO.getSalePrice());
            orderItem.setLabelId(myOrderVO.getLabelId());
            orderItem.setBatchId(placeOrderVO.getBatchId());
            orderItem.setCreatedTime(new Date());
            giftcardOrderItemDao.insert(orderItem);
            //活动卡下单通知营销，默认活动卡只有一张
            if (batch.getCardAttr().equals(CardAttrEnum.ACTIVITY.getId())) {
                SecKillConsumeDTO consumeDTO = new SecKillConsumeDTO();
                consumeDTO.setOrderId(giftcardOrder.getId());
                consumeDTO.setMemberId(giftcardOrder.getMemberId());
                consumeDTO.setBatchId(batch.getId());
                if (!cardActivityApi.useSecKill(consumeDTO)) {
                    log.info("活动卡{}下单{}通知营销结果：false", batch.getId(), giftcardOrder.getId());
                    throw new GlobalException(GiftcardExceptionEnum.ACTIVITY_STORAGE_ERROR);
                }
            }
        }
        return giftcardOrder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public Boolean updateOrderAfterPay(GiftcardOrder giftcardOrder) {
        GiftcardOrder order = giftcardOrderDao.queryOrderById(giftcardOrder.getId());
        if (null == order) {
            throw new GlobalException(GiftcardExceptionEnum.ORDER_NOT_EXIST);
        }
        if (order.getOrderStatus().equals(CardOrderStatusEnum.PAID.getStatusCode())) {
            log.info("订单已支付:{}", giftcardOrder.toString());
            return true;
        }
        if (order.getPayAmount().compareTo(giftcardOrder.getPayAmount()) != 0) {
            throw new GlobalException(GiftcardExceptionEnum.PAY_ERROR_PRICE);
        }
        // 1.生产giftcardUnit,并绑定到用户，2.更新订单
        PlaceOrderVO placeOrderVO = new PlaceOrderVO();
        placeOrderVO.setIssueNum(order.getCardNum());
        placeOrderVO.setBatchId(order.getBatchId());
        placeOrderVO.setMemberId(order.getMemberId());
        placeOrderVO.setMemberName(order.getMemberName());
        List<String> cardNoList = giftcardBatchService.produceElecCardsForMember(placeOrderVO);
        if (!CollectionUtils.isEmpty(cardNoList)) {
            giftcardOrder.setOrderStatus(CardOrderStatusEnum.PAID.getStatusCode());
            giftcardOrder.setPayTime(new Date());
            giftcardOrder.setModifiedTime(new Date());
            if (updateById(giftcardOrder)) {
                //异步通知营销模块
                try {
                    MessageDTO messageDTO = new MessageDTO();
                    messageDTO.setMemberId(order.getMemberId());
                    messageDTO.setType(MessageTypeEnum.CARD_BUY);
                    messageDTO.getParams().put("payId", giftcardOrder.getPaySn());
                    messageDTO.getParams().put("payAmount", giftcardOrder.getPayAmount());
                    messageSender.publicMessage(messageDTO);
                } catch (Exception e) {
                    log.error("通知失败", e);
                }

                GiftcardOrderItem orderItem = new GiftcardOrderItem();
                orderItem.setCardNoList(listToStr(cardNoList));
                EntityWrapper<GiftcardOrderItem> wrapper = new EntityWrapper<>();
                //orderId batchId 唯一性
                wrapper.eq("orderId", giftcardOrder.getId());
                wrapper.eq("batchId", order.getBatchId());
                return giftcardOrderItemDao.update(orderItem, wrapper) > 0;
            }
        }
        return false;
    }

    @Override
    public Boolean updateOrderByMember(GiftcardOrder giftcardOrder) {
        EntityWrapper<GiftcardOrder> wrapper = new EntityWrapper<>();
        wrapper.eq("memberId", giftcardOrder.getMemberId());
        wrapper.eq("id", giftcardOrder.getId());
        wrapper.eq("delFlag", 0);
        GiftcardOrder order = selectOne(wrapper);
        if (null == order) {
            throw new GlobalException(GiftcardExceptionEnum.ORDER_NOT_YOURS);
        }
        //删除订单
        if (null != giftcardOrder.getDelFlag() && giftcardOrder.getDelFlag() == 1) {
            GiftcardOrder updOrder = new GiftcardOrder();
            updOrder.setId(giftcardOrder.getId());
            updOrder.setDelFlag(giftcardOrder.getDelFlag());
            updOrder.setModifiedTime(new Date());
            return updateById(updOrder);
        } else if (null != giftcardOrder.getOrderStatus()) {
            //未支付的订单才能取消
            if (order.getOrderStatus() != CardOrderStatusEnum.UNPAID.getStatusCode()) {
                throw new GlobalException(GiftcardExceptionEnum.UNPAID_CAN_CANCEL);
            }
            GiftcardOrderItem orderItem = new GiftcardOrderItem();
            orderItem.setOrderId(order.getId());
            orderItem = giftcardOrderItemDao.selectOne(orderItem);
            GiftcardBatch batch = giftcardBatchService.selectById(orderItem.getBatchId());
            EntityWrapper<GiftcardBatch> batchWrapper = new EntityWrapper<>();
            batchWrapper.eq("id", batch.getId());
            //乐观锁
            batchWrapper.eq("orderQty", batch.getOrderQty());
            GiftcardBatch updBatch = new GiftcardBatch();
            updBatch.setOrderQty(batch.getOrderQty() - orderItem.getCardNum());
            if (!giftcardBatchService.update(updBatch, batchWrapper)) {
                log.error("礼卡批次：{}归还下单数量：{} 失败", orderItem.getBatchId(), orderItem.getCardNum());
            }
            //活动卡订单取消通知营销模块
            if (batch.getCardAttr().equals(CardAttrEnum.ACTIVITY.getId())) {
                SecKillConsumeDTO consumeDTO = new SecKillConsumeDTO();
                consumeDTO.setBatchId(batch.getId());
                consumeDTO.setMemberId(order.getMemberId());
                consumeDTO.setOrderId(order.getId());
                log.info("活动卡{}订单{}取消通知营销模块结果：{}", orderItem.getBatchId(), orderItem.getOrderId(),
                        cardActivityApi.backSecKill(consumeDTO));
            }
            GiftcardOrder updOrder = new GiftcardOrder();
            updOrder.setId(giftcardOrder.getId());
            updOrder.setOrderStatus(giftcardOrder.getOrderStatus());
            updOrder.setModifiedTime(new Date());
            return giftcardOrderDao.updateById(updOrder) > 0;
        }
        return false;
    }

    @Override
    public MyOrderVO getMyOrderDetail(Long memberId, Long orderId) {
        GiftcardOrder giftcardOrder = giftcardOrderDao.queryOrderById(orderId);
        if (null == giftcardOrder || !giftcardOrder.getMemberId().equals(memberId)) {
            throw new GlobalException(GiftcardExceptionEnum.ORDER_NOT_YOURS);
        }
        GiftcardBatch giftcardIssue = giftcardBatchService.selectById(giftcardOrder.getBatchId());
        if (null == giftcardIssue) {
            throw new GlobalException(GiftcardExceptionEnum.ISSUE_NOT_EXIST);
        }
        MyOrderVO myOrderVO = new MyOrderVO();
        BeanCopyUtil.copy(giftcardOrder, myOrderVO);
        myOrderVO.setScope(ApplyScopeEnum.getById(giftcardIssue.getApplyScope()).getScopeDesc());
        //支付成功的失效时间按支付时间计算
        if (giftcardOrder.getOrderStatus().equals(CardOrderStatusEnum.PAID.getStatusCode())) {
            myOrderVO.setDeadTime(DateUtil.renewalDays(giftcardOrder.getPayTime(), giftcardIssue.getValidDays()));
        } else {
            myOrderVO.setDeadTime(DateUtil.renewalDays(new Date(), giftcardIssue.getValidDays()));
        }
        myOrderVO.setPayWay(PayCodeEnum.getByCode(giftcardOrder.getPayCode()).getCnName());
        myOrderVO.setOrderId(giftcardOrder.getId());
        return myOrderVO;
    }

    private String listToStr(List<String> stringList) {
        if (CollectionUtils.isEmpty(stringList)) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringList.forEach(s -> {
            stringBuffer.append(s).append(",");
        });
        return stringBuffer.toString();
    }

    @Override
    public BigDecimal getOrderAmount(Long orderId) {
        GiftcardOrder giftcardOrder = selectById(orderId);
        if (null == giftcardOrder) {
            throw new GlobalException(GiftcardExceptionEnum.ORDER_NOT_EXIST);
        }
        if (giftcardOrder.getOrderStatus().compareTo(CardOrderStatusEnum.UNPAID.getStatusCode()) != 0) {
            log.info("查询的订单非未支付状态，orderId:{}", orderId);
            return BigDecimal.ZERO;
        }
        return giftcardOrder.getPayAmount();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateBatchByWrapper(GiftcardOrder giftcardOrder, Wrapper wrapper) {
        //归还下单数量
        List<GiftcardOrder> giftcardOrderList = selectList(wrapper);
        if (CollectionUtils.isEmpty(giftcardOrderList)) {
            return 0;
        }
        giftcardOrderList.forEach(giftcardOrder1 -> {
            GiftcardOrderItem orderItem = new GiftcardOrderItem();
            orderItem.setOrderId(giftcardOrder1.getId());
            orderItem = giftcardOrderItemDao.selectOne(orderItem);
            GiftcardBatch batch = giftcardBatchService.selectById(orderItem.getBatchId());
            EntityWrapper<GiftcardBatch> batchWrapper = new EntityWrapper<>();
            batchWrapper.eq("id", batch.getId());
            //乐观锁
            batchWrapper.eq("orderQty", batch.getOrderQty());
            GiftcardBatch updBatch = new GiftcardBatch();
            updBatch.setOrderQty(batch.getOrderQty() - orderItem.getCardNum());
            if (!giftcardBatchService.update(updBatch, batchWrapper)) {
                log.error("礼卡批次：{}归还下单数量：{} 失败", orderItem.getBatchId(), orderItem.getCardNum());
            }
            //活动卡订单取消通知营销模块
            if (batch.getCardAttr().equals(CardAttrEnum.ACTIVITY.getId())) {
                SecKillConsumeDTO consumeDTO = new SecKillConsumeDTO();
                consumeDTO.setBatchId(batch.getId());
                consumeDTO.setMemberId(giftcardOrder1.getMemberId());
                consumeDTO.setOrderId(giftcardOrder1.getId());
                log.info("活动卡{}订单{}取消通知营销模块结果：{}", orderItem.getBatchId(), orderItem.getOrderId(),
                        cardActivityApi.backSecKill(consumeDTO));
            }
        });
        return giftcardOrderDao.update(giftcardOrder, wrapper);
    }

    @Override
    public List<GiftcardOrder> queryOrdersForExport(GiftcardOrderReq giftcardOrderReq) {
        giftcardOrderReq.setStart(giftcardOrderReq.getStart() - 1);
        return giftcardOrderDao.queryOrdersForExport(giftcardOrderReq);
    }

}
