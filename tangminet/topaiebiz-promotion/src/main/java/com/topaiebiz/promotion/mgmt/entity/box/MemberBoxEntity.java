package com.topaiebiz.promotion.mgmt.entity.box;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

@TableName("t_pro_member_box")
@Data
public class MemberBoxEntity extends BaseBizEntity<Long> {
    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 2656893823343521533L;
    /**
     * 会员ID
     */
    private Long memberId;
    /**
     * 活动开宝箱编号
     */
    private Long promotionId;
    /**
     * 宝箱数量
     */
    private Integer awardCount;
    /**
     * 宝箱开启数量
     */
    private Integer openCount;
    /**
     * 备注
     */
    private String memo;
}
