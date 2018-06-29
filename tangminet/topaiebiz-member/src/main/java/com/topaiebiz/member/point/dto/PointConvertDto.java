package com.topaiebiz.member.point.dto;

import lombok.Data;

/**
 * Created by ward on 2018-01-30.
 */
@Data
public class PointConvertDto {

    private Long memberId;

    private String userName;

    private String telephone;

    /**
     * 待转换贝因美crm积分
     */
    private Integer waitConvertPoint;

}
