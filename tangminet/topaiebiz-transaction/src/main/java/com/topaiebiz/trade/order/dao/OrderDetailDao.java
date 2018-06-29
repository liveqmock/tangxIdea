package com.topaiebiz.trade.order.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.trade.dto.statics.PromotionStaticsDTO;
import com.topaiebiz.trade.order.dto.store.export.GoodExpressDTO;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description 订单明细DAO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/12 9:31
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface OrderDetailDao extends BaseDao<OrderDetailEntity> {


    /**
     * Description: 统计订单商品使用营销活动ID 得总价 人数 等
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/28
     *
     * @param:
     **/
    List<PromotionStaticsDTO> orderDetailStaticsByPromotionId(@Param("promotionIds") List<Long> promotionIds);


    /**
     * Description: 根据订单ID集合 查询所有的物流信息
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/8
     *
     * @param:
     **/
    List<GoodExpressDTO> getExpressInfoByOrderIds(@Param("orderIds") List<Long> orderIds);

    Integer countSkuHistoryVolume(@Param("memberId") Long memberId,
                                  @Param("promotionId") Long promotionId,
                                  @Param("skuId") Long skuId,
                                  @Param("cancelState") Integer cancelStatus);
}