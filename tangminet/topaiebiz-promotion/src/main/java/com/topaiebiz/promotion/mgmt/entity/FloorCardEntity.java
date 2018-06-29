package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 礼卡楼层
 */
@TableName("t_pro_floor_cards")
@Data
public class FloorCardEntity extends BaseBizEntity<Long> {
    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 8395988264983864610L;
    /**
     * 礼卡名称
     */
    private String cardName;
    /**
     * 礼卡楼层，比如礼卡充值送
     */
    private String floorCode;
    /**
     * 礼卡发行ID
     */
    private Long batchId;
    /**
     * 价
     */
    private BigDecimal salePrice;
    /**
     * 排序
     */
    private Integer sort;
}
