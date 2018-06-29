package com.topaiebiz.member.repair.service;

import com.topaiebiz.member.repair.dto.RepairResultDTO;

/**
 * Created by hecaifeng on 2018/6/20.
 */
public interface MemberRepairService {

    RepairResultDTO updateMemberBindAccount(Integer num, Long startId);
}
