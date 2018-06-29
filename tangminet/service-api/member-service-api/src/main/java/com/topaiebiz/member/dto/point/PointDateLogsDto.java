package com.topaiebiz.member.dto.point;

import lombok.Data;

import java.util.List;

/**
 * Created by ward on 2018-02-07.
 */
@Data
public class PointDateLogsDto {

    private List<PointLogDto> showList;

    private String showDate;
}
