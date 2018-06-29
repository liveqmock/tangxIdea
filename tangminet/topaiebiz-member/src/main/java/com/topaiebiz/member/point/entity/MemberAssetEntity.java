package com.topaiebiz.member.point.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by ward on 2018-01-17.
 */
@TableName("t_mem_member_asset")
@Data
public class MemberAssetEntity extends BaseBizEntity<Long> {

    /**
     * ID
     **/
    private Long id;

    /**
     * 会员ID
     **/
    private Long memberId;

    /**
     * 用户名
     **/
    private String userName;

    /**
     * 会员手机号
     **/
    private String telephone;

    /**
     * 余额
     **/
    private BigDecimal balance;

    /**
     * 积分
     **/
    private Integer point;

    /**
     * 备注
     */
    private String meno;

}
