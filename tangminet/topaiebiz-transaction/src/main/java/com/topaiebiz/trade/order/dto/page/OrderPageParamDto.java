package com.topaiebiz.trade.order.dto.page;

import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description 平台/商家/用户 -- 订单分页查询参数
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/12 9:50
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderPageParamDto implements Serializable {

    private static final long serialVersionUID = -7488752207640200451L;

    /**
     * 单次最大导出数量
     */
    private static final Integer maxExportSize = 2000;

    /**
     * 默认导出数量
     */
    private static final Integer defaultExportSize = 500;


    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单状态
     */
    private Integer orderState;

    /**
     * 订单被锁状态
     */
    private Integer lockState;

    /**
     * 商品名称
     */
    private String goodName;

    /**
     * 下单人手机号
     */
    private String memberPhone;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 订单时间
     */
    private Date orderTime;

    /**
     * 订单查询开始时间
     */
    private Date startTime;

    /**
     * 订单查询结束时间
     */
    private Date endTime;

    /**
     * 订单价格
     */
    private BigDecimal orderPrice;

    /**
     * 分页
     */
    private PagePO pagePO = new PagePO();

    public void setOrderState(Integer orderState) {
        // 查询条件为待发货状态， 则增加筛选
        if (OrderStatusEnum.PENDING_DELIVERY.getCode().equals(orderState)){
            this.lockState = Constants.OrderLockFlag.LOCK_YES;
        }
        this.orderState = orderState;
    }

    /**
     * 页面导出分页 当前页
     */
    private Integer exportIndex;

    /**
     *  页面导出分页 页面大小
     */
    private Integer exportSize;


    public Integer getExportIndex() {
        if (null == exportIndex){
            exportIndex = 1;
        }
        return exportIndex;
    }

    public Integer getExportSize() {
        if (null == exportSize ){
            exportSize = maxExportSize;
        }
        if (exportSize > maxExportSize){
            exportSize = maxExportSize;
        }
        return exportSize;
    }
}
