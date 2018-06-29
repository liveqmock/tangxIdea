package com.topaiebiz.settlement.service;


import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.settlement.dto.SettlementDTO;
import com.topaiebiz.settlement.dto.SettlementDetailDTO;
import com.topaiebiz.settlement.dto.SettlementOrderDTO;
import com.topaiebiz.settlement.dto.SettlementRefundOrderDTO;
import com.topaiebiz.settlement.po.SettlementOrderQueryPO;
import com.topaiebiz.settlement.po.SettlementQueryPO;
import com.topaiebiz.settlement.po.SettlementRefundOrderQueryPO;

import javax.servlet.http.HttpServletResponse;

/**
 * Description： 店铺结算的接口。
 * <p>
 * Author Harry
 * <p>
 * Date 2017年10月31日 下午5:09:53
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface StoreSettlementService {

    /**
     * 获取平台端-店铺销售结算列表。
     *
     * @param queryPO
     * @return
     */
    PageInfo<SettlementDTO> getSettlementList(SettlementQueryPO queryPO);

    /**
     * 获取平台端-结算订单详情。
     *
     * @param queryPO
     * @return
     */
    PageInfo<SettlementOrderDTO> getSettlementOrderList(SettlementOrderQueryPO queryPO);

    /**
     * 获取平台端-结算售后订单详情列表。
     *
     * @param queryPO
     * @return
     */
    PageInfo<SettlementRefundOrderDTO> getSettlementRefundList(SettlementRefundOrderQueryPO queryPO);

    /**
     * 下载导出店铺结算数据
     *
     * @param response
     * @param queryPO
     */
    void downloadExportData(HttpServletResponse response, SettlementQueryPO queryPO);

    /**
     * 下载导出订单明细数据
     *
     * @param response
     * @param queryPO
     */
    void downloadOrderExportData(HttpServletResponse response, SettlementOrderQueryPO queryPO);

    /**
     * 下载导出售后订单明细数据
     *
     * @param response
     * @param queryPO
     */
    void downloadRefundExportData(HttpServletResponse response, SettlementRefundOrderQueryPO queryPO);

    /**
     * 店铺结算详情
     *
     * @param id
     * @return
     */
    SettlementDetailDTO getSettlementDetail(Long id);

    /**
     * 商家审核
     *
     * @param id
     * @return
     */
    Boolean merchantCheck(Long id);

    /**
     * 商务审核
     *
     * @param id
     * @return
     */
    Boolean commerceCheck(Long id);

    /**
     * 财务结算
     *
     * @param id
     * @return
     */
    Boolean settleAccounts(Long id);
}