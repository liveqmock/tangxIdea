package com.topaiebiz.trade.refund.helper;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.refund.dao.RefundOrderDao;
import com.topaiebiz.trade.refund.dao.RefundOrderDetailDao;
import com.topaiebiz.trade.refund.entity.RefundOrderDetailEntity;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.trade.refund.enumdata.RefundOrderStateEnum;
import com.topaiebiz.trade.refund.exception.RefundOrderExceptionEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description 售后订单查询工具类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/3 18:42
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class RefundQueryUtil {

    @Autowired
    private RefundOrderDao refundOrderDao;

    @Autowired
    private RefundOrderDetailDao refundOrderDetailDao;


    public Map<Long, BigDecimal> queryRefundAmountGroupByOrderId(Collection<Long> orderIds) {
        EntityWrapper<RefundOrderEntity> wrapper = new EntityWrapper<>();
        wrapper.in("orderId", orderIds);
        wrapper.eq("refundState", RefundOrderStateEnum.REFUNDED.getCode());
        wrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

        List<RefundOrderEntity> refundOrderEntities = refundOrderDao.selectList(wrapper);
        Map<Long, BigDecimal> resultMap = new HashMap<>();

        for (RefundOrderEntity refundOrderEntity : refundOrderEntities) {
            BigDecimal refundAmount = resultMap.get(refundOrderEntity.getOrderId());
            if (null == refundAmount) {
                refundAmount = BigDecimal.ZERO;
            }
            refundAmount = refundAmount.add(refundOrderEntity.getRefundPrice());
            resultMap.put(refundOrderEntity.getOrderId(), refundAmount);
        }
        return resultMap;
    }


    /**
     * Description: 查询 申请仅退款的 的售后订单 （非整单退）
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/3
     *
     * @param:
     **/
    public List<RefundOrderEntity> queryPendingAuditRefunds() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -Constants.Refund.REFUND_AUTO_AUDIT_DAYS);

        EntityWrapper<RefundOrderEntity> refundWrapper = new EntityWrapper<>();
        refundWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        refundWrapper.eq("refundState", RefundOrderStateEnum.APPLY_FOR_REFUND.getCode());
        refundWrapper.eq("refundRange", OrderConstants.OrderRefundStatus.ALL_REFUND_NO);
        refundWrapper.eq("pfInvolved", Constants.Refund.PLATFORM_IS_NOT_INVOLVED);
        refundWrapper.lt("refundTime", calendar.getTime());
        refundWrapper.last("limit 100");
        return refundOrderDao.selectList(refundWrapper);
    }


    /**
     * Description: 查询等待审核的 未发货整单退售后订单 （整单退）
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/2
     *
     * @param:
     **/
    public List<RefundOrderEntity> queryPendingAuditAllRefund() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -Constants.Refund.ALL_REFUND_AUTO_AUDIT_DAYS);

        EntityWrapper<RefundOrderEntity> refundWrapper = new EntityWrapper<>();
        refundWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        refundWrapper.eq("refundState", RefundOrderStateEnum.APPLY_FOR_REFUND.getCode());
        refundWrapper.eq("refundRange", OrderConstants.OrderRefundStatus.ALL_REFUND_YES);
        refundWrapper.eq("pfInvolved", Constants.Refund.PLATFORM_IS_NOT_INVOLVED);
        refundWrapper.lt("refundTime", calendar.getTime());
        refundWrapper.last("limit 100");
        return refundOrderDao.selectList(refundWrapper);
    }

    /**
     * Description: 查询 申请退货退款的 的售后订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/3
     *
     * @param:
     **/
    public List<RefundOrderEntity> queryPendingAuditReturns() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -Constants.Refund.REFUND_AUTO_AUDIT_DAYS);

        EntityWrapper<RefundOrderEntity> refundWrapper = new EntityWrapper<>();
        refundWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        refundWrapper.eq("refundState", RefundOrderStateEnum.APPLY_FOR_RETURNS.getCode());
        refundWrapper.eq("pfInvolved", Constants.Refund.PLATFORM_IS_NOT_INVOLVED);
        refundWrapper.lt("refundTime", calendar.getTime());
        refundWrapper.last("limit 100");
        return refundOrderDao.selectList(refundWrapper);
    }

    /**
     * Description: 查询待寄回商品
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/3
     *
     * @param:
     **/
    public List<RefundOrderEntity> queryPendingShipping() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -Constants.Refund.WAIT_GOODS_RETURN_MAX_DAYS);

        EntityWrapper<RefundOrderEntity> refundWrapper = new EntityWrapper<>();
        refundWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        refundWrapper.eq("refundState", RefundOrderStateEnum.WAITING_FOR_RETURN.getCode());
        refundWrapper.eq("pfInvolved", Constants.Refund.PLATFORM_IS_NOT_INVOLVED);
        refundWrapper.lt("auditTime", calendar.getTime());
        refundWrapper.last("limit 100");
        return refundOrderDao.selectList(refundWrapper);
    }


    /**
     * Description: 查询被拒绝未继续处理的售后订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/5
     *
     * @param:
     **/
    public List<RefundOrderEntity> queryRejectedRefund() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -Constants.Refund.REJECTED_AND_DO_NOT_DEAL_DAYS);

        EntityWrapper<RefundOrderEntity> refundWrapper = new EntityWrapper<>();
        refundWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        // 退款已拒绝 / 退货已拒绝
        refundWrapper.gt("refundState", RefundOrderStateEnum.REFUNDED.getCode());
        refundWrapper.lt("refundState", RefundOrderStateEnum.CLOSE.getCode());
        refundWrapper.eq("pfInvolved", Constants.Refund.PLATFORM_IS_NOT_INVOLVED);
        refundWrapper.lt("auditTime", calendar.getTime());
        refundWrapper.last("limit 100");
        return refundOrderDao.selectList(refundWrapper);
    }


    /**
     * Description: 自动审核超时未签收的寄回退换货
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/5
     *
     * @param:
     **/
    public List<RefundOrderEntity> queryWaitReceive() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -Constants.Refund.ACCEPT_REFUND_GOODS_MAX_DAYS);

        EntityWrapper<RefundOrderEntity> refundWrapper = new EntityWrapper<>();
        refundWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        refundWrapper.eq("refundState", RefundOrderStateEnum.WAITING_FOR_RECEIVE.getCode());
        refundWrapper.eq("pfInvolved", Constants.Refund.PLATFORM_IS_NOT_INVOLVED);
        refundWrapper.lt("shipmentsTime", calendar.getTime());
        refundWrapper.last("limit 100");
        return refundOrderDao.selectList(refundWrapper);
    }


    public List<RefundOrderEntity> queryPageByCustomer(PagePO pagePO, Long memberId) {
        Page<OrderEntity> page = PageDataUtil.buildPageParam(pagePO);
        EntityWrapper<RefundOrderEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        entityEntityWrapper.eq("memberId", memberId);
        entityEntityWrapper.orderBy("refundTime", false);
        return refundOrderDao.selectPage(page, entityEntityWrapper);
    }

    public Map<Long, List<RefundOrderDetailEntity>> queryRefundDetailsMap(List<Long> refundOrderIds) {
        EntityWrapper<RefundOrderDetailEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.in("refundOrderId", refundOrderIds);
        entityEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<RefundOrderDetailEntity> refundOrderDetailEntities = refundOrderDetailDao.selectList(entityEntityWrapper);


        if (CollectionUtils.isEmpty(refundOrderDetailEntities)) {
            return Collections.emptyMap();
        }
        Map<Long, List<RefundOrderDetailEntity>> map = new HashMap<>(refundOrderIds.size());
        refundOrderDetailEntities.forEach(refundOrderDetailEntity -> {
            Long refundOrderId = refundOrderDetailEntity.getRefundOrderId();
            List<RefundOrderDetailEntity> list = map.get(refundOrderId);
            if (null == list) {
                list = new ArrayList<>();
            }
            list.add(refundOrderDetailEntity);
            map.put(refundOrderId, list);
        });
        return map;
    }

    /**
     * Description: 根据订单ID 查询所有售后订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/26
     *
     * @param:
     **/
    public Integer queryOrderRefundApplyCount(Long orderId) {
        if (null == orderId) {
            return 0;
        }
        EntityWrapper<RefundOrderEntity> wrapper = new EntityWrapper<>();
        wrapper.setSqlSelect("count(id)");
        wrapper.eq("orderId", orderId);
        return refundOrderDao.selectCount(wrapper);
    }

    /**
     * Description: 查询该订单的售后被拒绝的次数
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/28
     *
     * @param:
     **/
    public Integer queryRefuseRefundApplyCount(Long orderId) {
        if (null == orderId) {
            return 0;
        }
        EntityWrapper<RefundOrderEntity> wrapper = new EntityWrapper<>();
        wrapper.setSqlSelect("count(id)");
        wrapper.eq("orderId", orderId);
        wrapper.gt("refundState", RefundOrderStateEnum.REFUNDED.getCode());
        wrapper.lt("refundState", RefundOrderStateEnum.CLOSE.getCode());
        return refundOrderDao.selectCount(wrapper);
    }


    /**
     * Description: 获取售后订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/9
     *
     * @param:
     **/
    public RefundOrderEntity queryRefundOrder(Long refundOrderId) {
        if (null == refundOrderId) {
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_ID_CANT_BE_NULL);
        }
        RefundOrderEntity condition = new RefundOrderEntity();
        condition.cleanInit();
        condition.setId(refundOrderId);
        condition.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        RefundOrderEntity refundOrderEntity = refundOrderDao.selectOne(condition);
        if (null == refundOrderEntity) {
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_IS_NOT_FOUND);
        }
        return refundOrderEntity;
    }

    /**
     * Description: 商家查询某个售后订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/30
     *
     * @param:
     **/
    public RefundOrderEntity queryStoreRefundOrder(Long refundOrderId, Long storeId) {
        if (null == refundOrderId) {
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_ID_CANT_BE_NULL);
        }
        RefundOrderEntity condition = new RefundOrderEntity();
        condition.cleanInit();
        condition.setId(refundOrderId);
        condition.setStoreId(storeId);
        condition.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        RefundOrderEntity refundOrderEntity = refundOrderDao.selectOne(condition);
        if (null == refundOrderEntity) {
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_IS_NOT_FOUND);
        }
        return refundOrderEntity;
    }


    /**
     * Description: 用户获取售后订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/9
     *
     * @param:
     **/
    public RefundOrderEntity queryCustomerRefundOrder(Long refundOrderId, Long memberId) {
        if (null == refundOrderId) {
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_ID_CANT_BE_NULL);
        }
        RefundOrderEntity queryCondition = new RefundOrderEntity();
        queryCondition.cleanInit();
        queryCondition.setId(refundOrderId);
        queryCondition.setMemberId(memberId);
        queryCondition.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        RefundOrderEntity refundOrderEntity = refundOrderDao.selectOne(queryCondition);
        if (null == refundOrderEntity) {
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_IS_NOT_FOUND);
        }
        return refundOrderEntity;
    }

    public List<RefundOrderDetailEntity> queryDetailsByRefundId(Long refundOrderId) {
        return queryDetails(refundOrderId, null);
    }

    public List<RefundOrderDetailEntity> queryDetailsByIds(List<Long> refundDetailIds) {
        return queryDetails(null, refundDetailIds);
    }

    public Set<Long> queryDetailIdsByRefundId(Long refundOrderId) {
        return this.queryDetailsByRefundId(refundOrderId).stream().map(RefundOrderDetailEntity::getOrderDetailId).collect(Collectors.toSet());
    }

    /**
     * Description: 获取售后订单明细
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/9
     *
     * @param: refundOrderId 和 refundOrderDetailIds 二选一
     **/
    public List<RefundOrderDetailEntity> queryDetails(Long refundOrderId, List<Long> refundOrderDetailIds) {
        if (null == refundOrderId && CollectionUtils.isEmpty(refundOrderDetailIds)) {
            log.warn("----------查询售后订单明细参数不正确！");
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_DETAILS_IS_NOT_FOUND);
        }
        EntityWrapper<RefundOrderDetailEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        if (CollectionUtils.isNotEmpty(refundOrderDetailIds)) {
            entityEntityWrapper.in("id", refundOrderDetailIds);
        } else {
            if (null != refundOrderId) {
                entityEntityWrapper.eq("refundOrderId", refundOrderId);
            }
        }
        List<RefundOrderDetailEntity> refundOrderDetailEntities = refundOrderDetailDao.selectList(entityEntityWrapper);
        if (CollectionUtils.isEmpty(refundOrderDetailEntities)) {
            log.warn("----------cant found the order details, params：{}", JSON.toJSONString(refundOrderDetailIds));
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_DETAILS_IS_NOT_FOUND);
        }
        return refundOrderDetailEntities;
    }

    public List<Long> queryOrderDetailIds(Long refundOrderId) {
        List<RefundOrderDetailEntity> refundOrderDetailEntities = this.queryDetailsByRefundId(refundOrderId);
        return refundOrderDetailEntities.stream().map(RefundOrderDetailEntity::getOrderDetailId).collect(Collectors.toList());
    }


}
