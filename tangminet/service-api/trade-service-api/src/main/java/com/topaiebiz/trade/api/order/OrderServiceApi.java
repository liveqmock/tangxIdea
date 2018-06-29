package com.topaiebiz.trade.api.order;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.trade.dto.order.*;
import com.topaiebiz.trade.dto.order.openapi.APIOrderDetailDTO;
import com.topaiebiz.trade.dto.order.openapi.OrderExpressDTO;
import com.topaiebiz.trade.dto.order.params.OrderQueryParams;
import com.topaiebiz.trade.dto.settlement.SettlementOrderDetailDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description 订单open api
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/9 13:06
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface OrderServiceApi {


    /**
     * Description: 评论完成，修改订单明细评论标识
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/9
     *
     * @param:
     **/
    boolean orderEvaluated(Long orderId);

    /**
     * Description: 订单发货
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/26
     *
     * @param:
     **/
    boolean orderShip(OrderExpressDTO orderExpressDTO);


    /**
     * Description: 订单详情
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/1
     *
     * @param:
     **/
    OrderDetailDTO orderDetail(Long orderId);

    /**
     * Description: 批量订单查询
     *
     * @Author: hxpeng
     * createTime: 2018/6/1
     * @param:
     **/
    Map<Long, GuiderOrderDetailDTO> guiderOrderDetailGroupById(List<Long> orderIds);

    /**
     * Description: 根据支付ID 查询订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/16
     *
     * @param:
     **/
    PushOrderParamsDTO queryOrdersByPayId(Long payId);

    /**
     * Description: 根据订单ID查询订单地址
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/16
     *
     * @param:
     **/
    OrderAddressDTO queryOrderAddressById(Long orderId);

    /**
     * Description: 根据订单ID查询支付信息
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/16
     *
     * @param:
     **/
    OrderPayDTO queryOrderPayInfoById(Long payId);


    /**
     * Description: 保存报关结果
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/17
     *
     * @param:
     **/
    boolean saveOrderReportCustomsResult(OrderCustomsResultDTO orderCustomsResultDTO);

    /**
     * 查询已经完成订单列表
     *
     * @param storeId      店铺ID
     * @param start        起始时间
     * @param end          结束时间
     * @param startOrderId 其实订单ID
     * @param pageSize     记录条数
     * @return
     */
    List<OrderDTO> queryFinishedOrders(Long storeId, Date start, Date end, Long startOrderId, Integer pageSize);

    /**
     * 根据订单ID批量查询订单详情，并按订单ID分组返回
     *
     * @param orderIds
     * @return
     */
    Map<Long, List<SettlementOrderDetailDTO>> querySettlementOrderDetails(List<Long> orderIds);

    /**
     * Description: open api 查询订单分页
     * <p>
     * Author: hxpeng
     * createTime: 2018/5/7
     *
     * @param:
     **/
    PageInfo<APIOrderDetailDTO> queryOrderPageOpenApi(OrderQueryParams orderQueryParams);

    /**
     * Description: 查询用户有效的订单数量
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/24
     *
     * @param:
     **/
    Integer queryMemberValidOrderCounts(Long memberId);

}