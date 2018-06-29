package com.topaiebiz.merchant.store.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.merchant.grade.dto.MerchantGradeDto;
import com.topaiebiz.merchant.grade.exception.MerchantGradeException;
import com.topaiebiz.merchant.info.dto.MerchantReturnDto;
import com.topaiebiz.merchant.info.service.MerchantReturnService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商家退货地址controller
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/24 0024 下午 4:56
 */
@RestController
@RequestMapping(value = "/merchant/retnrn/",method = RequestMethod.POST)
public class MerchantReturnController {

    @Autowired
    private MerchantReturnService merchantReturnService;

    @PermissionController(value = PermitType.MERCHANT,operationName = "添加商家退货地址信息")
    @RequestMapping(path = "/insertMerchantReturnInfo")
    public ResponseInfo insertMerchantReturnInfo(@RequestBody @Valid MerchantReturnDto merchantReturnDto, BindingResult result)
            throws GlobalException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        return new ResponseInfo(merchantReturnService.insertMerchantReturnInfo(merchantReturnDto));
    }


    @PermissionController(value = PermitType.MERCHANT,operationName = "回显商家退货地址信息")
    @RequestMapping(path = "/getMerchantGradeBymerchantId")
    public ResponseInfo getMerchantGradeById() throws GlobalException {
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        return new ResponseInfo(merchantReturnService.selectMerchantReturnByMerchantId(merchantId));
    }




}


