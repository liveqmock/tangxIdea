package com.topaiebiz.giftcard.vo;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

/**
 * @description: 礼卡订单查询请求
 * @author: Jeff Chen
 * @date: created in 下午1:51 2018/1/18
 */
@Data
public class GiftcardOrderReq extends PagePO {


    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 会员名称
     */
    private String memberName;
    /**
     * 订单状态
     */
    private Integer orderStatus;
    /**
     * 标签id
     */
    private Long labelId;
    /**
     * 下单开始时间
     */
    private String orderStart;
    /**
     * 下单结束时间
     */
    private String orderEnd;
    /**
     * 支付开始时间
     */
    private String payStart;
    /**
     * 支付结束时间
     */
    private String payEnd;

    /**
     * 数据位置起始
     */
    private Integer start;
    /**
     * 数据位置结束
     */
    private Integer end;
}
