package com.topaiebiz.member.dto.point;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by ward on 2018-01-20.
 */
@Data
public class MemberAssetDto {

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
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 积分
     **/
    private Integer point = 0;

    /**
     * 备注
     */
    private String meno;
}
