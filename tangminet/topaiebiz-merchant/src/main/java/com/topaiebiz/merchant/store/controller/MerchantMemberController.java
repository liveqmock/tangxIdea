package com.topaiebiz.merchant.store.controller;

import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.member.po.MemberFilterPo;
import com.topaiebiz.merchant.store.service.MerchantMemberService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员关注店铺下单记录表
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/20 0020 下午 1:29
 */
@RestController
@RequestMapping(value = "/merchant/merchantmember/", method = RequestMethod.POST)
public class MerchantMemberController {

    @Autowired
    private MerchantMemberService merchantMemberService;


    @PermissionController(value = PermitType.MERCHANT,operationName = "店铺会员管理")
    @RequestMapping(path = "/getMerchantMemberList")
    public ResponseInfo getMerchantMemberList(@RequestBody MemberFilterPo memberFilterPo){
        int pageNo = memberFilterPo.getPageNo();
        int pageSize = memberFilterPo.getPageSize();
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(pageNo);
        pagePO.setPageSize(pageSize);
    return new ResponseInfo(merchantMemberService.getMerchantMerchantMemberList(pagePO,memberFilterPo));
    }
}
