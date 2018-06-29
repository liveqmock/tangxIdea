package com.topaiebiz.member.dto.member;

import lombok.Data;

import java.io.Serializable;

/***
 * @author wangwei
 * @date 2017-12-28 11:49
 */
@Data
public class MemberTokenDto implements Serializable {

    private static final long serialVersionUID = 2378574364285909770L;

    private String sessionId;

    private Long memberId;

    private String telephone;

    private String userName;


}