package com.topaiebiz.member.vo;

import com.topaiebiz.member.dto.point.PointDateLogsDto;
import lombok.Data;

import java.util.List;

/**
 * Created by ward on 2018-02-06.
 */
@Data
public class PointHistoryReturnVo {

    private List<PointDateLogsDto> list;

    private Long lastLogId;
}
