package com.topaiebiz.member.dto.point;

import lombok.Data;

import java.util.Date;

/**
 * Created by ward on 2018-01-17.
 */
@Data
public class PointCrmLogDto {

    /**
     * ID
     **/
    private Long id;

    /**
     * 会员ID
     **/
    private Long memberId;


    /**
     * 妈妈购积分
     **/
    private Integer mmgPoint;

    private Integer trueMmgPoint;

    /**
     * 积分转换比例
     **/
    private String pointRate;

    /**
     * 贝因美crm积分
     **/
    private Integer crmPoint;

    /**
     * 备注
     */
    private String meno;


    /**
     * 执行消耗时间
     */
    private String executeTime;


    /**
     * 执行状态
     */
    private Integer executeStatus;


    private Date createdTime;


    private Integer afterMmgPoint;


}
