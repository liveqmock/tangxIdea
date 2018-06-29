package com.topaiebiz.member.address.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.member.address.service.MemberAddressService;
import com.topaiebiz.member.dto.address.MemberAddressDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Description：会员收货地址控制层
 * <p>
 * Author Scott.Yang
 * <p>
 * Date 2017年10月13日 下午8:20:57
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@MemberLogin
@RestController
@RequestMapping("/member/address")
public class MemberAddressController {

    @Autowired
    private MemberAddressService memberAddressService;

    /**
     * Description：添加会员收货地址
     * <p>
     * Author Scott.Yang
     *
     * @param memberAddressDto 会员收货地址Dto
     * @param result
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseInfo addMemberAddress(@RequestBody @Valid MemberAddressDto memberAddressDto, BindingResult result) {
        Long memberId = MemberContext.getMemberId();
        if (result.hasErrors()) {
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        memberAddressDto.setMemberId(memberId);
        return new ResponseInfo(memberAddressService.addMemberAddress(memberAddressDto));
    }

    /**
     * Description：修改会员收货地址
     * <p>
     * Author Scott.Yang
     *
     * @param memberAddressDto 会员收货地址Dto
     * @param result
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public ResponseInfo editMemberAddress(@RequestBody @Valid MemberAddressDto memberAddressDto, BindingResult result) {
        if (result.hasErrors()) {
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        memberAddressDto.setMemberId(MemberContext.getMemberId());
        return new ResponseInfo(memberAddressService.modifyMemberAddress(memberAddressDto));
    }

    /**
     * Description： 根据id查询收货地址信息
     * <p>
     * Author Scott.Yang
     *
     * @param addressId 收货编号ID
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/detail/{addressId}", method = RequestMethod.POST)
    public ResponseInfo findMemberAddressById(@PathVariable Long addressId) {
        return new ResponseInfo(memberAddressService.findMemberAddress(MemberContext.getMemberId(), addressId));
    }

    /**
     * Description： 删除会员收货地址
     * <p>
     * Author Scott.Yang
     *
     * @param addressId 会员收货地址ID
     * @return
     */
    @MemberLogin
    @RequestMapping(path = "/remove/{addressId}", method = RequestMethod.POST)
    public ResponseInfo removeMemberAddress(@PathVariable Long addressId) {
        return new ResponseInfo(memberAddressService.removeAddress(MemberContext.getMemberId(), addressId));
    }

    /**
     * Description： 会员收货地址列表
     * <p>
     * Author Scott.Yang
     *
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/lists", method = RequestMethod.POST)
    public ResponseInfo getMemberAddressList() {
        return new ResponseInfo(memberAddressService.getMemberAddressList(MemberContext.getMemberId()));
    }


    @MemberLogin
    @RequestMapping(path = "/default/{addressId}", method = RequestMethod.POST)
    public ResponseInfo setDefaultAddress(@PathVariable Long addressId) {
        return new ResponseInfo(memberAddressService.setDefaultAddress(MemberContext.getMemberId(), addressId));
    }
}
