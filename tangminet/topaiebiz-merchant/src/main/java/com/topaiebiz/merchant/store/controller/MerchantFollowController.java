package com.topaiebiz.merchant.store.controller;

import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.merchant.store.dto.MerchantFollowDto;
import com.topaiebiz.merchant.store.service.MerchantFollowService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Aurthor:zhaoxupeng
 * @Description: 店铺关注controller
 * @Date 2018/1/19 0019 上午 10:27
 */
@RestController
@RequestMapping(value = "/merchant/merchantfollow/", method = RequestMethod.POST)
public class MerchantFollowController {

    @Autowired
    private MerchantFollowService merchantFollowService;

    /**
     * 关注店铺
     */
    @MemberLogin
    @RequestMapping(path = "/addMerchantFollow")
    public ResponseInfo insertMerchantFollow(@RequestBody MerchantFollowDto merchantFollowDto, BindingResult result) {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        merchantFollowDto.setMemberId(memberId);
        return new ResponseInfo(merchantFollowService.saveMerchantFollow(merchantFollowDto));
    }

    /**
     * 根据会员id查询所关注的店铺（商家）
     * @return
     */
    @MemberLogin
    @RequestMapping(path = "/selectMerchantFollowList")
    public ResponseInfo selectMerchantFollowistList(@RequestBody MerchantFollowDto merchantFollowDto) {
         Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        merchantFollowDto.setMemberId(memberId);
        int pageNo = merchantFollowDto.getPageNo();
        int pageSize = merchantFollowDto.getPageSize();
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(pageNo);
        pagePO.setPageSize(pageSize);
        return new ResponseInfo(merchantFollowService.selectMerchantFollowList(pagePO,merchantFollowDto));
    }

    /**
     * 根据店铺id查询店铺详情
     * @param storeId
     * @return
     */
    @MemberLogin
    @RequestMapping(path = "/selectMerchantFollowDetailsList/{storeId}")
    public ResponseInfo selectMerchantFollowDetailsList(@PathVariable Long storeId){
      MerchantFollowDto merchantFollowDto=  merchantFollowService.selectMerchantFollowDetails(storeId);
        return new ResponseInfo(merchantFollowDto);
    }


    /**
     *  取消/删除商家店铺关注信息
     * @param merchantFollowDto
     * @return
     */
    @MemberLogin
    @RequestMapping(path="/cancelFollowByMemberIdAndStoreId")
    public ResponseInfo cancelFollowByMemberIdAndStoreId(@RequestBody MerchantFollowDto merchantFollowDto){
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        merchantFollowDto.setMemberId(memberId);
        return new ResponseInfo(merchantFollowService.removeMerchantFollowById(merchantFollowDto));
    }

    /**
     * 判断是否关注
     * @param merchantFollowDto
     * @return
     */
    @MemberLogin
    @RequestMapping(path="/checkFollowByMemberIdAndStoreId")
    public ResponseInfo checkFollowByMemberIdAndStoreId(@RequestBody MerchantFollowDto merchantFollowDto){
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        merchantFollowDto.setMemberId(memberId);
        System.out.println(memberId);
        return new ResponseInfo(merchantFollowService.checkMerchantFollowById(merchantFollowDto));
    }
}
