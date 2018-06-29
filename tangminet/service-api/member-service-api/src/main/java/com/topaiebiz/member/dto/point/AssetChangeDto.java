package com.topaiebiz.member.dto.point;

import com.topaiebiz.member.constants.AssetOperateType;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by ward on 2018-01-18.
 */
@Data
public class AssetChangeDto {

    private Long memberId;

    private String userName;


    private String telephone;

    /**
     * 余额变化额度
     **/
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 积分变化额度  负数-表示扣除  正数+表示增加
     **/
    private Integer point = 0;

    /**
     * 操作类型code（）
     */
    private AssetOperateType operateType;


    /**
     * 交易单号。。。等 唯一标识  用于解决幂等性
     */
    private String operateSn = "";

    private String memo;
}
