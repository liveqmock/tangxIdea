package com.topaiebiz.giftcard.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 通用数据接收对象
 * @author: Jeff Chen
 * @date: created in 下午3:57 2018/1/25
 */

public class CommonData implements Serializable {
    /**
     * 卡批次id
     */
    private Long batchId;
    /**
     * 卡单元id
     */
    private Long unitId;
    /**
     * 卡精选id
     */
    private Long selectId;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 订单详情id
     */
    private Long itemId;
    /**
     * 日志id
     */
    private Long logId;

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getSelectId() {
        return selectId;
    }

    public void setSelectId(Long selectId) {
        this.selectId = selectId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    private List<Long> ids;

    private List<String> strList;

    private List<Integer> intList;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public List<String> getStrList() {
        return strList;
    }

    public void setStrList(List<String> strList) {
        this.strList = strList;
    }

    public List<Integer> getIntList() {
        return intList;
    }

    public void setIntList(List<Integer> intList) {
        this.intList = intList;
    }
}
