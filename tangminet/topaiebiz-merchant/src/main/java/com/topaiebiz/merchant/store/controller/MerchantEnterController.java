package com.topaiebiz.merchant.store.controller;

import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.goods.api.BackendCategoryApi;
import com.topaiebiz.goods.constants.GoodsConstants;
import com.topaiebiz.goods.dto.category.backend.BackendCategorysDTO;
import com.topaiebiz.member.api.MemberApi;
import com.topaiebiz.merchant.enter.dto.*;
import com.topaiebiz.merchant.enter.exception.MerchantEnterException;
import com.topaiebiz.merchant.enter.service.MerchantEnterService;
import com.topaiebiz.merchant.freight.dto.AddFreightTempleteDto;
import com.topaiebiz.merchant.freight.dto.MerFreightTempleteDto;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

/**
 * Description: 商家入驻流程控制层
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年10月9日 上午11:04:02
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping(path = "/merchant/merchantEnter",method = RequestMethod.POST)
public class MerchantEnterController {

    @Autowired
    private MerchantEnterService merchantEnterService;

    @Autowired
    private MemberApi memberApi;

    @Autowired
    private BackendCategoryApi backendCategoryApi;


    /**
     * Description：商家入驻流程--基本信息填写（公司及联系人信息）
     * <p>
     * Author: Anthony
     * <p>
     * param : dto 商家资质Dto
     * <p>
     * param : result 绑定结果集
     * <p>
     * return : 执行结果参数提示
     * <p>
     * throws GlobalException : 全局异常类
     */
    @RequestMapping(path = "/insertMerchantQualification")
    @PermissionController(value = PermitType.ENTER, operationName = "商家入住添加基本信息")
    public ResponseInfo addMerchantQualification(HttpSession session, @RequestBody @Valid MerchantBasicInfoDto merchantBasicInfoDto, BindingResult result, ServletRequest request)
            throws GlobalException, ParseException {
        HttpServletRequest  req  = (HttpServletRequest) request;
        String userLoginId=  req.getHeader("userLoginId");
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        merchantEnterService.saveMerchantQualification(merchantBasicInfoDto, session,userLoginId);
        return new ResponseInfo();
    }

    /**
     * Description：修改基本信息
     * <p>
     * Author: Anthony
     * <p>
     * param merchantBasicInfoDto 基本信息Dto参数
     */
    @RequestMapping(path = "/updateMerchantQualificationById")
    @PermissionController(value = PermitType.ENTER, operationName = "商家入住修改基本信息")
    public ResponseInfo editMerchantQualificationById( @RequestBody  MerchantBasicInfoDto merchantBasicInfoDto) {
        merchantEnterService.modifyMerchantQualificationById(merchantBasicInfoDto);
        return new ResponseInfo();
    }

    /**
     * Description： 经营信息(营业执照信息、银行账户信息)添加
     * <p>
     * Author: Anthony
     * <p>
     * param : dto 商家账户信息Dto
     * <p>
     * param : result 绑定结果集
     * <p>
     * return : 执行结果参数提示
     * <p>
     * throws GlobalException : 全局异常类
     */
    @RequestMapping(path = "/insertMerchantAccount")
    @PermissionController(value = PermitType.ENTER, operationName = "商家入住添加经营信息")
    public ResponseInfo addMerchantAccount(@RequestBody @Valid MercahntManageInfoDto mercahntManageInfoDto, BindingResult result,
                                           Long qualificationId) throws GlobalException, ParseException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        merchantEnterService.saveMerchantAccount(mercahntManageInfoDto);
        return new ResponseInfo();
    }

    /**
     * Description： 修改经营信息。
     * <p>
     * Author: Anthony
     * <p>
     * param mercahntManageInfoDto 经营信息dto参数
     */
    @RequestMapping(path = "/updateMerchantManageInfoById")
    @ResponseBody
    @PermissionController(value = PermitType.ENTER, operationName = "商家入住修改经营信息")
    public ResponseInfo editMerchantManageInfoById(@RequestBody MercahntManageInfoDto mercahntManageInfoDto) throws ParseException {
        merchantEnterService.modifyMerchantInfoById(mercahntManageInfoDto);
        return new ResponseInfo();
    }

    /**
     * Description：商家入驻流程--在线经营范围（店铺经营信息）添加类目
     * <p>
     * Author: Anthony
     * <p>
     * param : dto 店铺经营信息Dto
     * <p>
     * param : result 绑定结果集
     * <p>
     * return : 执行结果参数提示
     * <p>
     * throws GlobalException : 全局异常类
     */
    @RequestMapping(path = "/insertStoreInfo")
    @ResponseBody
    @PermissionController(value = PermitType.ENTER, operationName = "商家入住添加经营范围")
    public ResponseInfo addStoreInfo( @RequestBody @Valid StoreInfoDtos storeInfoDto, BindingResult result)
            throws GlobalException, ParseException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        merchantEnterService.addStoreBackendCategoryInfo(storeInfoDto);
        return new ResponseInfo();
    }

    /////////////////////////////////以下回显

    /**
     * Description : 我要查询功能，信息审核，根据商家的入驻状态回显审核结果(信息查询)
     * <p>
     * Author: Anthony
     * <p>
     * param username 账号
     */
    @RequestMapping(path = "/getMerchantInfoStateByLoginName")
    @PermissionController(value = PermitType.ENTER, operationName = "入住查询审核状态")
    public ResponseInfo getMerchantInfoStateByLoginName(@RequestBody  String username) throws GlobalException {
        StateDto stateDto = merchantEnterService.getMerchantInfoStateByLoginName(username);
        return new ResponseInfo(stateDto);
    }

    /**
     * Description：根据商家id回显提交的信息 (公司及联系人信息)
     * <p>
     * Author: Anthony
     * <p>
     * param : loginName 商家的账户
     */
    @RequestMapping(path = "/getMerchantInfoByLoginName")
    @PermissionController(value = PermitType.ENTER, operationName = "回显公司及联系人信息")
    public ResponseInfo getMerchantInfoByLoginName() {
        return new ResponseInfo(merchantEnterService.getMerchantInfoByLoginName());
    }

    /**
     * Description：根据商家id回显商家的提交信息 (经营信息)。
     * <p>
     * Author: Anthony
     * <p>
     * param loginName 商家的账户
     */
    @RequestMapping(path = "/getMercahntManageInfoByLoginName")
    @PermissionController(value = PermitType.ENTER, operationName = "回显商家经营信息")
    public ResponseInfo getMercahntManageInfoByLoginName() {
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        return new ResponseInfo(merchantEnterService.getMercahntManageInfoByLoginName(merchantId));
    }
    /**
     * Description：入驻回显经营类目
     * <p>
     * Author: Aaron
     *
     * @param
     * @return
     */
    @RequestMapping(path = "/getMerchantCategory")
    @PermissionController(value = PermitType.ENTER, operationName = "入驻回显经营类目")
    public ResponseInfo getMerchantCategory() {
        // 根据店铺id查出店铺所选的经营类目
        List<BackendCategorysDTO> backendCategoryDtoByBelongStore = merchantEnterService.getBackendCategoryDtoByBelongStore();
        return new ResponseInfo(backendCategoryDtoByBelongStore);
    }

    /***
     * Description：商家端费用信息回显。
     *
     * Author: Anthony
     *
     * param : id 商家信息id
     */
    @RequestMapping(path = "/getCostInfoByIds")
    @PermissionController(value = PermitType.ENTER, operationName = "商家端费用信息回显")
    public ResponseInfo getCostInfoByIds() {
        // 根据当前用户查出商家id
        Long id = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        return new ResponseInfo(merchantEnterService.getCostInfoById(id));
    }

    /**
     * Description： 上传缴费凭证.
     * <p>
     * Author: Anthony
     * <p>
     * param : capitalDto
     */
    @RequestMapping(path = "/insertPayImage")
    @PermissionController(value = PermitType.ENTER, operationName = "上传缴费凭证")
    public ResponseInfo addPayImage(@RequestBody  CapitalDto capitalDto) {
        merchantEnterService.savePayImage(capitalDto);
        return new ResponseInfo();
    }

    /**
     * Description：商家不通过原因回显(我要查询)的操作。
     * <p>
     * Author: Anthony
     *
     * @return
     */
    @RequestMapping(path = "/selectMerchantAuditByMerchantId")
    @PermissionController(value = PermitType.ENTER, operationName = "商家不通过原因回显")
    public ResponseInfo selectMerchantAuditByMerchantId() {
        // 根据当前用户查出商家id
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        return new ResponseInfo(merchantEnterService.selectMerchantAuditByMerchantId(merchantId));
    }


    ///////////////////////////////////////查询

    /**
     * Description：根据商家id回显商家的提交信息 (在线经营范围)。
     * <p>
     * Author: Anthony
     * <p>
     * param : loginName 商家的账户
     */
    @RequestMapping(path = "/getMerchantTypeByLoginName")
    @PermissionController(value = PermitType.ENTER, operationName = "根据商家id回显商家的提交信息")
    public ResponseInfo getMerchantTypeByLoginName() {
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        StoreInfoDto storeInfoDto = merchantEnterService.getMerchantTypeByLoginName(merchantId);
        //Long merchantIds = storeInfoDto.getMerchantId();
      //  List<BackendCategorysDTO> backendCategoryDtoByBelongStore = backendCategoryApi.getMerchantCategory(storeInfoDto.getMerchantId());
        List<BackendCategorysDTO> backendCategoryDtoByBelongStore = backendCategoryApi.getMerchantBacCategory(storeInfoDto.getMerchantId());
        storeInfoDto.setBackendCategorysDtos(backendCategoryDtoByBelongStore);
        return new ResponseInfo(storeInfoDto);
    }


    /***
     * Description：平台端费用信息回显。
     *
     * Author: Anthony
     *
     * param : id 商家信息id
     */
    @RequestMapping(path = "/getCostInfoById/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台端费用信息回显")
    public ResponseInfo getCostInfoById(@PathVariable  Long id) {

        return new ResponseInfo(merchantEnterService.getCostInfoById(id));
    }

    /**
     * Description： 创建店铺后店铺信息的回显。
     * <p>
     * Author: Anthony
     * <p>
     * param : id 店铺id
     * <p>
     * return : 店铺信息
     */
    @RequestMapping(path = "/selectStoreInfoById/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "创建店铺后店铺信息的回显")
    public ResponseInfo getStoreInfoById(@PathVariable Long id) {
        return new ResponseInfo(merchantEnterService.getStoreInfoById(id));
    }

    /**
     * Description：平台端点击审核通过商家提交并且修改商家入驻状态。
     * <p>
     * Author: Anthony
     *
     * @param examineStateDto
     * @return
     */
    @RequestMapping(path = "/updateMerchantInfoStateById", method = RequestMethod.POST)
    @ResponseBody
    @PermissionController(value = PermitType.PLATFORM, operationName = "点击审核通过商家提交并且修改商家入驻状态")
    public ResponseInfo updateMerchantInfoStateById(HttpServletRequest request, @RequestBody  ExamineStateDto examineStateDto) {
        String userLoginId = request.getHeader("userLoginId");
        merchantEnterService.updateMerchantInfoStateById(userLoginId, examineStateDto);
        return new ResponseInfo();
    }

    /**
     * Description：点击不通过修改商家入驻状态。无用
     * <p>
     * Author: Anthony
     *
     * @param id
     * @return
     */
    @RequestMapping(path = "/updateMerchantInfoStateByMerchantId/{id}")
    public ResponseInfo updateMerchantInfoStateByMerchantId(@PathVariable Long id) {
        merchantEnterService.updateMerchantInfoStateByMerchantId(id);
        return new ResponseInfo();
    }

    /**
     * Description：点击通过修改商家入驻状态。
     * <p>
     * Author: Anthony
     *
     * @param id
     * @return
     */
    @RequestMapping(path = "/updateStateByMerchantId")
    @PermissionController(value = PermitType.PLATFORM, operationName = "点击通过修改商家入驻状态")
    public ResponseInfo updateStateByMerchantId(@PathVariable Long id) {
        merchantEnterService.updateStateByMerchantId(id);
        return new ResponseInfo();
    }

    /**
     * Description： 平台端缴费
     * <p>
     * Author: Anthony
     * <p>
     * param : capitalDto 缴费信息
     */
    @RequestMapping(path = "/insertPaymentPrice")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台端缴费")
    public ResponseInfo addPaymentPrice(@RequestBody  CapitalDto capitalDto) {
        merchantEnterService.savePaymentPrice(capitalDto);
        return new ResponseInfo();
    }

    @RequestMapping(path = "/echoPaymentPrice/{merchantId}")
    @PermissionController(value = PermitType.PLATFORM,operationName = "平台端回显缴纳费用")
    public ResponseInfo echoPaymentPrice(@PathVariable Long merchantId){
        CostInfoDto costInfoById = merchantEnterService.getCostInfoById(merchantId);
        return new ResponseInfo(costInfoById);
    }

    /**
     * Description : 商家资质信息分页检索。
     * <p>
     * Author : Anthony
     * <p>
     * param : page 分页参数
     * <p>
     * param : merchantQualificationDto 商家资质Dto对象
     * <p>
     * return : 商家资质List
     */
    @RequestMapping(path = "/getMerchantQualificationList", method = RequestMethod.POST)
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家资质信息分页检索")
    public ResponseInfo getMerchantQualificationList(@RequestBody  MerchantQualificationDto merchantQualificationDto) {
        int pageNo = merchantQualificationDto.getPageNo();
        int pageSize = merchantQualificationDto.getPageSize();
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(pageNo);
        pagePO.setPageSize(pageSize);
        return new ResponseInfo(merchantEnterService.getMerchantQualificationList(pagePO, merchantQualificationDto));
    }

    /**
     * Description：商家基本信息回显
     * <p>
     * Author: Anthony
     * <p>
     * param : id 商家资质信息id
     * <p>
     * return : 商家资质信息数据
     * <p>
     * throws : GlobalException 全局异常类
     */
    @RequestMapping(path = "/getMerchantQualificationListById/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家基本信息回显")
    public ResponseInfo getMerchantParticularsBymerchantId(@PathVariable Long id) throws GlobalException {
        return new ResponseInfo(merchantEnterService.selectMerchantBasicInfOById(id));
    }

    /**
     * 入驻管线基本信息
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/getMerchantQualificationListById")
    @PermissionController(value = PermitType.ENTER, operationName = "商家基本信息回显")
    public ResponseInfo getMerchantParticularsByIrsd() throws GlobalException {
        Long id = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        return new ResponseInfo(merchantEnterService.selectMerchantBasicInfOById(id));
    }
    /**
     * Description：经营类目信息回显。
     * <p>
     * Author: Anthony
     *
     * @param
     * @return
     */
    @RequestMapping(path = "/getMerchantManageInfoById")
    @PermissionController(value = PermitType.ENTER, operationName = "经营类目信息回显")
    public ResponseInfo getMerchantManageInfoById() {
        MerchantManageDto merchantManageDto = new MerchantManageDto();
        // 根据店铺id查出店铺所选的经营类目
        Long id = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        //List<BackendCategorysDTO> backendCategoryDtoByBelongStore =backendCategoryApi.getMerchantCategory(id);
        List<BackendCategorysDTO> backendCategoryDtoByBelongStore = backendCategoryApi.getMerchantBacCategory(id);
        merchantManageDto = merchantEnterService.getMerchantManageInfoById(id);
        merchantManageDto.setBackendCategorysDtos(backendCategoryDtoByBelongStore);
        merchantManageDto.setImgages(merchantManageDto.getImgages());
        return new ResponseInfo(merchantManageDto);
    }

    @RequestMapping(path = "/getMerchantManageInfoById/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "经营类目信息回显")
    public ResponseInfo getMerchantManageInfoByMerchantId(@PathVariable Long id) {
        MerchantManageDto merchantManageDto = new MerchantManageDto();
        // 根据店铺id查出店铺所选的经营类目(待审核状态)
     //   List<BackendCategorysDTO> backendCategoryDtoByBelongStore = backendCategoryApi.getMerchantCategory(id, GoodsConstants.BackendMerchantCategory.);
        List<BackendCategorysDTO> backendCategoryDtoByBelongStore = backendCategoryApi.getMerchantBacCategory(id);
        merchantManageDto = merchantEnterService.getMerchantManageInfoById(id);
        merchantManageDto.setBackendCategorysDtos(backendCategoryDtoByBelongStore);
        merchantManageDto.setImgages(merchantManageDto.getImgages());
        return new ResponseInfo(merchantManageDto);
    }

    /**
     * Description：审核商家基本信息并保存不通过原因及不通过字段(平台端点击审核完成)
     * <p>
     * Author: Anthony
     * <p>
     * param : merchantauditLogDto 商家入驻审核记录Dto对象
     * <p>
     * param : result 绑定的结果集
     * <p>
     * return : 执行结果参数
     * <p>
     * throws : GlobalException 全局异常类
     */
    @RequestMapping(path = "/insertMerchantauditLog")
    public ResponseInfo addMerchantauditLog(@RequestBody MerchantauditLogDto MerchantauditLogDto, BindingResult result)
            throws GlobalException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result
            );
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        merchantEnterService.saveMerchantAuditLogAndDetail(MerchantauditLogDto);
        return new ResponseInfo();
    }

    /**
     * Description： 6.信息审核（审核记录）
     * <p>
     * Author Aaron.Xue
     *
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/selectMerchantauditLog")
    @PermissionController(value = PermitType.ENTER, operationName = "审核记录")
    public ResponseInfo selectMerchantauditLog() throws GlobalException {
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        List<MerchantauditLogDto> list = merchantEnterService.selectMerchantauditLog(merchantId);
        return new ResponseInfo(list);
    }

    /**
     * Description：不通过详情
     * <p>
     * Author: Anthony
     *
     * @return
     */
    @RequestMapping(path = "/selectMerchantauditLogDetail/{id}")
    @PermissionController(value = PermitType.ENTER, operationName = "审核记录")
    public ResponseInfo selectMerchantauditLogDetail(@PathVariable Long id) {
        // 根据当前用户查出商家id
        return new ResponseInfo(merchantEnterService.getMerchantauditLogDetail(id));
    }
    /**
     * 平台端回显审核不通过详情
     * @param merchantId
     * @return
     */
    @RequestMapping(path = "/selectPalmMerchantauditLogDetail/{merchantId}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台端回显审核记录")
    public ResponseInfo selectPalmMerchantauditLogDetail(@PathVariable Long merchantId){
        //获取当前账户
        return new ResponseInfo (merchantEnterService.getPalmMerchantauditLogDetail(merchantId));
    }

}
