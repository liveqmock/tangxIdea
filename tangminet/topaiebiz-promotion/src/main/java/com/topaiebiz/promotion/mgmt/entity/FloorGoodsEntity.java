package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;

@TableName("t_pro_floor_goods")
@Data
public class FloorGoodsEntity extends BaseBizEntity<Long> {
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品楼层，比如辣妈育儿宝典
     */
    private String floorCode;
    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * 折扣价
     */
    private BigDecimal discountPrice;
    /**
     * 排序
     */
    private Integer sort;
}
