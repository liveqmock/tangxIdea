package com.topaiebiz.guider.utils;

import com.topaiebiz.guider.bo.OrderCompleteAchievementBo;
import com.topaiebiz.guider.bo.PayCompleteAchievementBo;
import com.topaiebiz.guider.bo.PayOrderBo;
import com.topaiebiz.guider.constants.GuiderContants;
import com.topaiebiz.guider.entity.GuiderTaskNewUserEntity;
import com.topaiebiz.guider.entity.GuiderTaskOrderEntity;
import com.topaiebiz.guider.entity.GuiderTaskPayEntity;
import com.topaiebiz.trade.constants.OrderStatusEnum;
import com.topaiebiz.trade.dto.order.GuiderOrderDetailDTO;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by ward on 2018-06-04.
 */
public class AchievementUtil {

    public static GuiderTaskOrderEntity packageTaskOrder(GuiderOrderDetailDTO orderDetailDTO, BigDecimal awardRate,
                                                         GuiderTaskNewUserEntity guiderTaskNewUse) {

        GuiderTaskOrderEntity param = new GuiderTaskOrderEntity();
        Long memberId = guiderTaskNewUse.getMemberId();
        param.setOrderId(orderDetailDTO.getId());
        param.setOrderStatus(orderDetailDTO.getOrderState());

        param.setTaskId(guiderTaskNewUse.getTaskId());
        param.setPayId(orderDetailDTO.getPayId());
        param.setPayMoney(orderDetailDTO.getPayPrice());
        param.setPayTime(orderDetailDTO.getPayTime());
        param.setRefundMoney(orderDetailDTO.getRefundPrice());
        param.setFreightMoney(orderDetailDTO.getActualFreight());
        param.setAwardMoney(calcAwardMoney(awardRate, orderDetailDTO));
        param.setAwardRate(awardRate);
        param.setAwardType(1);

        param.setMemberId(memberId);
        param.setSrcMemberId(guiderTaskNewUse.getSrcMemberId());
        param.setCreatorId(memberId);
        param.setCreatedTime(new Date());
        return param;
    }


    public static OrderCompleteAchievementBo packageOrderCompleteAchievement(GuiderTaskOrderEntity taskOrderEntity, GuiderOrderDetailDTO orderDetailDTO) {
        OrderCompleteAchievementBo param = new OrderCompleteAchievementBo();

        param.setTaskId(taskOrderEntity.getTaskId());
        param.setOrderId(orderDetailDTO.getId());
        param.setMemberId(orderDetailDTO.getMemberId());
        param.setPayMoney(orderDetailDTO.getPayPrice());
        param.setFreightMoney(orderDetailDTO.getActualFreight());
        param.setRefundMoney(orderDetailDTO.getRefundPrice());
        BigDecimal awardBaseMoney = calcAwardBaseMoney(orderDetailDTO);
        param.setAwardBaseMoney(awardBaseMoney);
        param.setAwardMoney(calcAwardMoney(taskOrderEntity.getAwardRate(), awardBaseMoney));
        return new OrderCompleteAchievementBo();
    }


    public static BigDecimal calcAwardMoney(BigDecimal awardRate, GuiderOrderDetailDTO orderDetailDTO) {
        BigDecimal awardBaseMoney = calcAwardBaseMoney(orderDetailDTO);
        return awardBaseMoney.multiply(awardRate);
    }

    public static BigDecimal calcAwardMoney(BigDecimal awardRate, BigDecimal awardBaseMoney) {
        return awardBaseMoney.multiply(awardRate);
    }


    public static BigDecimal calcAwardBaseMoney(GuiderOrderDetailDTO orderDetailDTO) {
        BigDecimal payMoney = orderDetailDTO.getPayPrice();
        BigDecimal refundMoney = orderDetailDTO.getRefundPrice();
        BigDecimal freightMoney = orderDetailDTO.getActualFreight();
        return payMoney.subtract(refundMoney).subtract(freightMoney);
    }

    public static BigDecimal calcAwardBaseMoney(GuiderTaskOrderEntity taskOrderEntity) {
        BigDecimal payMoney = taskOrderEntity.getPayMoney();
        BigDecimal refundMoney = taskOrderEntity.getRefundMoney();
        BigDecimal freightMoney = taskOrderEntity.getFreightMoney();
        return payMoney.subtract(refundMoney).subtract(freightMoney);
    }


    public static List<Long> extractUncompletePayIds(List<GuiderTaskOrderEntity> taskOrderEntityList) {
        HashSet<Long> payIds = new HashSet<>();
        for (GuiderTaskOrderEntity taskOrder : taskOrderEntityList) {
            if (0 == taskOrder.getPayStatus()) {
                payIds.add(taskOrder.getPayId());
            }
        }
        List<Long> payIdList = new ArrayList<>();
        payIdList.addAll(payIds);
        return payIdList;
    }

    public static String getAggregateKey(GuiderTaskOrderEntity taskOrder) {
        return taskOrder.getTaskId() + "_" + taskOrder.getPayId();
    }

    public static String getAggregateKey(Long taskId, Long payId) {
        return taskId + "_" + payId;
    }

    /**
     * @param soldierOrderStatus
     * @param orderStatus
     * @return
     * @desc 待到账：支付单下的订单列表中至少有一单是处于非最终状态
     * 已失效：支付单下的订单列表中所有订单均是“已关闭”
     * 已到账：支付单下的订单列表中至少有一单是“已完成”其余订单是“已关闭”
     */
    private static Integer gainGuiderFlagOrderStatus(Integer soldierOrderStatus, Integer orderStatus) {
        if (null == soldierOrderStatus) {
            return orderStatus;
        }
        if (OrderStatusEnum.ORDER_CLOSE.getCode().equals(soldierOrderStatus)) {
            if (OrderStatusEnum.ORDER_CLOSE.getCode().equals(orderStatus)) {
                return OrderStatusEnum.ORDER_CLOSE.getCode();
            } else if (OrderStatusEnum.ORDER_COMPLETION.getCode().equals(orderStatus)) {
                return OrderStatusEnum.ORDER_COMPLETION.getCode();
            } else {
                return orderStatus;
            }
        } else if (OrderStatusEnum.ORDER_COMPLETION.getCode().equals(soldierOrderStatus)) {
            if (OrderStatusEnum.ORDER_CLOSE.getCode().equals(orderStatus)) {
                return OrderStatusEnum.ORDER_COMPLETION.getCode();
            } else if (OrderStatusEnum.ORDER_COMPLETION.getCode().equals(orderStatus)) {
                return OrderStatusEnum.ORDER_COMPLETION.getCode();
            } else {
                return orderStatus;
            }
        } else {
            return soldierOrderStatus;
        }
    }

    private static Integer gainGuiderPayStatus(Integer soldierOrderStatus, Integer orderStatus) {
        Integer flagOrderStatus = gainGuiderFlagOrderStatus(soldierOrderStatus, orderStatus);
        if (OrderStatusEnum.ORDER_CLOSE.getCode().equals(flagOrderStatus)) {
            return GuiderContants.GuiderPayStatus.CLOSE;
        } else if (OrderStatusEnum.ORDER_COMPLETION.getCode().equals(flagOrderStatus)) {
            return GuiderContants.GuiderPayStatus.COMPLETE;
        } else {
            return GuiderContants.GuiderPayStatus.WAITING;
        }
    }

    public static HashMap<String, PayOrderBo> aggregateTaskPayMap(Long currentOrderId, List<GuiderTaskOrderEntity> taskOrderEntityList,
                                                                  List<GuiderTaskPayEntity> taskPayEntityList) {

        HashMap<String, GuiderTaskPayEntity> taskPayEntityMap = new HashMap<>();
        for (GuiderTaskPayEntity taskPayEntity : taskPayEntityList) {
            String aggregateKey = getAggregateKey(taskPayEntity.getTaskId(), taskPayEntity.getPayId());
            taskPayEntityMap.put(aggregateKey, taskPayEntity);
        }

        HashMap<String, PayOrderBo> aggregateMap = new HashMap<>();
        for (GuiderTaskOrderEntity taskOrder : taskOrderEntityList) {
            Long taskId = taskOrder.getTaskId();
            Long payId = taskOrder.getPayId();
            String aggregateKey = getAggregateKey(taskId, payId);
            PayOrderBo payOrderBo = aggregateMap.get(aggregateKey);
            if (null == payOrderBo) {
                payOrderBo = new PayOrderBo();
            }
            payOrderBo.setPayId(payId);
            payOrderBo.setTaskId(taskId);
            payOrderBo.setCurrentTaskPay(taskPayEntityMap.get(aggregateKey));
            //设置 当前的taskOrder
            if (currentOrderId.equals(taskOrder.getOrderId())) {
                payOrderBo.setCurrentTaskOrder(taskOrder);
                aggregateMap.put(aggregateKey, payOrderBo);
                continue;
            }
            Integer currentOrderStatus = taskOrder.getOrderStatus();
            payOrderBo.setSoldierOrderStatus(gainGuiderFlagOrderStatus(payOrderBo.getSoldierOrderStatus(), currentOrderStatus));
            List<GuiderTaskOrderEntity> tempList = payOrderBo.getOtherTaskOrders();
            if (null == tempList) {
                tempList = new ArrayList<>();
                tempList.add(taskOrder);
                payOrderBo.setOtherTaskOrders(tempList);
            }
            aggregateMap.put(aggregateKey, payOrderBo);
        }
        return aggregateMap;
    }

    public static Boolean checkQualification(GuiderTaskNewUserEntity taskNewUserEntity) {
        if (null == taskNewUserEntity || null == taskNewUserEntity.getSrcMemberId()
                || taskNewUserEntity.getSrcMemberId() < 1) {
            return false;
        }
        if (null == taskNewUserEntity.getIsCompleteTask() || !taskNewUserEntity.getIsCompleteTask()) {
            return false;
        }
        Integer prizeDuration = taskNewUserEntity.getPrizeDuration();
        Date completeTime = taskNewUserEntity.getCompleteTime();
        if (null == prizeDuration || null == completeTime) {
            return false;
        }
        Long limitTime = completeTime.getTime() + prizeDuration * 1000;
        Long nowTime = System.currentTimeMillis();
        if (nowTime <= limitTime && nowTime > completeTime.getTime()) {
            return true;
        } else {
            return false;
        }

    }

    public static PayCompleteAchievementBo calcPayCompleteAchievement(OrderStatusEnum currentOrderStatus, GuiderTaskOrderEntity currentTaskOrder,
                                                                      List<GuiderTaskOrderEntity> otherTaskOrderEntityList) {

        PayCompleteAchievementBo param = new PayCompleteAchievementBo();
        BigDecimal awardRate = currentTaskOrder.getAwardRate();
        BigDecimal awardMoney;
        BigDecimal awardBaseMoney;
        BigDecimal payTotalMoney = BigDecimal.ZERO;
        BigDecimal refundTotalMoney = BigDecimal.ZERO;
        BigDecimal freightTotalMoney = BigDecimal.ZERO;

        if (OrderStatusEnum.ORDER_COMPLETION.equals(currentOrderStatus)) {
            payTotalMoney = payTotalMoney.add(currentTaskOrder.getPayMoney());
            refundTotalMoney = refundTotalMoney.add(currentTaskOrder.getRefundMoney());
            freightTotalMoney = freightTotalMoney.add(currentTaskOrder.getFreightMoney());
        }
        for (GuiderTaskOrderEntity taskOrderEntity : otherTaskOrderEntityList) {
            if (OrderStatusEnum.ORDER_COMPLETION.getCode().equals(taskOrderEntity.getOrderStatus())) {
                payTotalMoney = payTotalMoney.add(taskOrderEntity.getPayMoney());
                refundTotalMoney = refundTotalMoney.add(taskOrderEntity.getRefundMoney());
                freightTotalMoney = freightTotalMoney.add(taskOrderEntity.getFreightMoney());
            }
        }
        awardBaseMoney = payTotalMoney.subtract(refundTotalMoney).subtract(freightTotalMoney);
        awardMoney = awardBaseMoney.multiply(awardRate);

        param.setMemberId(currentTaskOrder.getMemberId());
        param.setTaskId(currentTaskOrder.getTaskId());
        param.setPayId(currentTaskOrder.getPayId());
        param.setSrcMemberId(currentTaskOrder.getSrcMemberId());
        param.setAwardBaseMoney(awardBaseMoney);
        param.setAwardMoney(awardMoney);
        param.setPayMoney(payTotalMoney);
        param.setRefundMoney(refundTotalMoney);
        param.setFreightMoney(freightTotalMoney);
        param.setAwardRate(awardRate);
        return param;
    }
}
