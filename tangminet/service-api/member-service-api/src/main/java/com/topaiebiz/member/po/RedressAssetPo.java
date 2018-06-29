package com.topaiebiz.member.po;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by ward on 2018-04-26.
 */
@Data
public class RedressAssetPo {

    public Long memberId;

    /**
     * 登录名（用户名或者手机号）
     */
    public String loginAccount;

    public BigDecimal balance = BigDecimal.ZERO;

    public Integer point = 0;

    public String redressMemo;

    public String operateSn;
}
