package com.topaiebiz.trade.order.service;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.trade.dto.order.OrderAddressDTO;
import com.topaiebiz.trade.dto.order.OrderDeliveryDTO;
import com.topaiebiz.trade.dto.order.openapi.OrderExpressDTO;
import com.topaiebiz.trade.order.dto.page.OrderPageParamDto;
import com.topaiebiz.trade.order.dto.store.ShipGoodsDTO;
import com.topaiebiz.trade.order.dto.store.StoreOrderDetailDTO;
import com.topaiebiz.trade.order.dto.store.StoreOrderPageDTO;
import com.topaiebiz.trade.order.dto.store.StoreOrderPageParamsDTO;
import com.topaiebiz.trade.order.dto.store.statistics.OrderStatisticsDTO;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
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
public interface StoreOrderService {

    /**
     * Description: 商家--订单分页
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/12
     *
     * @param:
     **/
    PageInfo<StoreOrderPageDTO> queryStoreOrders(StoreOrderPageParamsDTO pageParamsDTO);

    /**
     * Description: 商家--平台--查询订单详情
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/12
     *
     * @param:
     **/
    StoreOrderDetailDTO queryOrderDetail(Long orderId);

    /**
     * Description: 查询待发货的商品
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/18
     *
     * @param:
     **/
    List<ShipGoodsDTO> queryShipGoods(Long orderId);

    /**
     * Description: 发货
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/13
     *
     * @param:
     **/
    Boolean shipOrder(OrderDeliveryDTO orderDeliveryDTO);

    /**
     * Description: api-发货
     * <p>
     * Author: hxpeng
     * createTime: 2018/5/7
     *
     * @param:
     **/
    Boolean orderDeliveryApi(OrderExpressDTO orderExpressDTO);

    /**
     * Description: 手动输入运费
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/25
     *
     * @param:
     **/
    Boolean updateFreight(Long orderId, BigDecimal freight);

    /**
     * Description: 获取收货地址
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/26
     *
     * @param:
     **/
    OrderAddressDTO queryOrderAddress(Long orderId);


    /**
     * Description: 商家首页订单统计信息
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/13
     *
     * @param:
     **/
    OrderStatisticsDTO storeIndex();

    /**
     * Description: 拼装导出参数
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/9
     *
     * @param:
     **/
    String buildOrderExportParas(StoreOrderPageParamsDTO paramDto);

    /**
     * Description: 下载导出数据
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/9
     *
     * @param:
     **/
    void downloadExportData(HttpServletResponse response, String storeId);

    /**
     * Description: 修改物流信息
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/16
     *
     * @param:
     **/
    boolean updateExpress(OrderDeliveryDTO orderDeliveryDTO);

    /**
     * Description: TODO
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/17
     *
     * @param:
     **/
    Boolean addRemark(Long orderId, String remarks);

}
