package com.topaiebiz.promotion.mgmt.entity.box;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

@TableName("t_pro_promotion_box_record")
@Data
public class AwardRecordEntity extends BaseBizEntity<Long> {
    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = -8323787704772598451L;
    /**
     * 会员ID
     */
    private Long memberId;
    /**
     * 活动配置ID
     */
    private Long promotionBoxId;
    /**
     * 活动ID
     */
    private Long promotionId;
    /**
     * 活动名称
     */
    private String promotionName;
    /**
     * 奖品名称
     */
    private String awardName;

    /**
     * 奖品类型
     */
    private Integer awardType;

    /**
     * 宝箱领取状态。（0 未领取，1 已领取）
     */
    private Integer state;

    /**
     * 实物宝箱内容(JSON串)
     */
    private String content;
}
