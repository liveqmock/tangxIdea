package com.topaiebiz.member.point.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.po.RedressAssetPo;
import com.topaiebiz.member.point.service.MemberPointService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */


/**
 * Description：会员积分（余额）管理控制层
 * <p>
 * Author ward.wang
 * <p>
 * Date 2018年04月26日 下午4:53:39
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@RestController
@RequestMapping("/member/point/mgmt")
public class PoingMgmtController {

    @Autowired
    private MemberPointService pointService;

    @RequestMapping(path = "/reviseAsset", method = RequestMethod.POST)
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台修正资产数据")
    public ResponseInfo redressAssetData(@RequestBody RedressAssetPo redressAssetPo) {
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        return new ResponseInfo(pointService.redressAssetData(redressAssetPo));
    }
}
