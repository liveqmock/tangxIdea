package com.topaiebiz.member.vo;

import com.topaiebiz.member.dto.point.PointCrmLogDto;
import lombok.Data;

import java.util.List;

/**
 * Created by ward on 2018-02-06.
 */
@Data
public class PointCrmLogReturnVo {
    private List<PointCrmLogDto> list;

    private Long lastLogId;
}
