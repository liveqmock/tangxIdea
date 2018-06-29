package com.topaiebiz.member.test;

import com.topaiebiz.basic.api.DistrictApi;
import com.topaiebiz.member.dto.point.MemberAssetDto;
import com.topaiebiz.member.point.service.MemberPointService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by ward on 2018-01-25.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PointTest {

    @Autowired
    private MemberPointService memberPointService;

    @Autowired
    private DistrictApi districtApi;

    @Test
    public void getMemberAssetTest() {
        districtApi.getDistrict(1L);

        Long memberId = 951392514894483457L;
        MemberAssetDto memberAssetDto = memberPointService.getMemberAsset(memberId);
    }
}
