package com.topaiebiz.giftcard.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.topaiebiz.giftcard.entity.GiftcardCarousel;
import com.topaiebiz.giftcard.entity.GiftcardOrder;
import com.topaiebiz.giftcard.vo.GiftcardOrderReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 礼卡订单dao
 * @author: Jeff Chen
 * @date: created in 上午10:53 2018/1/12
 */
@Mapper
public interface GiftcardOrderDao extends BaseMapper<GiftcardOrder>{

    /**
     * 分页查询订单
     * @param page
     * @param giftcardOrderReq
     * @return
     */
    List<GiftcardOrder> queryOrders(Page page, GiftcardOrderReq giftcardOrderReq);

    /**
     * 按订单id查询
     * @param orderId
     * @return
     */
    GiftcardOrder queryOrderById(@Param("orderId") Long orderId);

    /**
     * 按条件查询需要导出的订单数据
     * @param giftcardOrderReq
     * @return
     */
    List<GiftcardOrder> queryOrdersForExport(GiftcardOrderReq giftcardOrderReq);

    /**
     * 查询指定用户和礼卡批次的购买数量
     * @param giftcardOrder
     * @return
     */
    Integer queryValidOrderByMemberAndGiftcard(GiftcardOrder giftcardOrder);

}
