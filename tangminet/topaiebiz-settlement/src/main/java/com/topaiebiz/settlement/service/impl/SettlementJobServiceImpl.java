package com.topaiebiz.settlement.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.DateUtils;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.constants.MerchantConstants;
import com.topaiebiz.merchant.dto.store.MerchantInfoDTO;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.settlement.dao.StoreSettlementDao;
import com.topaiebiz.settlement.dao.StoreSettlementOrderDao;
import com.topaiebiz.settlement.dao.StoreSettlementRefundOrderDao;
import com.topaiebiz.settlement.dto.*;
import com.topaiebiz.settlement.entity.StoreSettlementEntity;
import com.topaiebiz.settlement.entity.StoreSettlementOrderEntity;
import com.topaiebiz.settlement.entity.StoreSettlementRefundOrderEntity;
import com.topaiebiz.settlement.service.SettlementJobService;
import com.topaiebiz.trade.api.order.OrderPayServiceApi;
import com.topaiebiz.trade.api.order.OrderServiceApi;
import com.topaiebiz.trade.api.refund.RefundServiceApi;
import com.topaiebiz.trade.dto.order.GoodsPromotionDetailDTO;
import com.topaiebiz.trade.dto.order.OrderDTO;
import com.topaiebiz.trade.dto.order.OrderPayDTO;
import com.topaiebiz.trade.dto.refund.RefundDTO;
import com.topaiebiz.trade.dto.refund.RefundDetailDTO;
import com.topaiebiz.trade.dto.settlement.SettlementOrderDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.topaiebiz.settlement.contants.Constants.SettlementStateEnum.FINISHED;
import static com.topaiebiz.settlement.contants.Constants.SettlementStateEnum.NO_MERCHANT_CHECK;
import static com.topaiebiz.settlement.exception.StoreSettlementExceptionEnum.CREATE_SETTLEMENT_FAILURE;

/***
 * @author yfeng
 * @date 2018-03-24 13:06
 */
@Slf4j
@Service
public class SettlementJobServiceImpl implements SettlementJobService, InitializingBean {
    private Date defaultLastSettlementDate;
    private static final Integer BATCH_SIZE = 500;
    private static final Integer FILE_DAY = 5;
    private static final Integer HALF_MONTH = 15;

    @Autowired
    private StoreSettlementDao settlementDao;
    @Autowired
    private StoreSettlementOrderDao settlementOrderDao;
    @Autowired
    private StoreSettlementRefundOrderDao settlementRefundOrderDao;
    @Autowired
    private OrderServiceApi orderServiceApi;
    @Autowired
    private RefundServiceApi refundServiceApi;
    @Autowired
    private OrderPayServiceApi orderPayServiceApi;
    @Autowired
    private StoreApi storeApi;


    @Override
    public void afterPropertiesSet() throws Exception {
        // 第一次结算时，无上次结算日期，即无法确定本次结算时间区间的起始值
        // 这里使用新系统发布时间前一天即2018-03-05日作为上次结算区间的结束值
        DateTime time = new DateTime(2018, 3, 5, 0, 0, 0);
        defaultLastSettlementDate = time.toDate();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createSettlement(StoreInfoDetailDTO storeDTO) {
       /* if (MerchantConstants.IsOwnShop.SELF_STORE.getCode().equals(storeDTO.getOwnShop())) {
            log.warn("店铺{}:{} 是自营店，不在系统中做结算", storeDTO.getId(), storeDTO.getName());
            return true;
        }*/
        Date lastSettlementTime = getLastSettlementEndDate(storeDTO);
        Date todayStart = LocalDate.now().toDate();

        /**
         * 结算区间
         * start - end
         * 交易/售后的结束时间作为判断条件， start<= 结束时间 < end
         */
        return doCreateSettlement(storeDTO, lastSettlementTime, todayStart);
    }

    public Date getNextSettlementDate(StoreInfoDetailDTO storeDTO, Date curEnd) {
        if (MerchantConstants.SettleCycle.SETTLECYCLE_MONTH.getSettleCycle().equals(storeDTO.getSettleCycle())) {
            return DateUtils.nextMonthStart(curEnd);
        } else if (MerchantConstants.SettleCycle.SETTLECYCLE_HALFMONTH.getSettleCycle().equals(storeDTO.getSettleCycle())) {
            if (DateUtils.isMonthFirstDay(curEnd)) {
                return DateUtils.getNextDay(curEnd, HALF_MONTH);
            }
            return DateUtils.nextMonthStart(curEnd);
        } else if (MerchantConstants.SettleCycle.SETTLECYCLE_WEEK.getSettleCycle().equals(storeDTO.getSettleCycle())) {
            return DateUtils.nextWeekStart(curEnd);
        } else if (MerchantConstants.SettleCycle.SETTLECYCLE_FIVEDAY.getSettleCycle().equals(storeDTO.getSettleCycle())) {
            return DateUtils.getNextDay(curEnd, 5);
        } else {
            log.error("店铺{}结算周期:{}", storeDTO.getId(), storeDTO.getSettleCycle());
            throw new GlobalException(CREATE_SETTLEMENT_FAILURE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createSettlement(StoreInfoDetailDTO storeDTO, Date startTime, Date endTime) {
      /*  if (MerchantConstants.IsOwnShop.SELF_STORE.getCode().equals(storeDTO.getOwnShop())) {
            log.warn("店铺{}:{} 是自营店，不在系统中做结算", storeDTO.getId(), storeDTO.getName());
            return true;
        }*/
        return doCreateSettlement(storeDTO, startTime, endTime);
    }

    @Override
    public List<Long> getSettleIds() {
        EntityWrapper<StoreSettlementEntity> settleCond = new EntityWrapper<>();
        settleCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        settleCond.isNull("refundCommission");
        List<StoreSettlementEntity> settlementList = settlementDao.selectList(settleCond);
        if (CollectionUtils.isEmpty(settlementList)) {
            return null;
        }
        //获取没有退款订单佣金数据的ID集合
        List<Long> ids = settlementList.stream().map(settlement -> settlement.getId()).collect(Collectors.toList());
        return ids;
    }

    @Override
    public Integer putRefundCommission(List<Long> ids) {
        Integer count = 0;
        if (CollectionUtils.isEmpty(ids)) {
            return count;
        }
        //批量查询退款订单的佣金总和
        EntityWrapper<StoreSettlementRefundOrderEntity> refundCond = new EntityWrapper<>();
        refundCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        refundCond.in("settlementId", ids);
        List<StoreSettlementRefundOrderEntity> refundList = settlementRefundOrderDao.selectList(refundCond);
        if (CollectionUtils.isNotEmpty(refundList)) {
            //拥有售后结算单的ID集合
            List<Long> updateIds = new ArrayList<>();
            Map<Long, List<StoreSettlementRefundOrderEntity>> refundMap = refundList.stream().collect(Collectors.groupingBy(StoreSettlementRefundOrderEntity::getSettlementId));

            for (Map.Entry<Long, List<StoreSettlementRefundOrderEntity>> entry : refundMap.entrySet()) {
                StoreSettlementEntity settlement = new StoreSettlementEntity();
                settlement.cleanInit();
                settlement.setId(entry.getKey());
                BigDecimal refundCommission = calcRefundCommission(entry.getValue());
                //退款订单佣金
                settlement.setRefundCommission(refundCommission);
                settlement.setLastModifiedTime(new Date());
                count += settlementDao.updateById(settlement);
                updateIds.add(entry.getKey());
            }
            //筛选无售后订单的ID集合
            ids.removeAll(updateIds);
        }

        if (CollectionUtils.isNotEmpty(ids)) {
            //设置无售后订单的退款订单佣金为0
            EntityWrapper<StoreSettlementEntity> updateCond = new EntityWrapper<>();
            updateCond.in("id", ids);
            updateCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            StoreSettlementEntity settlement = new StoreSettlementEntity();
            settlement.cleanInit();
            //退款订单佣金
            settlement.setRefundCommission(BigDecimal.ZERO);
            settlement.setLastModifiedTime(new Date());
            count += settlementDao.update(settlement, updateCond);
        }

        return count;
    }

    @Override
    public List<Long> getOrderIds() {
        //查询需要订正的数据
        EntityWrapper<StoreSettlementOrderEntity> orderCond = new EntityWrapper<>();
        orderCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        orderCond.isNull("goodsDetail");
        List<StoreSettlementOrderEntity> orderList = settlementOrderDao.selectList(orderCond);
        if (CollectionUtils.isEmpty(orderList)) {
            return null;
        }
        List<Long> orderIds = orderList.stream().map(order -> order.getOrderId()).collect(Collectors.toList());

        return orderIds;
    }

    @Override
    public Integer putOrderGoodDetail(List<Long> orderIds) {
        Integer count = 0;
        if (CollectionUtils.isEmpty(orderIds)) {
            return count;
        }

        //查询结算单详情
        Map<Long, List<SettlementOrderDetailDTO>> orderDetailsMap = orderServiceApi.querySettlementOrderDetails(orderIds);
        for (Long orderId : orderIds) {
            List<SettlementOrderDetailDTO> orderDetails = orderDetailsMap.get(orderId);
            String goodsDetail = packDetail(orderDetails);
            //更新结算订单
            EntityWrapper<StoreSettlementOrderEntity> updateOrderCond = new EntityWrapper<>();
            updateOrderCond.eq("orderId", orderId);
            updateOrderCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            StoreSettlementOrderEntity orderEntity = new StoreSettlementOrderEntity();
            orderEntity.cleanInit();
            orderEntity.setGoodsDetail(goodsDetail);
            orderEntity.setLastModifiedTime(new Date());
            count += settlementOrderDao.update(orderEntity, updateOrderCond);
        }
        return count;
    }

    @Override
    public List<StoreSettlementRefundOrderEntity> getRefundIds() {
        EntityWrapper<StoreSettlementRefundOrderEntity> refundCond = new EntityWrapper<>();
        refundCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        refundCond.isNull("goodsDetail");
        List<StoreSettlementRefundOrderEntity> refundList = settlementRefundOrderDao.selectList(refundCond);
        return refundList;
    }

    @Override
    public Integer putRefundGoodDetail(List<StoreSettlementRefundOrderEntity> refundList) {
        Integer count = 0;
        if (CollectionUtils.isEmpty(refundList)) {
            return count;
        }
        for (StoreSettlementRefundOrderEntity refund : refundList) {
            StoreSettlementOrderEntity orderCond = new StoreSettlementOrderEntity();
            orderCond.cleanInit();
            orderCond.setOrderId(refund.getOrderId());
            orderCond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            StoreSettlementOrderEntity settleOrder = null;
            try {
                settleOrder = settlementOrderDao.selectOne(orderCond);
            } catch (Exception e) {
                log.error("数据错误！一个订单存在多条结算订单数据，订单号：{}", refund.getOrderId());
            }

            if (settleOrder == null) {
                continue;
            }
            //更新结算订单
            EntityWrapper<StoreSettlementRefundOrderEntity> updateOrderCond = new EntityWrapper<>();
            updateOrderCond.eq("refundId", refund.getRefundId());
            updateOrderCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            StoreSettlementRefundOrderEntity refundEntity = new StoreSettlementRefundOrderEntity();
            refundEntity.cleanInit();
            refundEntity.setGoodsDetail(settleOrder.getGoodsDetail());
            refundEntity.setLastModifiedTime(new Date());
            count += settlementRefundOrderDao.update(refundEntity, updateOrderCond);
        }
        return count;
    }

    private boolean doCreateSettlement(StoreInfoDetailDTO storeDTO, Date startTime, Date endTime) {
        //生成一个零时的结算单ID
        Long settlementId = System.currentTimeMillis();
        Integer orderCount = generateSettlementOrders(settlementId, storeDTO, startTime, endTime);

        //计算结算单
        StoreSettlementEntity settlement = generateSettlement(storeDTO, settlementId);

        if (0 == orderCount) {
            // 本月无订单完成,状态置为已结算
            settlement.setState(FINISHED.getCode());
        }

        settlement.setSettleStartDate(startTime);
        settlement.setSettleEndDate(endTime);

        MerchantInfoDTO merchantInfoDTO = storeApi.getMerchant(storeDTO.getMerchantId());
        if (merchantInfoDTO == null) {
            log.error("店铺{}的商户不存在", storeDTO.getId());
            throw new GlobalException(CREATE_SETTLEMENT_FAILURE);
        }
        settlement.setMerchantName(merchantInfoDTO.getName());

        settlementDao.insert(settlement);
        Long newSettlementId = settlement.getId();

        //更新订单快照
        recoverSettlementOrder(settlementId, newSettlementId, storeDTO.getId());

        //更新退款快照
        recoverSettlementRefundOrder(settlementId, newSettlementId, storeDTO.getId());

        //更新下次结算时间
        Date nextSettlementDate = getNextSettlementDate(storeDTO, endTime);
        storeApi.updateNextSettleDate(storeDTO.getId(), nextSettlementDate);

        return true;
    }

    private void recoverSettlementOrder(Long oldSettlementId, Long newSettlementId, Long storeId) {
        //变更结算ID和deletedFlag
        StoreSettlementOrderEntity update = new StoreSettlementOrderEntity();
        update.cleanInit();
        update.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        update.setSettlementId(newSettlementId);

        //定义更新条件
        StoreSettlementOrderEntity cond = new StoreSettlementOrderEntity();
        cond.cleanInit();
        cond.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        cond.setSettlementId(oldSettlementId);
        cond.setStoreId(storeId);

        settlementOrderDao.update(update, new EntityWrapper<>(cond));
    }

    private void recoverSettlementRefundOrder(Long oldSettlementId, Long newSettlementId, Long storeId) {
        //变更结算ID和deletedFlag
        StoreSettlementRefundOrderEntity update = new StoreSettlementRefundOrderEntity();
        update.cleanInit();
        update.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        update.setSettlementId(newSettlementId);

        //定义更新条件
        StoreSettlementRefundOrderEntity cond = new StoreSettlementRefundOrderEntity();
        cond.cleanInit();
        cond.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        cond.setSettlementId(oldSettlementId);
        cond.setStoreId(storeId);

        settlementRefundOrderDao.update(update, new EntityWrapper<>(cond));
    }

    private StoreSettlementEntity generateSettlement(StoreInfoDetailDTO storeDTO, Long settlementId) {
        StoreSettlementEntity settlement = new StoreSettlementEntity();
        settlement.setMerchantId(storeDTO.getMerchantId());
        settlement.setMerchantName(storeDTO.getMerchantName());
        settlement.setStoreId(storeDTO.getId());
        settlement.setStoreName(storeDTO.getName());
        settlement.setSettleCycle(storeDTO.getSettleCycle());

        //统计正向订单数据
        SettlementOrderStatisDTO orderStatisDTO = settlementOrderDao.selectSettlementStatis(settlementId, storeDTO.getId());
        if (orderStatisDTO == null) {
            orderStatisDTO = new SettlementOrderStatisDTO();
        }
        fixOrderStatis(orderStatisDTO);

        SettlementRefundOrderStatisDTO refundStatisDTO = settlementRefundOrderDao.selectSettlementStatis(settlementId, storeDTO.getId());
        if (refundStatisDTO == null) {
            refundStatisDTO = new SettlementRefundOrderStatisDTO();
        }
        fixRefundStatis(refundStatisDTO);

        BigDecimal settlementSum = orderStatisDTO.getSettleSum().subtract(refundStatisDTO.getSettleSum());

        //退款积分
        settlement.setPointRefundSum(refundStatisDTO.getPointSum());
        //退款礼卡
        settlement.setCardRefundSum(refundStatisDTO.getCardSum());

        settlement.setBalanceSum(orderStatisDTO.getBalanceSum());
        settlement.setCardSum(orderStatisDTO.getCardSum());
        settlement.setCashSum(orderStatisDTO.getCashSum());
        settlement.setPointSum(orderStatisDTO.getPointSum());
        settlement.setGoodsTotal(orderStatisDTO.getGoodsTotal());
        settlement.setPaySum(orderStatisDTO.getPayPrice());
        settlement.setFreight(orderStatisDTO.getFreight());
        settlement.setTax(orderStatisDTO.getTax());

        settlement.setPromStoreSum(orderStatisDTO.getPromStoreSum());
        settlement.setPromPlatformSum(orderStatisDTO.getPromPlatformSum());
        settlement.setRefundSum(refundStatisDTO.getSettleSum());
        settlement.setPlatformCommission(orderStatisDTO.getPlatformCommission());
        //退款订单佣金
        settlement.setRefundCommission(refundStatisDTO.getPlatformCommission());
        settlement.setSettleSum(settlementSum);
        settlement.setState(NO_MERCHANT_CHECK.getCode());
        return settlement;
    }

    private void fixOrderStatis(SettlementOrderStatisDTO orderStatisDTO) {
        //修正退款统计
        if (orderStatisDTO.getSettleSum() == null) {
            orderStatisDTO.setSettleSum(BigDecimal.ZERO);
        }
        if (orderStatisDTO.getBalanceSum() == null) {
            orderStatisDTO.setBalanceSum(BigDecimal.ZERO);
        }
        if (orderStatisDTO.getCardSum() == null) {
            orderStatisDTO.setCardSum(BigDecimal.ZERO);
        }
        if (orderStatisDTO.getPointSum() == null) {
            orderStatisDTO.setPointSum(BigDecimal.ZERO);
        }
        if (orderStatisDTO.getCashSum() == null) {
            orderStatisDTO.setCashSum(BigDecimal.ZERO);
        }
        if (orderStatisDTO.getFreight() == null) {
            orderStatisDTO.setFreight(BigDecimal.ZERO);
        }
        if (orderStatisDTO.getTax() == null) {
            orderStatisDTO.setTax(BigDecimal.ZERO);
        }
        if (orderStatisDTO.getGoodsTotal() == null) {
            orderStatisDTO.setGoodsTotal(BigDecimal.ZERO);
        }
        if (orderStatisDTO.getPayPrice() == null) {
            orderStatisDTO.setPayPrice(BigDecimal.ZERO);
        }
        if (orderStatisDTO.getPromPlatformSum() == null) {
            orderStatisDTO.setPromPlatformSum(BigDecimal.ZERO);
        }
        if (orderStatisDTO.getPromStoreSum() == null) {
            orderStatisDTO.setPromStoreSum(BigDecimal.ZERO);
        }
        if (orderStatisDTO.getPlatformCommission() == null) {
            orderStatisDTO.setPlatformCommission(BigDecimal.ZERO);
        }
    }

    private void fixRefundStatis(SettlementRefundOrderStatisDTO refundStatisDTO) {
        //修正退款统计
        if (refundStatisDTO.getSettleSum() == null) {
            refundStatisDTO.setSettleSum(BigDecimal.ZERO);
        }
        if (refundStatisDTO.getBalanceSum() == null) {
            refundStatisDTO.setBalanceSum(BigDecimal.ZERO);
        }
        if (refundStatisDTO.getCardSum() == null) {
            refundStatisDTO.setCardSum(BigDecimal.ZERO);
        }
        if (refundStatisDTO.getPointSum() == null) {
            refundStatisDTO.setPointSum(BigDecimal.ZERO);
        }
        if (refundStatisDTO.getCashSum() == null) {
            refundStatisDTO.setCashSum(BigDecimal.ZERO);
        }
        if (refundStatisDTO.getRefundPrice() == null) {
            refundStatisDTO.setRefundPrice(BigDecimal.ZERO);
        }
        if (refundStatisDTO.getPromPlatformSum() == null) {
            refundStatisDTO.setPromPlatformSum(BigDecimal.ZERO);
        }
        if (refundStatisDTO.getPromStoreSum() == null) {
            refundStatisDTO.setPromStoreSum(BigDecimal.ZERO);
        }
        if (refundStatisDTO.getFreight() == null) {
            refundStatisDTO.setFreight(BigDecimal.ZERO);
        }
        if (refundStatisDTO.getTax() == null) {
            refundStatisDTO.setTax(BigDecimal.ZERO);
        }
        if (refundStatisDTO.getPlatformCommission() == null) {
            refundStatisDTO.setPlatformCommission(BigDecimal.ZERO);
        }
    }

    private GoodsCommissionDetailDTO queryGoodsCommissionDetail(GoodsCommissionDTO commission, Long skuId) {
        List<GoodsCommissionDetailDTO> commissionDetails = commission.getCommissionDetail();
        for (GoodsCommissionDetailDTO commissionDetail : commissionDetails) {
            if (skuId.equals(commissionDetail.getSkuId())) {
                return commissionDetail;
            }
        }
        return null;
    }

    private Integer generateSettlementOrders(Long settlementId, StoreInfoDetailDTO storeDTO, Date startTime, Date endTime) {
        Integer orderCount = 0;
        Long startId = 0L;
        while (true) {
            List<OrderDTO> orders = orderServiceApi.queryFinishedOrders(storeDTO.getId(), startTime, endTime, startId, BATCH_SIZE);
            if (CollectionUtils.isEmpty(orders)) {
                break;
            }
            List<Long> orderIds = orders.stream().map(item -> item.getId()).collect(Collectors.toList());
            List<Long> payIds = orders.stream().map(item -> item.getPayId()).distinct().collect(Collectors.toList());
            Map<Long, List<SettlementOrderDetailDTO>> orderDetails = orderServiceApi.querySettlementOrderDetails(orderIds);
            Map<Long, OrderPayDTO> orderPays = orderPayServiceApi.queryPayInfos(payIds);
            Map<Long, StoreSettlementOrderEntity> settlementOrderMap = new HashMap<>();

            //生成购物订单结算快照
            for (OrderDTO orderDTO : orders) {
                OrderPayDTO orderPayDTO = orderPays.get(orderDTO.getPayId());
                List<SettlementOrderDetailDTO> orderDetailDTOs = orderDetails.get(orderDTO.getId());
                StoreSettlementOrderEntity settlementOrder = saveSettlementOrder(settlementId, orderDTO, orderPayDTO, orderDetailDTOs);
                settlementOrderMap.put(orderDTO.getId(), settlementOrder);
            }

            //计算退款结算快照
            List<RefundDTO> refundOrders = refundServiceApi.queryFinishedRefundOrders(orderIds);
            if (CollectionUtils.isNotEmpty(refundOrders)) {
                List<Long> refundIds = refundOrders.stream().map(item -> item.getId()).collect(Collectors.toList());
                Map<Long, List<RefundDetailDTO>> refundDetailsMap = refundServiceApi.querySKURefundDetails(refundIds);
                for (RefundDTO refundOrderDTO : refundOrders) {
                    List<RefundDetailDTO> refundDetails = refundDetailsMap.get(refundOrderDTO.getId());
                    if (CollectionUtils.isEmpty(refundDetails)) {
                        log.error("{}没有退款详情", refundOrderDTO.getId());
                        throw new GlobalException(CREATE_SETTLEMENT_FAILURE);
                    }
                    RefundDetailDTO refundDetail = refundDetails.get(0);
                    StoreSettlementOrderEntity settlementOrder = settlementOrderMap.get(refundOrderDTO.getOrderId());
                    //根据退款订单、退款详情、购物订单结算快照生成退款订单结算快照
                    saveSettlementRefundOrder(settlementId, refundOrderDTO, refundDetail, settlementOrder);
                }
            }

            //更新任务状态
            Integer curCount = orders.size();
            startId = orders.get(curCount - 1).getId();
            orderCount += curCount;
        }
        return orderCount;
    }

    private StoreSettlementOrderEntity saveSettlementOrder(Long settlementId, OrderDTO orderDTO, OrderPayDTO orderPayDTO, List<SettlementOrderDetailDTO> orderDetailDTOs) {
        if (orderPayDTO == null) {
            log.warn("订单{}无支付单", orderDTO.getId());
            throw new GlobalException(CREATE_SETTLEMENT_FAILURE);
        }
        if (CollectionUtils.isEmpty(orderDetailDTOs)) {
            log.warn("订单{}无订单详情", orderDTO.getId());
            throw new GlobalException(CREATE_SETTLEMENT_FAILURE);
        }
        fixGoodsOrder(orderDTO);
        StoreSettlementOrderEntity order = new StoreSettlementOrderEntity();
        order.setSettlementId(settlementId);
        order.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        order.setStoreId(orderDTO.getStoreId());
        order.setGoodsTotal(orderDTO.getGoodsTotal());
        order.setMemberId(orderDTO.getMemberId());

        order.setPromStoreSum(orderDTO.getDiscountTotal().subtract(orderDTO.getPlatformDiscount()));
        order.setPromPlatformSum(orderDTO.getPlatformDiscount());

        order.setBalanceSum(orderDTO.getBalance());
        order.setPointSum(orderDTO.getScore());
        order.setCardSum(orderDTO.getCardPrice());

        BigDecimal orderCash = orderDTO.getPayPrice().subtract(orderDTO.getBalance()).subtract(orderDTO.getScore()).subtract(orderDTO.getCardPrice());
        order.setCashSum(orderCash);
        order.setPayPrice(orderDTO.getPayPrice());
        order.setFinishTime(orderDTO.getCompleteTime());
        order.setOrderId(orderDTO.getId());
        order.setFreight(orderDTO.getActualFreight());
        order.setTax(BigDecimal.ZERO);

        //支付信息
        order.setPaymentChannel(orderPayDTO.getPayType());
        order.setPaymentTradeNo(orderPayDTO.getOuterPaySn());
        order.setPayTime(orderDTO.getPayTime());

        //本单佣金
        GoodsCommissionDTO commission = getCommissionInfo(orderDetailDTOs);
        BigDecimal commisionTotal = commission.getCommissionTotal().setScale(4, BigDecimal.ROUND_HALF_UP);
        order.setPlatformCommission(commisionTotal);
        order.setCommissionDetail(JSON.toJSONString(commission));

        //商品详情
        order.setGoodsDetail(getGoodsDetail(commission.getGoodsDetail()));
        //本单应结金额
        BigDecimal settlementAmount = orderDTO.getPayPrice().add(orderDTO.getPlatformDiscount()).subtract(commisionTotal);
        order.setSettleSum(settlementAmount);

        settlementOrderDao.insert(order);
        return order;
    }

    private String getGoodsDetail(List<GoodsDetailDTO> goodsDetails) {
        List<List<String>> datas = new ArrayList<>();
        for (GoodsDetailDTO gd : goodsDetails) {
            List<String> items = new ArrayList<>();
            items.add(StringUtils.join("名称:", gd.getName()));
            items.add(StringUtils.join("单价:", gd.getGoodsPrice()));
            items.add(StringUtils.join("数量:", gd.getGoodsNum()));
            items.add(StringUtils.join("店铺营销贴现:", gd.getPromStoreAmount()));
            items.add(StringUtils.join("平台营销贴现:", gd.getPromPlatformAmount()));
            items.add(StringUtils.join("实付:", gd.getPayPrice()));
            items.add(StringUtils.join("佣金比例:", gd.getBrokerageRatio(), "%"));
            datas.add(items);
        }
        return JSON.toJSONString(datas);
    }

    private void fixGoodsOrder(OrderDTO orderDTO) {
        if (orderDTO.getBalance() == null) {
            orderDTO.setBalance(BigDecimal.ZERO);
        }
        if (orderDTO.getCardPrice() == null) {
            orderDTO.setCardPrice(BigDecimal.ZERO);
        }
        if (orderDTO.getScore() == null) {
            orderDTO.setScore(BigDecimal.ZERO);
        }
    }

    private void saveSettlementRefundOrder(Long settlementId, RefundDTO refundOrderDTO, RefundDetailDTO refundDetail, StoreSettlementOrderEntity settlementOrder) {
        StoreSettlementRefundOrderEntity refundOrder = new StoreSettlementRefundOrderEntity();
        refundOrder.setSettlementId(settlementId);
        refundOrder.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        refundOrder.setStoreId(refundOrderDTO.getStoreId());

        refundOrder.setOrderId(refundOrderDTO.getOrderId());
        refundOrder.setRefundId(refundOrderDTO.getId());
        refundOrder.setMemberId(refundOrderDTO.getMemberId());
        refundOrder.setPaymentTradeNo(refundOrderDTO.getCallBackNo());
        refundOrder.setFinishTime(refundOrderDTO.getCompleteTime());
        refundOrder.setCashSum(refundOrderDTO.getRefundThirdAmount());
        refundOrder.setApplyTime(refundOrderDTO.getCreatedTime());
        refundOrder.setBalanceSum(refundOrderDTO.getRefundBalance());
        refundOrder.setCardSum(refundOrderDTO.getRefundCardPrice());
        refundOrder.setPointSum(refundOrderDTO.getRefundIntegralPrice());
        refundOrder.setFreight(refundOrderDTO.getRefundFreight());
        refundOrder.setTax(BigDecimal.ZERO);
        refundOrder.setRefundPrice(refundOrderDTO.getRefundPrice());

        GoodsCommissionDTO commission = JSON.parseObject(settlementOrder.getCommissionDetail(), GoodsCommissionDTO.class);
        GoodsCommissionDetailDTO buyOrderGoodsDetail = queryGoodsCommissionDetail(commission, refundDetail.getGoodSkuId());
        BigDecimal goodsCommission = buyOrderGoodsDetail.getCommissionAmount().setScale(4, BigDecimal.ROUND_HALF_UP);
        refundOrder.setGoodsTotal(buyOrderGoodsDetail.getGoodsTotal().setScale(4, BigDecimal.ROUND_HALF_UP));

        refundOrder.setPromPlatformSum(buyOrderGoodsDetail.getPromPlatformAmount().setScale(4, BigDecimal.ROUND_HALF_UP));
        refundOrder.setPromStoreSum(buyOrderGoodsDetail.getPromStoreAmount().setScale(4, BigDecimal.ROUND_HALF_UP));

        //商品详情
        refundOrder.setGoodsDetail(settlementOrder.getGoodsDetail());
        /**
         * 商品价格比例 = 实退价格/实付价格
         */
        BigDecimal goodsPriceRate = refundOrderDTO.getRefundPrice().divide(buyOrderGoodsDetail.getPayPrice(), 4, BigDecimal.ROUND_HALF_UP);
        /**
         * 回退的佣金 = 单品的佣金 * 商品价格比例
         */
        BigDecimal commissionBack = goodsPriceRate.multiply(buyOrderGoodsDetail.getCommissionAmount()).setScale(4, BigDecimal.ROUND_HALF_UP);
        refundOrder.setPlatformCommission(commissionBack);

        /**
         *  单品的平台贴现 - 单品的佣金
         */
        BigDecimal commissionBase = buyOrderGoodsDetail.getPromPlatformAmount().subtract(goodsCommission);
        /**
         * (单品的平台贴现 - 单品的佣金) * 商品价格比例
         */
        BigDecimal platformBack = goodsPriceRate.multiply(commissionBase).setScale(4, BigDecimal.ROUND_HALF_UP);
        /**
         * 结算金额 = 退款金额 + (单品的平台贴现 - 单品的佣金) * 商品价格比例
         */
        BigDecimal settlementSum = refundOrderDTO.getRefundPrice().add(platformBack);
        refundOrder.setSettleSum(settlementSum);

        settlementRefundOrderDao.insert(refundOrder);
    }

    private GoodsCommissionDTO getCommissionInfo(List<SettlementOrderDetailDTO> orderDetailDTOs) {
        GoodsCommissionDTO commissionDTO = new GoodsCommissionDTO();
        BigDecimal commissionAmount = BigDecimal.ZERO;
        for (SettlementOrderDetailDTO orderDetail : orderDetailDTOs) {
            //商品的营销优惠详情
            GoodsPromotionDetailDTO promotionDetail = JSON.parseObject(orderDetail.getPromotionDetail(), GoodsPromotionDetailDTO.class);

            //(商品支付总价 + 平台贴现 - 税费) * 佣金比例 = 佣金
            BigDecimal goodsAmount = orderDetail.getPayPrice().add(promotionDetail.getPlatformDiscount());
            BigDecimal curCommission = goodsAmount.multiply(orderDetail.getBrokerageRatio()).divide(new BigDecimal(100));

            GoodsCommissionDetailDTO commissionDetailDTO = new GoodsCommissionDetailDTO();
            commissionDetailDTO.setSkuId(orderDetail.getSkuId());
            commissionDetailDTO.setPayPrice(orderDetail.getPayPrice());
            commissionDetailDTO.setCommissionAmount(curCommission);
            commissionDetailDTO.setGoodsTotal(orderDetail.getTotalPrice());
            commissionDetailDTO.setPromPlatformAmount(promotionDetail.getPlatformDiscount());
            commissionDetailDTO.setPromStoreAmount(promotionDetail.getStoreDiscount().add(promotionDetail.getGoodsDiscount()));

            //记录佣金比例详情
            commissionAmount = commissionAmount.add(curCommission);
            commissionDTO.getCommissionDetail().add(commissionDetailDTO);

            //商品详情
            GoodsDetailDTO goodsDetail = new GoodsDetailDTO();
            goodsDetail.setName(orderDetail.getName());
            goodsDetail.setGoodsPrice(orderDetail.getGoodsPrice());
            goodsDetail.setGoodsNum(orderDetail.getGoodsNum());
            goodsDetail.setPayPrice(orderDetail.getPayPrice());
            goodsDetail.setPromPlatformAmount(promotionDetail.getPlatformDiscount());
            goodsDetail.setPromStoreAmount(promotionDetail.getStoreDiscount().add(promotionDetail.getGoodsDiscount()));
            goodsDetail.setBrokerageRatio(orderDetail.getBrokerageRatio());
            commissionDTO.getGoodsDetail().add(goodsDetail);
        }
        commissionDTO.setCommissionTotal(commissionAmount);
        return commissionDTO;
    }

    private Date getLastSettlementEndDate(StoreInfoDetailDTO storeDTO) {
        // 查询上一次的结算记录
        EntityWrapper<StoreSettlementEntity> cond = new EntityWrapper<>();
        cond.eq("storeId", storeDTO.getId());
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.orderBy("id", false);
        RowBounds page = new RowBounds(0, 1);
        List<StoreSettlementEntity> lastSettlements = settlementDao.selectPage(page, cond);
        if (CollectionUtils.isNotEmpty(lastSettlements)) {
            return lastSettlements.get(0).getSettleEndDate();
        } else {
            Date lastDay = getLastSettlementDate(storeDTO.getSettleCycle());
            if(lastDay == null) {
                log.error("店铺{}结算周期:{}", storeDTO.getId(), storeDTO.getSettleCycle());
                throw new GlobalException(CREATE_SETTLEMENT_FAILURE);
            }

            return lastDay;
        }
    }

    /**
     * 获取上次结算时间
     * @param settleCycle
     * @return
     */
    private static Date getLastSettlementDate(String settleCycle) {
        if (MerchantConstants.SettleCycle.SETTLECYCLE_MONTH.getSettleCycle().equals(settleCycle)) {
            //上个月1号
            return LocalDate.now().minusMonths(1).withDayOfMonth(1).toDate();
        } else if (MerchantConstants.SettleCycle.SETTLECYCLE_HALFMONTH.getSettleCycle().equals(settleCycle)) {
            if (DateUtils.isMonthFirstDay(new Date())) {
                //上个月16号
                return LocalDate.now().minusMonths(1).withDayOfMonth(16).toDate();
            } else {
                //本月1号
                return LocalDate.now().withDayOfMonth(1).toDate();
            }
        } else if (MerchantConstants.SettleCycle.SETTLECYCLE_WEEK.getSettleCycle().equals(settleCycle)) {
            //上周一
            if (DateUtils.isWeekFirstDay(new Date())) {
                return LocalDate.now().minusWeeks(1).withDayOfWeek(1).toDate();
            } else{
                return LocalDate.now().withDayOfWeek(1).toDate();
            }
        } else if (MerchantConstants.SettleCycle.SETTLECYCLE_FIVEDAY.getSettleCycle().equals(settleCycle)) {
            return LocalDate.now().minusDays(5).toDate();
        } else {
            return null;
        }
    }

    private BigDecimal calcRefundCommission(List<StoreSettlementRefundOrderEntity> list) {
        BigDecimal refundCommission = BigDecimal.ZERO;
        for (StoreSettlementRefundOrderEntity entity : list) {
            refundCommission = refundCommission.add(entity.getPlatformCommission());
        }
        return refundCommission;
    }

    private String packDetail(List<SettlementOrderDetailDTO> orderDetails) {
        //本单佣金
        GoodsCommissionDTO commission = getCommissionInfo(orderDetails);
        //返回商品详情
        return getGoodsDetail(commission.getGoodsDetail());
    }

}