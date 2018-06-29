package com.topaiebiz.trade.refund.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.trade.order.dto.store.export.OrderRefundPriceDTO;
import com.topaiebiz.trade.refund.dto.page.CustomerRefundPageDTO;
import com.topaiebiz.trade.refund.dto.page.RefundOrderPageDTO;
import com.topaiebiz.trade.refund.dto.page.RefundPageParamsDTO;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description 售后订单DAO层
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/8 12:53
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface RefundOrderDao extends BaseDao<RefundOrderEntity> {

    /**
     * Description: 获取用户的售后订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/8
     *
     * @param:
     **/
    List<CustomerRefundPageDTO> getCustomerRefundOrderPage(Page<CustomerRefundPageDTO> pageDtoPage, @Param("memberId") Long memberId);

    /**
     * Description: 平台 获取售后订单分页
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/10
     *
     * @param:
     **/
    List<RefundOrderPageDTO> getPlatformRefundOrderPage(Page<RefundOrderPageDTO> pageDtoPage, RefundPageParamsDTO pageParams);

    /**
     * Description: 商家端 获取售后订单分页
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/10
     *
     * @param:
     **/
    List<RefundOrderPageDTO> getStoreRefundOrderPage(Page<RefundOrderPageDTO> pageDtoPage, RefundPageParamsDTO pageParams);

    /**
     * Description: 查询支付订单的退款金额
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/8
     *
     * @param:
     **/
    List<OrderRefundPriceDTO> getRefundPriceByOrderId(@Param("orderIds") List<Long> orderIds, @Param("refundState") Integer refundState);

}

