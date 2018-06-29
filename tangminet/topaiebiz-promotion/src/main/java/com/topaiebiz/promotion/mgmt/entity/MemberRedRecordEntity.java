package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员红包
 * Created by Joe on 2018/1/6.
 */
@TableName("t_pro_member_red_record")
@Data
public class MemberRedRecordEntity extends BaseBizEntity<Long>{

    /**
     * 会员编号
     */
    private Long memberId;

    /**
     * 红包id
     */
    private Long redId;

    /**
     * 金额
     */
    private BigDecimal sum;

    /**
     * 备注
     */
    private String memo;

}
