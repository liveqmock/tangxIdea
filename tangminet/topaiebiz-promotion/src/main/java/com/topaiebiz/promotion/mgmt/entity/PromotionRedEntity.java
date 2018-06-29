package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 营销红包
 * Created by Joe on 2018/1/6.
 */
@TableName("t_pro_promotion_red")
@Data
public class PromotionRedEntity extends BaseBizEntity<Long>{

    /**
     * 所属营销活动
     */
    private Long promotionId;

    /**
     * 金额
     */
    private BigDecimal sun;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 领取会员
     */
    private Long memberId;

    /**
     * 备注
     */
    private String memo;

}
