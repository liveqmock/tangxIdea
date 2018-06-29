package com.topaiebiz.member.vo;

import com.topaiebiz.member.dto.member.MemberCheckinDto;
import lombok.Data;

import java.util.List;

/**
 * Created by ward on 2018-02-06.
 */
@Data
public class CheckinLogReturnVo {
    private List<MemberCheckinDto> list;

    private Long lastLogId;
}
