package com.topaiebiz.settlement.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.DateUtils;
import com.nebulapaas.common.ExportUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.dto.store.MerchantAccountDTO;
import com.topaiebiz.settlement.contants.Constants.SettlementStateEnum;
import com.topaiebiz.settlement.dao.StoreSettlementDao;
import com.topaiebiz.settlement.dao.StoreSettlementOrderDao;
import com.topaiebiz.settlement.dao.StoreSettlementRefundOrderDao;
import com.topaiebiz.settlement.dto.SettlementDTO;
import com.topaiebiz.settlement.dto.SettlementDetailDTO;
import com.topaiebiz.settlement.dto.SettlementOrderDTO;
import com.topaiebiz.settlement.dto.SettlementRefundOrderDTO;
import com.topaiebiz.settlement.dto.export.SettlementExportDTO;
import com.topaiebiz.settlement.dto.export.SettlementOrderExportDTO;
import com.topaiebiz.settlement.dto.export.SettlementRefundExportDTO;
import com.topaiebiz.settlement.entity.StoreSettlementEntity;
import com.topaiebiz.settlement.entity.StoreSettlementOrderEntity;
import com.topaiebiz.settlement.entity.StoreSettlementRefundOrderEntity;
import com.topaiebiz.settlement.exception.StoreSettlementExceptionEnum;
import com.topaiebiz.settlement.po.SettlementOrderQueryPO;
import com.topaiebiz.settlement.po.SettlementQueryPO;
import com.topaiebiz.settlement.po.SettlementRefundOrderQueryPO;
import com.topaiebiz.settlement.service.StoreSettlementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.topaiebiz.settlement.contants.Constants.EXPORT_PAGE_NO;
import static com.topaiebiz.settlement.contants.Constants.EXPORT_PAGE_SIZE;
import static com.topaiebiz.settlement.contants.Constants.SettlementStateEnum.*;

/***
 * @author yfeng
 * @date 2018-01-29 17:29
 */
@Slf4j
@Service
public class StoreSettlementServiceImpl implements StoreSettlementService {

    @Autowired
    private StoreApi storeApi;
    @Autowired
    private StoreSettlementDao storeSettlementDao;
    @Autowired
    private StoreSettlementOrderDao storeSettlementOrderDao;
    @Autowired
    private StoreSettlementRefundOrderDao storeSettlementRefundOrderDao;
    @Autowired
    private RedisCache redisCache;

    @Override
    public PageInfo<SettlementDTO> getSettlementList(SettlementQueryPO queryPO) {
        log.info("店铺销售结算查询条件 {}", JSON.toJSONString(queryPO));

        PagePO pagePo = new PagePO();
        BeanCopyUtil.copy(queryPO, pagePo);
        Page<StoreSettlementEntity> page = PageDataUtil.buildPageParam(pagePo);

        EntityWrapper<StoreSettlementEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        if (queryPO.getId() != null) {
            cond.eq("id", queryPO.getId());
        }
        if (queryPO.getMerchantId() != null) {
            cond.eq("merchantId", queryPO.getMerchantId());
        }
        if (StringUtils.isNotBlank(queryPO.getStoreName())) {
            cond.like("storeName", StringUtils.join("%", queryPO.getStoreName(), "%"));
        }
        if (StringUtils.isNotBlank(queryPO.getSettleStartDate())) {
            cond.ge("settleTime", queryPO.getSettleStartDate());
        }
        if (StringUtils.isNotBlank(queryPO.getSettleEndDate())) {
            cond.le("settleTime", queryPO.getSettleEndDate());
        }
        if (queryPO.getState() != null) {
            cond.eq("state", queryPO.getState());
        }
        if (StringUtils.isNotBlank(queryPO.getSettleCycle())) {
            cond.eq("SettleCycle", queryPO.getSettleCycle());
        }
        //按照生成时间倒序排序
        cond.orderBy("createdTime", false);

        List<StoreSettlementEntity> datas = storeSettlementDao.selectPage(page, cond);

        page.setRecords(datas);
        return PageDataUtil.copyPageInfo(page, SettlementDTO.class);
    }

    @Override
    public PageInfo<SettlementOrderDTO> getSettlementOrderList(SettlementOrderQueryPO queryPO) {
        log.info("结算订单明细查询条件 {}", JSON.toJSONString(queryPO));

        PagePO pagePo = new PagePO();
        BeanCopyUtil.copy(queryPO, pagePo);
        Page<StoreSettlementOrderEntity> page = PageDataUtil.buildPageParam(pagePo);

        EntityWrapper<StoreSettlementOrderEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        if (queryPO.getOrderId() != null) {
            cond.eq("orderId", queryPO.getOrderId());
        }
        if (queryPO.getSettlementId() != null) {
            cond.eq("settlementId", queryPO.getSettlementId());
        }
        if (queryPO.getMemberId() != null) {
            cond.eq("memberId", queryPO.getMemberId());
        }
        if (StringUtils.isNotBlank(queryPO.getFinishStartTime())) {
            cond.ge("finishTime", queryPO.getFinishStartTime());
        }
        if (StringUtils.isNotBlank(queryPO.getFinishEndTime())) {
            cond.le("finishTime", queryPO.getFinishEndTime());
        }

        List<StoreSettlementOrderEntity> dataList = storeSettlementOrderDao.selectPage(page, cond);

        page.setRecords(dataList);
        return PageDataUtil.copyPageInfo(page, SettlementOrderDTO.class);
    }

    @Override
    public PageInfo<SettlementRefundOrderDTO> getSettlementRefundList(SettlementRefundOrderQueryPO queryPO) {
        log.info("结算售后订单明细查询条件 {}", JSON.toJSONString(queryPO));

        PagePO pagePo = new PagePO();
        BeanCopyUtil.copy(queryPO, pagePo);
        Page<StoreSettlementRefundOrderEntity> page = PageDataUtil.buildPageParam(pagePo);

        EntityWrapper<StoreSettlementRefundOrderEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        if (queryPO.getOrderId() != null) {
            cond.eq("orderId", queryPO.getOrderId());
        }
        if (queryPO.getRefundId() != null) {
            cond.eq("refundId", queryPO.getRefundId());
        }
        if (queryPO.getSettlementId() != null) {
            cond.eq("settlementId", queryPO.getSettlementId());
        }
        if (queryPO.getMemberId() != null) {
            cond.eq("memberId", queryPO.getMemberId());
        }
        if (StringUtils.isNotBlank(queryPO.getFinishStartTime())) {
            cond.ge("finishTime", queryPO.getFinishStartTime());
        }
        if (StringUtils.isNotBlank(queryPO.getFinishEndTime())) {
            cond.le("finishTime", queryPO.getFinishEndTime());
        }
        if (StringUtils.isNotBlank(queryPO.getApplyStartTime())) {
            cond.ge("applyTime", queryPO.getApplyStartTime());
        }
        if (StringUtils.isNotBlank(queryPO.getApplyEndTime())) {
            cond.le("applyTime", queryPO.getApplyEndTime());
        }

        List<StoreSettlementRefundOrderEntity> dataList = storeSettlementRefundOrderDao.selectPage(page, cond);

        page.setRecords(dataList);
        return PageDataUtil.copyPageInfo(page, SettlementRefundOrderDTO.class);
    }

    @Override
    public void downloadExportData(HttpServletResponse response, SettlementQueryPO queryPO) {
        queryPO.setPageNo(EXPORT_PAGE_NO);
        queryPO.setPageSize(EXPORT_PAGE_SIZE);

        log.info("店铺销售结算导出查询条件 {}", JSON.toJSONString(queryPO));

        PageInfo<SettlementDTO> settlementExportPage = this.getSettlementList(queryPO);
        if (CollectionUtils.isEmpty(settlementExportPage.getRecords())) {
            return;
        }
        BeanCopyUtil.Convert<SettlementDTO, SettlementExportDTO> convert = (src, target) -> {
            //结算时间
            target.setSettleTime(DateUtils.parseDateToString(src.getSettleTime(), DateUtils.DATE_TIME_FORMAT));
            //结算开始日期
            target.setSettleStartDate(DateUtils.parseDateToString(src.getSettleStartDate(), DateUtils.DATE_FORMAT));
            //结算结束日期
            target.setSettleEndDate(DateUtils.parseDateToString(src.getSettleEndDate(), DateUtils.DATE_FORMAT));
            target.setState(SettlementStateEnum.getValueByCode(src.getState()));
        };
        List<SettlementExportDTO> exportList = BeanCopyUtil.copyList(settlementExportPage.getRecords(), SettlementExportDTO.class, convert);
        String excelHeadColumn = ExportUtil.buildExcelHeadColumn("结算单号,开始时间,结束时间,商家ID,商家名称,店铺ID,店铺名称,商品总价," +
                "现金,美礼卡,店铺营销抵扣,平台营销抵扣,积分抵扣,平台佣金,退款扣除金额,应结余额,结算周期,财务结算时间,状态");
        String excelBodyColumn;
        try {
            excelBodyColumn = ExportUtil.buildExcelBodyColumn(exportList, SettlementExportDTO.class);
            ExportUtil.setRespProperties(StringUtils.join("店铺结算导出"), response);
            ExportUtil.doExport(excelHeadColumn, excelBodyColumn, response.getOutputStream());
        } catch (Exception e) {
            log.error(StringUtils.join(">>>>>>>>>>导出店铺结算失败：", e.getMessage()), e);
            throw new GlobalException(StoreSettlementExceptionEnum.EXPORT_SETTLEMENT_FAILURE);
        }

    }

    @Override
    public void downloadOrderExportData(HttpServletResponse response, SettlementOrderQueryPO queryPO) {
        queryPO.setPageNo(EXPORT_PAGE_NO);
        queryPO.setPageSize(EXPORT_PAGE_SIZE);

        log.info("结算d订单明细导出查询条件 {}", JSON.toJSONString(queryPO));

        PageInfo<SettlementOrderDTO> exportPage = this.getSettlementOrderList(queryPO);
        if (CollectionUtils.isEmpty(exportPage.getRecords())) {
            return;
        }
        BeanCopyUtil.Convert<SettlementOrderDTO, SettlementOrderExportDTO> convert = (src, target) -> {
            //商品详情
            target.setGoodsDetail(packDetail(src.getGoodsDetail()));
            //完成时间
            target.setFinishTime(DateUtils.parseDateToString(src.getFinishTime(), DateUtils.DATE_TIME_FORMAT));
            //支付时间
            target.setPayTime(DateUtils.parseDateToString(src.getPayTime(), DateUtils.DATE_TIME_FORMAT));
        };

        List<SettlementOrderExportDTO> exportList = BeanCopyUtil.copyList(exportPage.getRecords(), SettlementOrderExportDTO.class, convert);
        String excelHeadColumn = ExportUtil.buildExcelHeadColumn("订单编号,订单完成时间,付款时间,会员ID,商品总价,运费,税费," +
                "店铺营销贴现,平台营销贴现,积分抵扣,美礼卡,余额,现金,支付渠道,第三方流水号,平台佣金,结算金额,商品明细");
        String excelBodyColumn;
        try {
            excelBodyColumn = ExportUtil.buildExcelBodyColumn(exportList, SettlementOrderExportDTO.class);
            ExportUtil.setRespProperties("结算订单详情导出", response);
            ExportUtil.doExport(excelHeadColumn, excelBodyColumn, response.getOutputStream());
        } catch (Exception e) {
            log.error(StringUtils.join(">>>>>>>>>>导出结算订单详情失败：", e.getMessage()), e);
            throw new GlobalException(StoreSettlementExceptionEnum.EXPORT_SETTLEMENT_FAILURE);
        }
    }

    @Override
    public void downloadRefundExportData(HttpServletResponse response, SettlementRefundOrderQueryPO queryPO) {
        queryPO.setPageNo(EXPORT_PAGE_NO);
        queryPO.setPageSize(EXPORT_PAGE_SIZE);

        log.info("结算d订单明细导出查询条件 {}", JSON.toJSONString(queryPO));

        PageInfo<SettlementRefundOrderDTO> settlementExportPage = this.getSettlementRefundList(queryPO);
        if (CollectionUtils.isEmpty(settlementExportPage.getRecords())) {
            return;
        }

        BeanCopyUtil.Convert<SettlementRefundOrderDTO, SettlementRefundExportDTO> convert = (src, target) -> {
            //商品详情
            target.setGoodsDetail(packDetail(src.getGoodsDetail()));
            //完成时间
            target.setFinishTime(DateUtils.parseDateToString(src.getFinishTime(), DateUtils.DATE_TIME_FORMAT));
            //申请售后时间
            target.setApplyTime(DateUtils.parseDateToString(src.getApplyTime(), DateUtils.DATE_TIME_FORMAT));
            if (src.getTax() == null) {
                target.setTax(BigDecimal.ZERO);
            }
        };

        List<SettlementRefundExportDTO> exportList = BeanCopyUtil.copyList(settlementExportPage.getRecords(), SettlementRefundExportDTO.class, convert);
        String excelHeadColumn = ExportUtil.buildExcelHeadColumn("售后订单编号,原订单编号,退款完成时间,退款申请时间,会员ID,申请退款金额,商品总价," +
                "运费,税费,应退店铺营销贴现,应退平台营销贴现,应退积分抵扣,应退美礼卡,应退现金,应退余额,支付渠道,第三方流水号,平台佣金退回,应扣除金额,商品明细");
        String excelBodyColumn;
        try {
            excelBodyColumn = ExportUtil.buildExcelBodyColumn(exportList, SettlementRefundExportDTO.class);
            ExportUtil.setRespProperties("结算售后订单详情导出", response);
            ExportUtil.doExport(excelHeadColumn, excelBodyColumn, response.getOutputStream());
        } catch (Exception e) {
            log.error(StringUtils.join(">>>>>>>>>>导出结算售后订单详情失败：", e.getMessage()), e);
            throw new GlobalException(StoreSettlementExceptionEnum.EXPORT_SETTLEMENT_FAILURE);
        }
    }

    @Override
    public SettlementDetailDTO getSettlementDetail(Long id) {
        StoreSettlementEntity settlement = storeSettlementDao.selectById(id);
        if (settlement == null) {
            return null;
        }

        SettlementDetailDTO settlementDetail = new SettlementDetailDTO();
        BeanCopyUtil.copy(settlement, settlementDetail);
        //积分结算金额
        settlementDetail.setPointSettleSum(settlement.getPointSum().subtract(settlement.getPointRefundSum()));
        //美礼卡结算金额
        settlementDetail.setCardSettleSum(settlement.getCardSum().subtract(settlement.getCardRefundSum()));

        //收款单位账户详情
        MerchantAccountDTO merchantAccount = storeApi.getMerchantAccountInfo(settlement.getMerchantId());
        if (merchantAccount != null) {
            //账号
            settlementDetail.setSettleAccount(merchantAccount.getSettleAccount());
            //开户银行
            settlementDetail.setSettleBankName(merchantAccount.getSettleBankName());
            //收款单位
            settlementDetail.setSettleAccountName(merchantAccount.getSettleAccountName());
            //开户支行
            settlementDetail.setSettleBankNum(merchantAccount.getSettleBankNum());
        }
        return settlementDetail;
    }

    @Override
    public Boolean merchantCheck(Long id) {
        return updateState(id, NO_MERCHANT_CHECK, NO_COMMERCE_CHECK);
    }

    @Override
    public Boolean commerceCheck(Long id) {
        return updateState(id, NO_COMMERCE_CHECK, NO_FINANCE_CHECK);
    }

    @Override
    public Boolean settleAccounts(Long id) {
        return updateState(id, NO_FINANCE_CHECK, FINISHED);
    }

    /**
     * 更改状态
     *
     * @param id
     * @return
     */
    private Boolean updateState(Long id, SettlementStateEnum oldState, SettlementStateEnum newState) {
        StoreSettlementEntity settlement = new StoreSettlementEntity();
        settlement.cleanInit();
        settlement.setId(id);
        settlement.setState(newState.getCode());
        settlement.setLastModifiedTime(new Date());

        //查询条件
        EntityWrapper<StoreSettlementEntity> cond = new EntityWrapper();
        cond.eq("id", id);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("state", oldState.getCode());
        return storeSettlementDao.update(settlement, cond) > 0;
    }

    /**
     * 拼接商品详情
     *
     * @param goodsDetail 商品详情JSON串
     * @return
     */
    private String packDetail(String goodsDetail) {
        //拼接商品属性前的商品集合
        List<List<String>> datas = JSON.parseObject(goodsDetail, new TypeReference<List<List<String>>>() {
        });
        //拼接商品属性后的商品集合
        List<String> rowTexts = new ArrayList<>(datas.size());
        for (List<String> items : datas) {
            String rowText = StringUtils.join(items, "|");
            rowTexts.add(rowText);
        }
        return StringUtils.join(rowTexts, ";");
    }
}