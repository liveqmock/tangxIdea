package com.topaiebiz.promotion.mgmt.entity.box;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

@TableName("t_pro_promotion_box_item")
@Data
public class BoxActivityItemEntity extends BaseBizEntity<Long> {
    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = -2224752370182619892L;
    /**
     * 活动开宝箱ID
     */
    private Long promotionBoxId;
    /**
     * 奖品ID
     */
    private Long awardId;
    /**
     * 奖品类型
     */
    private Integer awardType;
    /**
     * 活动库存，固定不变
     */
    private Integer totalStorage;
    /**
     * 剩余总库存
     */
    private Integer totalStorageRest;
    /**
     * 每天库存配置值，固定不变(实物奖品日库存与总活动库存一致)
     */
    private Integer dayStorage;
    /**
     * 每天库存剩余(实物奖品日库存与总活动剩余库存一致)
     */
    private Integer dayStorageRest;
    /**
     * 实物奖品配置
     */
    private String resContent;
    /**
     * 中奖概率
     */
    private Double awardRate;
}
