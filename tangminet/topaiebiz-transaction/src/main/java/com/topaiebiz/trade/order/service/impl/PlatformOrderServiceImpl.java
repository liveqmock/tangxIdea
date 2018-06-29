package com.topaiebiz.trade.order.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.ExportUtil;
import com.nebulapaas.common.PageDataUtil;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.trade.dto.order.OrderDetailDTO;
import com.topaiebiz.trade.order.dao.OrderDao;
import com.topaiebiz.trade.order.dto.page.OrderPageParamDto;
import com.topaiebiz.trade.order.dto.platform.PlatformOrderPageDTO;
import com.topaiebiz.trade.order.dto.store.export.OrderExportDTO;
import com.topaiebiz.trade.order.dto.store.statistics.ExportDailyDataDTO;
import com.topaiebiz.trade.order.service.PlatformOrderService;
import com.topaiebiz.trade.order.util.OrderHelper;
import com.topaiebiz.trade.order.util.OrdersQueryUtil;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/20 13:28
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PlatformOrderServiceImpl implements PlatformOrderService {

    @Autowired
    private OrderHelper orderHelper;

    @Autowired
    private OrdersQueryUtil ordersQueryUtil;

    @Autowired
    private StoreApi storeApi;

    @Autowired
    private OrderDao orderDao;

    @Override
    public PageInfo<PlatformOrderPageDTO> queryPlatformOrders(OrderPageParamDto paramDto) {
        PagePO pagePO = paramDto.getPagePO();
        Page<OrderEntity> orderEntityPage = PageDataUtil.buildPageParam(pagePO);
        PageInfo<PlatformOrderPageDTO> pageDTOPageInfo = PageDataUtil.copyPageInfo(orderEntityPage, PlatformOrderPageDTO.class);

        paramDto.setStoreId(getStoreId(paramDto.getStoreId()));

        // 1：查询订单
        List<OrderEntity> orderEntities = ordersQueryUtil.queryOrders(orderEntityPage, paramDto);
        if (CollectionUtils.isEmpty(orderEntities)) {
            return pageDTOPageInfo;
        }
        orderEntityPage.setRecords(orderEntities);
        pageDTOPageInfo = PageDataUtil.copyPageInfo(orderEntityPage, PlatformOrderPageDTO.class);

        List<Long> orderIds = new ArrayList<>(orderEntities.size());
        orderEntities.forEach(orderEntity -> orderIds.add(orderEntity.getId()));

        // 2：查询订单明细
        Map<Long, List<OrderDetailEntity>> orderDetails = ordersQueryUtil.queryDetailsByOrderIds(orderIds);
        if (!MapUtils.isEmpty(orderDetails)) {
            pageDTOPageInfo.getRecords().forEach(pageDTO -> pageDTO.setOrderPageDetailDTOS(orderHelper.buildOrderDetailInPage(orderDetails.get(pageDTO.getId()))));
        }

        // 3：查询收货信息
        orderHelper.buildPfOrderAddress(orderIds, pageDTOPageInfo.getRecords());

        return pageDTOPageInfo;
    }

    @Override
    public OrderDetailDTO queryCommonOrderDetail(Long orderId) {
        OrderEntity orderEntity = ordersQueryUtil.queryOrder(orderId);
        return orderHelper.buildOrderDetail(orderEntity);
    }

    @Override
    public void downloadDailyOrderData(Integer days, HttpServletResponse response) {

        List<ExportDailyDataDTO> exportDailyDataDTOS = orderDao.queryDailyDate(days);
        if (CollectionUtils.isEmpty(exportDailyDataDTOS)) {
            try {
                String result = "<html><script>alert(暂无数据！)</script></html>";
                response.getOutputStream().write(result.getBytes("utf-8"));
                response.getOutputStream().flush();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String excelHeadColumn = ExportUtil.buildExcelHeadColumn("下单时间,订单号,三级类目,二级类目,一级类目,用户ID," +
                "店铺名称,订单状态,手机号,商品SKUID,商品ItemID,商品名称,商品价格,商品数量,订单金额,平台优惠金额,店铺优惠金额,余额支付金额,礼卡支付金额," +
                "积分支付金额,总支付金额(包含余额支付/礼卡支付/积分支付/现金支付)");
        String excelBodyColumn;
        try {
            excelBodyColumn = ExportUtil.buildExcelBodyColumn(exportDailyDataDTOS, ExportDailyDataDTO.class);
            ExportUtil.setRespProperties("日常订单数据导出", response);
            ExportUtil.doExport(excelHeadColumn, excelBodyColumn, response.getOutputStream(), "GBK");
        } catch (Exception e) {
            log.error(">>>>>>>>>>>download dailt data fail, error:{}", e);
        }
    }


    /**
     * Description:  这里的storeId 是商户ID， 不好修改，由后台开发再调下商户接口查询店铺ID
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/9
     *
     * @param:
     **/
    private Long getStoreId(Long merchantId) {
        if (merchantId != null) {
            StoreInfoDetailDTO storeInfoDetailDTO = storeApi.getStoreByMerchantId(merchantId);
            if (null != storeInfoDetailDTO) {
                return storeInfoDetailDTO.getId();
            }
        }
        return null;
    }


}
