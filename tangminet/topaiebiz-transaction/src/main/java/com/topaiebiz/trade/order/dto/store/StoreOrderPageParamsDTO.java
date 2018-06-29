package com.topaiebiz.trade.order.dto.store;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Description 商家端 订单列表查询条件
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/17 11:38
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
public class StoreOrderPageParamsDTO implements Serializable {

    private static final long serialVersionUID = -368548043942050263L;

    /**
     * 店铺ID,订单ID,订单状态,商品名称,支付ID,用户名称,用户手机号
     */
    private Long storeId;
    private Long orderId;
    private Long payId;
    private String goodsName;
    private String memberName;
    private String memberPhone;
    private Integer orderState;


    /**
     * 订单下单:开始时间-结束时间
     */
    private Date orderCreateTimeStart;
    private Date orderCreateTimeEnd;

    /**
     * 订单完成:开始时间-结束时间
     */
    private Date orderCompleteTimeStart;
    private Date orderCompleteTimeEnd;

    /**
     * 分页
     */
    private PagePO pagePO = new PagePO();

    /**
     * 页面导出分页 当前页,页面大小
     */
    private Integer exportIndex;
    private Integer exportSize;

    /**
     * 单次最大导出数量
     */
    private static final Integer MAX_EXPORT_SIZE = 2000;

    public Integer getExportIndex() {
        if (null == exportIndex) {
            exportIndex = 1;
        }
        return exportIndex;
    }

    public Integer getExportSize() {
        if (null == exportSize) {
            exportSize = MAX_EXPORT_SIZE;
        }
        if (exportSize > MAX_EXPORT_SIZE) {
            exportSize = MAX_EXPORT_SIZE;
        }
        return exportSize;
    }


}
