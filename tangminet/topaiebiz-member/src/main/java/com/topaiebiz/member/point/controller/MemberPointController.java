package com.topaiebiz.member.point.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.constants.AssetOperateType;
import com.topaiebiz.member.dto.point.AssetChangeDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.member.po.PointHistoryPo;
import com.topaiebiz.member.point.service.MemberPointLogService;
import com.topaiebiz.member.point.service.MemberPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


/**
 * Description： 会员积分控制层
 * <p>
 * <p>
 * Author Scott.Yang
 * <p>
 * Date 2017年10月16日 上午9:28:31
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping("/member/point")
public class MemberPointController {

    @Autowired
    private MemberPointService memberPointService;

    @Autowired
    private MemberPointLogService pointLogService;

    @MemberLogin
    @RequestMapping(path = "/history", method = RequestMethod.POST)
    public ResponseInfo getPointHistory(@RequestBody PointHistoryPo pointHistoryPo) {
        return new ResponseInfo(pointLogService.getPointHistory(MemberContext.getMemberId(), pointHistoryPo));
    }

    @MemberLogin
    @RequestMapping(path = "/crm", method = RequestMethod.POST)
    public ResponseInfo queryCrmPointSum() {
        return new ResponseInfo(memberPointService.getCrmPointSum(MemberContext.getCurrentMemberToken()));
    }

    @MemberLogin
    @RequestMapping(path = "/convertLog/{lastLogId}", method = RequestMethod.POST)
    public ResponseInfo getPointCrmLog(@PathVariable Long lastLogId) {
        return new ResponseInfo(memberPointService.getPointCrmLog(MemberContext.getMemberId(), lastLogId));
    }

    @MemberLogin
    @RequestMapping(path = "/convert/{reduceCrmPoint}", method = RequestMethod.POST)
    public ResponseInfo convertCrmPoint(@PathVariable Integer reduceCrmPoint) {
        return new ResponseInfo(memberPointService.doCrmPointToMmgPoint(MemberContext.getCurrentMemberToken(), reduceCrmPoint));
    }

    @MemberLogin
    @RequestMapping(path = "/mmg", method = RequestMethod.POST)
    public ResponseInfo queryMmgPointSum() {
        return new ResponseInfo(memberPointService.getMmgPointSum(MemberContext.getMemberId()));
    }

    @RequestMapping(path = "/testAsset", method = RequestMethod.GET)
    public ResponseInfo testAsset() {
        AssetChangeDto assetChangeDto = new AssetChangeDto();
        assetChangeDto.setMemberId(951392633924636673L);
        assetChangeDto.setUserName("z2o9gwau");
        assetChangeDto.setTelephone("18757183911");
        assetChangeDto.setBalance(new BigDecimal(36.00));
        assetChangeDto.setPoint(100);
        assetChangeDto.setOperateType(AssetOperateType.REFUND);
        assetChangeDto.setOperateSn("981368826115465217");
        return new ResponseInfo(memberPointService.useOrRollbackAccountAssets(assetChangeDto));
    }
}
