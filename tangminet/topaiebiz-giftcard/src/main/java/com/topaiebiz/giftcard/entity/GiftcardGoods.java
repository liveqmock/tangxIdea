package com.topaiebiz.giftcard.entity;

import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 礼卡适用的商品
 * @author: Jeff Chen
 * @date: created in 上午10:06 2018/4/19
 */
@TableName("t_giftcard_goods")
public class GiftcardGoods implements Serializable {

    /**
     * id
     */
    private Long id;
    /**
     * 批次id
     */
    private Long batchId;
    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * 创建时间
     */
    private Date createdTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}

