package com.topaiebiz.member.po;

import lombok.Data;
import org.joda.time.DateTime;
import com.nebulapaas.base.po.PagePO;

/**
 * Created by ward on 2018-01-19.
 */
@Data
public class MemberFilterPo extends PagePO {

    private Long memberId;

    private String userName;

    private String nickName;

    private String realName;

    private String telephone;

    private Long gradeId;

    private DateTime registerStarTime;

    private DateTime registerEndTime;
}
