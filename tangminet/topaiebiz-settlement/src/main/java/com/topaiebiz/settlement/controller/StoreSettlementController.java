package com.topaiebiz.settlement.controller;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.settlement.dto.SettlementDTO;
import com.topaiebiz.settlement.dto.SettlementOrderDTO;
import com.topaiebiz.settlement.dto.SettlementRefundOrderDTO;
import com.topaiebiz.settlement.po.SettlementOrderQueryPO;
import com.topaiebiz.settlement.po.SettlementQueryPO;
import com.topaiebiz.settlement.po.SettlementRefundOrderQueryPO;
import com.topaiebiz.settlement.service.StoreSettlementService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.util.SecurityContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.topaiebiz.settlement.exception.StoreSettlementExceptionEnum.MERCHANT_ID_NOT_EXIST;

/***
 * @author yfeng
 * @date 2018-01-29 16:31
 */
@Slf4j
@RestController
@RequestMapping(value = "/settlement/store/", method = RequestMethod.POST)
public class StoreSettlementController {

    @Autowired
    private StoreSettlementService storeSettlementService;

    /**
     * 商家端-店铺销售结算列表 。
     *
     * @param storeQueryPO
     * @return
     */
    @RequestMapping(path = "/getSettlementList")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家-结算列表")
    public ResponseInfo getSettlementList(@RequestBody SettlementQueryPO storeQueryPO) {
        //获取商家ID
        Long merchantId = getMerchantId();
        log.warn("merchantId {}", merchantId);
        storeQueryPO.setMerchantId(merchantId);

        PageInfo<SettlementDTO> result = storeSettlementService.getSettlementList(storeQueryPO);
        return new ResponseInfo(result);
    }

    /**
     * 商家端-结算订单列表。
     *
     * @param storeQueryPO
     * @return
     */
    @RequestMapping(path = "/getSettlementOrderList")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家-结算订单明细")
    public ResponseInfo getSettlementOrderList(@RequestBody SettlementOrderQueryPO storeQueryPO) {
        //获取商家ID
        Long merchantId = getMerchantId();
        log.warn("merchantId {}", merchantId);

        PageInfo<SettlementOrderDTO> result = storeSettlementService.getSettlementOrderList(storeQueryPO);
        return new ResponseInfo(result);
    }

    /**
     * 商家端-分销售后订单列表。
     *
     * @param storeQueryPO
     * @return
     */
    @RequestMapping(path = "/getSettlementRefundList")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家-结算售后订单明细")
    public ResponseInfo getSettlementRefundList(@RequestBody SettlementRefundOrderQueryPO storeQueryPO) {
        //获取商家ID
        Long merchantId = getMerchantId();
        log.warn("merchantId {}", merchantId);

        PageInfo<SettlementRefundOrderDTO> result = storeSettlementService.getSettlementRefundList(storeQueryPO);
        return new ResponseInfo(result);
    }

    /**
     * 商家端-店铺销售结算导出。
     *
     * @param response
     * @param queryPO
     * @throws IOException
     */
    @RequestMapping(value = "/export/downloadExport", method = RequestMethod.POST)
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家-导出订单数据")
    public void downloadExport(HttpServletResponse response, @RequestBody SettlementQueryPO queryPO) throws IOException {
        //获取商家ID
        Long merchantId = getMerchantId();
        log.warn("merchantId {}", merchantId);
        queryPO.setMerchantId(merchantId);
        storeSettlementService.downloadExportData(response, queryPO);
    }

    /**
     * 商家端-结算订单详情导出。
     *
     * @param response
     * @param queryPO
     * @throws IOException
     */
    @RequestMapping(value = "/orderExport/downloadExport", method = RequestMethod.POST)
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家-导出订单明细数据")
    public void downloadOrderExport(HttpServletResponse response, @RequestBody SettlementOrderQueryPO queryPO) throws IOException {
        //获取商家ID
        Long merchantId = getMerchantId();
        log.warn("merchantId {}", merchantId);
        storeSettlementService.downloadOrderExportData(response, queryPO);
    }

    /**
     * 商家端-结算售后订单详情导出。
     *
     * @param response
     * @param queryPO
     * @throws IOException
     */
    @RequestMapping(value = "/refundExport/downloadExport", method = RequestMethod.POST)
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家-导出售后订单明细数据")
    public void downloadRefundExport(HttpServletResponse response, @RequestBody SettlementRefundOrderQueryPO queryPO) throws IOException {
        //获取商家ID
        Long merchantId = getMerchantId();
        log.warn("merchantId {}", merchantId);
        storeSettlementService.downloadRefundExportData(response, queryPO);
    }

    /**
     * 商家端-结算 。
     *
     * @param id
     * @return
     */
    @RequestMapping(path = "/merchantCheck/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家-结算")
    public ResponseInfo settleAccounts(@PathVariable Long id) {
        //获取商家ID
        Long merchantId = getMerchantId();
        log.warn("merchantId {}", merchantId);
        Boolean result = storeSettlementService.merchantCheck(id);
        return new ResponseInfo(result);
    }

    private Long getMerchantId() {
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        if (merchantId == null) {
            log.warn("商家ID不存在！");
            //商家ID不存在
            throw new GlobalException(MERCHANT_ID_NOT_EXIST);
        }

        return merchantId;
    }
}