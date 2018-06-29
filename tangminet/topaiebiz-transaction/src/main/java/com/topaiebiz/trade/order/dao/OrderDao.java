package com.topaiebiz.trade.order.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.trade.order.dto.page.OrderPageParamDto;
import com.topaiebiz.trade.order.dto.store.StoreOrderPageParamsDTO;
import com.topaiebiz.trade.order.dto.store.statistics.ExportDailyDataDTO;
import com.topaiebiz.trade.order.dto.store.statistics.MemberOrderCountDTO;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description 订单DAO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/12 9:31
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface OrderDao extends BaseDao<OrderEntity> {


    List<OrderEntity> queryOrders(Page<OrderEntity> pageDTOPage, OrderPageParamDto paramDto);

    List<OrderEntity> queryStoreOrders(Page<OrderEntity> page, StoreOrderPageParamsDTO paramsDTO);

    /**
     * Description: 查询今日用户 订单 相关信息
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/13
     *
     * @param:
     **/
    List<MemberOrderCountDTO> queryTodayOrderGroupMember(Page<MemberOrderCountDTO> page, @Param("storeId") Long storeId);


    /**
     * Description: 日常数据导出
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/24
     *
     * @param:
     **/
    List<ExportDailyDataDTO> queryDailyDate(@Param("days") Integer date);
}
