package com.topaiebiz.merchant.store.controller;

import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.goods.api.BackendCategoryApi;
import com.topaiebiz.goods.constants.GoodsConstants;
import com.topaiebiz.goods.dto.category.backend.BackendCategorysDTO;
import com.topaiebiz.merchant.enter.dto.StoreInfoDto;
import com.topaiebiz.merchant.info.dto.MerchantFrozenDto;
import com.topaiebiz.merchant.info.dto.MerchantInfoDto;
import com.topaiebiz.merchant.info.dto.MerchantInfoGradeDto;
import com.topaiebiz.merchant.info.dto.MerchantInfoListDto;
import com.topaiebiz.merchant.info.service.MerchantInfoService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 商家管理控制层
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年9月27日 下午1:25:19
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping(path = "/merchant/merchantInfo", method = RequestMethod.POST)
public class MerchantInfoController {

    @Autowired
    private MerchantInfoService merchantInfoService;

    @Autowired
    private BackendCategoryApi backendCategoryApi;

    /**
     * Description： 添加商家信息。
     * Author: Anthony
     * param: dto 商家信息dto
     * <p>
     * param: result 绑定的结果异常
     * <p>
     * throws: GlobalException 全局统一异常类
     * <p>
     * return: saveInteger 执行成功或失败的提示信息
     */
    @RequestMapping(path = "/insertMerchantInfo")
    public ResponseInfo addMerchantInfo(@RequestBody @Valid MerchantInfoDto merchantInfoDto, BindingResult result) throws GlobalException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        return new ResponseInfo(merchantInfoService.saveMerchantInfo(merchantInfoDto));
    }

    /**
     * Description： 商家信息列表分页检索
     * <p>
     * Author: Anthony
     * <p>
     * param : page 分页参数
     * <p>
     * param : merchantInfoDto 商家信息Dto
     * <p>
     * return : list 商家信息列表数据
     * <p>
     * throws : GlobalException 全局统一异常类
     */

    @RequestMapping(path = "/getMerchantInfoList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家信息列表分页检索")
    public ResponseInfo getMerchantInfoList(@RequestBody MerchantInfoListDto merchantInfoListDto)
            throws GlobalException {
        int pageNo = merchantInfoListDto.getPageNo();
        int pageSize = merchantInfoListDto.getPageSize();
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(pageNo);
        pagePO.setPageSize(pageSize);
        return new ResponseInfo(merchantInfoService.getMerchantInfoList(pagePO, merchantInfoListDto));
    }

    /**
     * Description：删除商家信息(冻结)。
     * <p>
     * Author: Anthony
     * <p>
     * param: id 商家信息id
     * <p>
     * return : 执行成功与否信息参数
     * <p>
     * throws : GlobalException 全局异常类
     */
    @RequestMapping(path = "/deleteMerchantInfoById")
    @PermissionController(value = PermitType.PLATFORM, operationName = "冻结商家信息")
    public ResponseInfo cancelMerchantInfoById(@RequestBody MerchantFrozenDto merchantFrozenDto) throws GlobalException {
        return new ResponseInfo(merchantInfoService.removeMerchantInfoById(merchantFrozenDto));
    }

    /**
     * Description：编辑(修改)商家信息。
     * <p>
     * Author: Anthony
     * <p>
     * param : dto 商家信息dto对象
     * <p>
     * param : result 绑定异常结果集
     * <p>
     * return : 执行成功与否的信息
     * <p>
     * throws : GlobalException 全局异常类
     */
    @RequestMapping(path = "updateMerchantInfoById")
    @PermissionController(value = PermitType.PLATFORM, operationName = "修改商家信息")
    public ResponseInfo editMerchantInfoById(@RequestBody @Valid MerchantInfoDto dto, BindingResult result) throws GlobalException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        // 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象。
        return new ResponseInfo(merchantInfoService.modifyMerchantInfoById(dto));
    }

    /**
     * Description：查看商家详情(根据Id查看商家详情数据回显)。
     * <p>
     * Author: Anthony
     * <p>
     * param : id 商家信息id
     * <p>
     * return : 商家详情 dto对象
     * <p>
     * throws : GlobalException 全局异常类
     */
    @RequestMapping(path = "/getMerchantParticulaById/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "查看商家详情")
    public ResponseInfo getMerchantParticularsByIrsd(@PathVariable Long id) throws GlobalException {
        List<Integer> statuses = new ArrayList<>();
        statuses.add(GoodsConstants.BackendMerchantCategoryStatus.AUDOT_APPROVAL.getCode());
        //根据商家id查询审核完成的类目
        List<BackendCategorysDTO> backendCategoryDtoByBelongStore = backendCategoryApi.getMerchantCategory(id, statuses);
        MerchantInfoDto merchantParticularsById = merchantInfoService.getMerchantParticularsById(id);
        if (backendCategoryDtoByBelongStore != null) {
            merchantParticularsById.setBackendCategorysDtos(backendCategoryDtoByBelongStore);
        }
        return new ResponseInfo(merchantParticularsById);
    }

    /**
     * Description：查看门店信息(根据Id查看门店信息数据回显)无用
     * <p>
     * Author: Anthony
     * <p>
     * param : id 门店信息id
     * <p>
     * return : 门店信息实体类对象
     * <p>
     * throws : GlobalException 全局异常类
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "查看门店信息")
    @RequestMapping(path = "/getStoreInfoById/{merchantId}")
    public ResponseInfo getStoreInfoById(@PathVariable Long merchantId) throws GlobalException {
        return new ResponseInfo(merchantInfoService.getStoreInfoById(merchantId));

    }

    /**
     * Description： 商户类型下拉框展示
     * <p>
     * Author: Anthony
     * <p>
     * return : MerchantType 商户类型的名称和对应的id
     */
    @RequestMapping(path = "/getMerchantTypeList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "商户类型列表")
    public ResponseInfo getMerchantTypeList() throws GlobalException {
        return new ResponseInfo(merchantInfoService.getMerchantType());
    }

    /**
     * Description：获取店铺信息
     * <p>
     * Author: Anthony
     * <p>
     * return : StoreInfoByName
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "获取店铺信息")
    @RequestMapping(path = "/getStoreInfoList")
    public ResponseInfo getStoreInfoList(@RequestBody MerchantInfoDto dto) throws GlobalException {
        return new ResponseInfo(merchantInfoService.getStoreInfoByName(dto));

    }

    /**
     * Description：商家等级设置。
     * <p>
     * Author: Anthony
     * <p>
     * param : id 商家id , merchantGradeId 商家等级id
     * <p>
     * param : result 绑定异常结果集
     * <p>
     * return : 执行成功与否的信息
     * <p>
     * throws : GlobalException 全局异常类
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家等级设置")
    @RequestMapping(path = "/updateMerchantInfoByMerchantGradeId")
    @ResponseBody
    public ResponseInfo editMerchantInfoByMerchantGradeId(@RequestBody MerchantInfoGradeDto merchantInfoGradeDto) throws GlobalException {

        return new ResponseInfo(merchantInfoService.modifyMerchantInfoByMerchantGradeId(merchantInfoGradeDto));
    }


    /**
     * Description： 商户类型下拉框
     * <p>
     * Author: Anthony
     * <p>
     * return 商家信息Dto
     * <p>
     * throws GlobalException
     */
    @RequestMapping(path = "selectMerchantInfoByMerchantType")
    @ResponseBody
    @PermissionController(value = PermitType.PLATFORM, operationName = "商户类型列表")
    public ResponseInfo getMerchantInfoByMerchantType() throws GlobalException {
        return new ResponseInfo(merchantInfoService.getMerchantInfoByMerchantType());
    }

    /**
     * Description： 所属商家。
     * <p>
     * Author: Anthony
     *
     * @return
     */
    @RequestMapping(path = "getMerchantInfoByName")
    @ResponseBody
    @PermissionController(value = PermitType.PLATFORM, operationName = "所属商家")
    public ResponseInfo getMerchantInfoByName(@RequestBody MerchantInfoDto merchantInfoDto) {
        return new ResponseInfo(merchantInfoService.getMerchantInfoByName(merchantInfoDto));
    }

    /**
     * Description： 所属店铺。
     * <p>
     * Author: Anthony
     *
     * @return
     */
    @RequestMapping(path = "getStoreInfoListById")
    @ResponseBody
    @PermissionController(value = PermitType.PLATFORM, operationName = "所属店铺")
    public ResponseInfo selectStoreInfoList(@RequestBody StoreInfoDto storeInfoDto) {
        return new ResponseInfo(merchantInfoService.getStoreInfoList(storeInfoDto));
    }


    /**
     * 修改商家名称与商家头像
     *
     * @param merchantInfoDto
     * @param result
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "修改商家名称与商家头像")
    @RequestMapping(path = "updateMerchantStoreNameById")
    public ResponseInfo editMerchantStoreNameById(@RequestBody @Valid MerchantInfoDto merchantInfoDto, BindingResult result)
            throws GlobalException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        // 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象。
        return new ResponseInfo(merchantInfoService.modiMerchantInfoById(merchantInfoDto));
    }

}
