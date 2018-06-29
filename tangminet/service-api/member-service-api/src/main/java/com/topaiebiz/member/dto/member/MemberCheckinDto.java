package com.topaiebiz.member.dto.member;

import lombok.Data;

/**
 * Created by ward on 2018-01-17.
 */

@Data
public class MemberCheckinDto {


    /**
     * ID
     **/
    private Long id;

    /**
     * 会员ID
     **/
    private Long memberId;

    /**
     * 签到日期
     */
    private String checkinDate;

    /**
     * 得到的积分
     **/
    private Integer point;

    /**
     * 备注
     */
    private String meno;

}
