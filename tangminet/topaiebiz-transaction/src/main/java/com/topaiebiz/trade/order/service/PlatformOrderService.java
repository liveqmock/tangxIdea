package com.topaiebiz.trade.order.service;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.trade.dto.order.OrderDetailDTO;
import com.topaiebiz.trade.order.dto.page.OrderPageParamDto;
import com.topaiebiz.trade.order.dto.platform.PlatformOrderPageDTO;
import com.topaiebiz.trade.order.dto.store.statistics.ExportDailyDataDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Description 商家订单service
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/12 9:30
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface PlatformOrderService {

    /**
     *
     * Description: 平台--订单分页
     *
     * Author: hxpeng
     * createTime: 2018/1/12
     *
     * @param:
     **/
    PageInfo<PlatformOrderPageDTO> queryPlatformOrders(OrderPageParamDto paramDto);

    /**
     *
     * Description: 商家--平台--查询订单详情
     *
     * Author: hxpeng
     * createTime: 2018/1/12
     *
     * @param:
     **/
    OrderDetailDTO queryCommonOrderDetail(Long orderId);


    /**
    *
    * Description: 每日订单数据导出
    *
    * Author: hxpeng
    * createTime: 2018/4/24
    *
    * @param:
    **/
    void downloadDailyOrderData(Integer days, HttpServletResponse response);
}
