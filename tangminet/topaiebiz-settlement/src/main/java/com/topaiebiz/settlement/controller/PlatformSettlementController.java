package com.topaiebiz.settlement.controller;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.settlement.dto.SettlementDTO;
import com.topaiebiz.settlement.dto.SettlementDetailDTO;
import com.topaiebiz.settlement.dto.SettlementOrderDTO;
import com.topaiebiz.settlement.dto.SettlementRefundOrderDTO;
import com.topaiebiz.settlement.po.SettlementOrderQueryPO;
import com.topaiebiz.settlement.po.SettlementQueryPO;
import com.topaiebiz.settlement.po.SettlementRefundOrderQueryPO;
import com.topaiebiz.settlement.service.StoreSettlementService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(value = "/settlement/platform/", method = RequestMethod.POST)
public class PlatformSettlementController {

    @Autowired
    private StoreSettlementService storeSettlementService;

    /**
     * 平台端-店铺销售结算。
     *
     * @param queryPO
     * @return
     */
    @RequestMapping(path = "/getSettlementList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台-结算列表")
    public ResponseInfo getSettlementList(@RequestBody SettlementQueryPO queryPO) {
        PageInfo<SettlementDTO> result = storeSettlementService.getSettlementList(queryPO);
        return new ResponseInfo(result);
    }

    /**
     * 平台端-结算订单详情。
     *
     * @param queryPO
     * @return
     */
    @RequestMapping(path = "/getSettlementOrderList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台-结算订单列表")
    public ResponseInfo getSettlementOrderList(@RequestBody SettlementOrderQueryPO queryPO) {
        PageInfo<SettlementOrderDTO> result = storeSettlementService.getSettlementOrderList(queryPO);
        return new ResponseInfo(result);
    }

    /**
     * 平台端-结算售后订单详情。
     *
     * @param queryPO
     * @return
     */
    @RequestMapping(path = "/getSettlementRefundList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台-结算售后订单列表")
    public ResponseInfo getSettlementRefundList(@RequestBody SettlementRefundOrderQueryPO queryPO) {
        PageInfo<SettlementRefundOrderDTO> result = storeSettlementService.getSettlementRefundList(queryPO);
        return new ResponseInfo(result);
    }

    /**
     * 平台端-店铺销售结算导出。
     *
     * @param response
     * @param queryPO
     * @throws IOException
     */
    @RequestMapping(value = "/export/downloadExport", method = RequestMethod.POST)
    @PermissionController(value = PermitType.PLATFORM, operationName = "导出店铺结算数据")
    public void downloadExport(HttpServletResponse response, @RequestBody SettlementQueryPO queryPO) throws IOException {
        storeSettlementService.downloadExportData(response, queryPO);
    }

    /**
     * 平台端-结算订单详情导出。
     *
     * @param response
     * @param queryPO
     * @throws IOException
     */
    @RequestMapping(value = "/orderExport/downloadExport", method = RequestMethod.POST)
    @PermissionController(value = PermitType.PLATFORM, operationName = "导出订单明细数据")
    public void downloadOrderExport(HttpServletResponse response, @RequestBody SettlementOrderQueryPO queryPO) throws IOException {
        storeSettlementService.downloadOrderExportData(response, queryPO);
    }

    /**
     * 平台端-结算售后订单详情导出。
     *
     * @param response
     * @param queryPO
     * @throws IOException
     */
    @RequestMapping(value = "/refundExport/downloadExport", method = RequestMethod.POST)
    @PermissionController(value = PermitType.PLATFORM, operationName = "导出售后订单明细数据")
    public void downloadRefundExport(HttpServletResponse response, @RequestBody SettlementRefundOrderQueryPO queryPO) throws IOException {
        storeSettlementService.downloadRefundExportData(response, queryPO);
    }

    /**
     * 平台端-结算明细打印 。
     *
     * @param id
     * @return
     */
    @RequestMapping(path = "/getSettlementDetail/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台-结算详情")
    public ResponseInfo getSettlementDetail(@PathVariable Long id) {
        SettlementDetailDTO result = storeSettlementService.getSettlementDetail(id);
        return new ResponseInfo(result);
    }

    /**
     * 平台端-商务审核 。
     *
     * @param id
     * @return
     */
    @RequestMapping(path = "/commerceCheck/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台-商务审核")
    public ResponseInfo commerceCheck(@PathVariable Long id) {
        Boolean result = storeSettlementService.commerceCheck(id);
        return new ResponseInfo(result);
    }

    /**
     * 商家端-结算 。
     *
     * @param id
     * @return
     */
    @RequestMapping(path = "/settleAccounts/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台-结算")
    public ResponseInfo settleAccounts(@PathVariable Long id) {
        Boolean result = storeSettlementService.settleAccounts(id);
        return new ResponseInfo(result);
    }
}
